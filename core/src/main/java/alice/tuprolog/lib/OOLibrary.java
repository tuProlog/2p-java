/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprolog.lib;

import alice.tuprolog.Double;
import alice.tuprolog.Float;
import alice.tuprolog.Long;
import alice.tuprolog.Number;
import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidObjectIdException;
import alice.tuprolog.exceptions.JavaException;
import alice.tuprolog.lib.annotations.OOLibraryEnableLambdas;
import alice.util.AbstractDynamicClassLoader;
import alice.util.AndroidDynamicClassLoader;
import alice.util.InspectionUtils;
import alice.util.JavaDynamicClassLoader;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static alice.util.Tools.loadText;
import static alice.util.Tools.removeApices;

/**
 * This class represents a tuProlog library enabling the interaction with the
 * Java environment from tuProlog.
 * <p>
 * Warning we use the setAccessible method
 * <p>
 * The most specific method algorithm used to find constructors / methods has
 * been inspired by the article "What Is Interactive Scripting?", by Michael
 * Travers Dr. Dobb's -- Software Tools for the Professional Programmer January
 * 2000 CMP Media Inc., a United News and Media Company
 * <p>
 * Library/Theory Dependency: BasicLibrary
 */
@SuppressWarnings("serial")
@OOLibraryEnableLambdas(mode = "active") //Alberto
public class OOLibrary extends Library {

    /**
     * java objects referenced by prolog terms (keys)
     */
    private HashMap<String, Object> currentObjects = new HashMap<String, Object>();
    /**
     * inverse map useful for implementation issue
     */
    private IdentityHashMap<Object, Struct> currentObjects_inverse = new IdentityHashMap<Object, Struct>();

    private HashMap<String, Object> staticObjects = new HashMap<String, Object>();
    private IdentityHashMap<Object, Struct> staticObjects_inverse = new IdentityHashMap<Object, Struct>();

    /**
     * progressive counter used to identify registered objects
     */
    private int id = 0;
    /**
     * progressive counter used to generate lambda function dinamically
     */
    private int counter = 0;

    private OOLibraryEnableLambdas lambdaPlugin;

    /**
     * @author Alessio Mercurio
     * <p>
     * used to manage different classloaders.
     */
    private AbstractDynamicClassLoader dynamicLoader;

    /**
     * library theory
     */

    public OOLibrary() {
        Class<OOLibrary> ooLibrary = OOLibrary.class;
        lambdaPlugin = ooLibrary.getAnnotation(OOLibraryEnableLambdas.class);

        if (System.getProperty("java.vm.name").equals("Dalvik")) {
            dynamicLoader = new AndroidDynamicClassLoader(new URL[]{}, getClass().getClassLoader());
        } else {
            dynamicLoader = new JavaDynamicClassLoader(new URL[]{}, getClass().getClassLoader());
        }
    }

    private static Method lookupMethod(Class<?> target, String name, Class<?>[] argClasses, Object[] argValues)
            throws NoSuchMethodException {
        // first try for exact match
        try {
            return target.getMethod(name, argClasses);
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of
                // luck
                return null;
            }
        }

        // go the more complicated route
        Method[] methods = target.getMethods();
        Vector<Method> goodMethods = new Vector<Method>();
        for (int i = 0; i != methods.length; i++) {
            if (name.equals(methods[i].getName())
                && matchClasses(methods[i].getParameterTypes(), argClasses)) {
                goodMethods.addElement(methods[i]);
            }
        }
        switch (goodMethods.size()) {
            case 0:
                // no methods have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                // double --> float
                // (the first found is used - no most specific
                // method algorithm is applied )

                for (int i = 0; i != methods.length; i++) {
                    if (name.equals(methods[i].getName())) {
                        Class<?>[] types = methods[i].getParameterTypes();
                        Object[] val = matchClasses(types, argClasses, argValues);
                        if (val != null) {
                            // found a method compatible
                            // after type conversions
                            for (int j = 0; j < types.length; j++) {
                                argClasses[j] = types[j];
                                argValues[j] = val[j];
                            }
                            return methods[i];
                        }
                    }
                }

                return null;
            case 1:
                return goodMethods.firstElement();
            default:
                return mostSpecificMethod(goodMethods);
        }
    }

    private static Constructor<?> lookupConstructor(Class<?> target, Class<?>[] argClasses, Object[] argValues)
            throws NoSuchMethodException {
        // first try for exact match
        try {
            return target.getConstructor(argClasses);
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of
                // luck
                return null;
            }
        }

        // go the more complicated route
        Constructor<?>[] constructors = target.getConstructors();
        Vector<Constructor<?>> goodConstructors = new Vector<Constructor<?>>();
        for (int i = 0; i != constructors.length; i++) {
            if (matchClasses(constructors[i].getParameterTypes(), argClasses)) {
                goodConstructors.addElement(constructors[i]);
            }
        }
        switch (goodConstructors.size()) {
            case 0:
                // no constructors have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                // double --> float
                // (the first found is used - no most specific
                // method algorithm is applied )

                for (int i = 0; i != constructors.length; i++) {
                    Class<?>[] types = constructors[i].getParameterTypes();
                    Object[] val = matchClasses(types, argClasses, argValues);
                    if (val != null) {
                        // found a method compatible
                        // after type conversions
                        for (int j = 0; j < types.length; j++) {
                            argClasses[j] = types[j];
                            argValues[j] = val[j];
                        }
                        return constructors[i];
                    }
                }

                return null;
            case 1:
                return goodConstructors.firstElement();
            default:
                return mostSpecificConstructor(goodConstructors);
        }
    }

    // 1st arg is from method, 2nd is actual parameters
    private static boolean matchClasses(Class<?>[] mclasses, Class<?>[] pclasses) {
        if (mclasses.length == pclasses.length) {
            for (int i = 0; i != mclasses.length; i++) {
                if (!matchClass(mclasses[i], pclasses[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean matchClass(Class<?> mclass, Class<?> pclass) {
        boolean assignable = mclass.isAssignableFrom(pclass);
        if (assignable) {
            return true;
        } else {
            return mclass.equals(java.lang.Long.TYPE) && (pclass.equals(Integer.TYPE));
        }
    }

    private static Method mostSpecificMethod(Vector<Method> methods)
            throws NoSuchMethodException {
        for (int i = 0; i != methods.size(); i++) {
            for (int j = 0; j != methods.size(); j++) {
                if ((i != j)
                    && (moreSpecific(methods.elementAt(i),
                                     methods.elementAt(j)))) {
                    methods.removeElementAt(j);
                    if (i > j) {
                        i--;
                    }
                    j--;
                }
            }
        }
        if (methods.size() == 1) {
            return methods.elementAt(0);
        } else {
            throw new NoSuchMethodException(">1 most specific method");
        }
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Method c1, Method c2) {
        Class<?>[] p1 = c1.getParameterTypes();
        Class<?>[] p2 = c2.getParameterTypes();
        int n = p1.length;
        for (int i = 0; i != n; i++) {
            if (!matchClass(p2[i], p1[i])) {
                return false;
            }
        }
        return true;
    }

    private static Constructor<?> mostSpecificConstructor(Vector<Constructor<?>> constructors)
            throws NoSuchMethodException {
        for (int i = 0; i != constructors.size(); i++) {
            for (int j = 0; j != constructors.size(); j++) {
                if ((i != j)
                    && (moreSpecific(constructors.elementAt(i)
                        , constructors.elementAt(j)))) {
                    constructors.removeElementAt(j);
                    if (i > j) {
                        i--;
                    }
                    j--;
                }
            }
        }
        if (constructors.size() == 1) {
            return constructors.elementAt(0);
        } else {
            throw new NoSuchMethodException(">1 most specific constructor");
        }
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Constructor<?> c1, Constructor<?> c2) {
        Class<?>[] p1 = c1.getParameterTypes();
        Class<?>[] p2 = c2.getParameterTypes();
        int n = p1.length;
        for (int i = 0; i != n; i++) {
            if (!matchClass(p2[i], p1[i])) {
                return false;
            }
        }
        return true;
    }

    // Checks compatibility also considering explicit type conversion.
    // The method returns the argument values, since they could be changed
    // after a type conversion.
    //
    // In particular the check must be done for the DEFAULT type of tuProlog,
    // that are int and double; so
    // (required X, provided a DEFAULT -
    // with DEFAULT to X conversion 'conceivable':
    // for instance *double* to *int* is NOT considered good
    //
    // required a float, provided an int OK
    // required a double, provided a int OK
    // required a long, provided a int ==> already considered by
    // previous match test
    // required a float, provided a double OK
    // required a int, provided a double => NOT CONSIDERED
    // required a long, provided a double => NOT CONSIDERED
    //
    private static Object[] matchClasses(Class<?>[] mclasses, Class<?>[] pclasses,
                                         Object[] values) {
        if (mclasses.length == pclasses.length) {
            Object[] newvalues = new Object[mclasses.length];

            for (int i = 0; i != mclasses.length; i++) {
                boolean assignable = mclasses[i].isAssignableFrom(pclasses[i]);
                if (assignable
                    || (mclasses[i].equals(java.lang.Long.TYPE) && pclasses[i]
                        .equals(java.lang.Integer.TYPE))) {
                    newvalues[i] = values[i];
                } else if (mclasses[i].equals(java.lang.Float.TYPE)
                           && pclasses[i].equals(java.lang.Double.TYPE)) {
                    // arg required: a float, arg provided: a double
                    // so we need an explicit conversion...
                    newvalues[i] = ((java.lang.Double) values[i]).floatValue();
                } else if (mclasses[i].equals(java.lang.Float.TYPE)
                           && pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a float, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = (float) (Integer) values[i];
                } else if (mclasses[i].equals(java.lang.Double.TYPE)
                           && pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a double, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = ((Integer) values[i]).doubleValue();
                } else if (values[i] == null && !mclasses[i].isPrimitive()) {
                    newvalues[i] = null;
                } else {
                    return null;
                }
            }
            return newvalues;
        } else {
            return null;
        }
    }

    private static final String THEORY;

    static {
        try {
            THEORY = loadText(OOLibrary.class.getResourceAsStream(OOLibrary.class.getSimpleName() + ".pl"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getTheory() {
        return THEORY;
    }

    public void dismiss() {
        currentObjects.clear();
        currentObjects_inverse.clear();
    }

    public void dismissAll() {
        currentObjects.clear();
        currentObjects_inverse.clear();
        staticObjects.clear();
        staticObjects_inverse.clear();
    }

    public void onSolveBegin(Term goal) {
        currentObjects.clear();
        currentObjects_inverse.clear();
        for (Map.Entry<Object, Struct> en : staticObjects_inverse.entrySet()) {
            bindDynamicObject(en.getValue(), en.getKey());
        }
        preregisterObjects();
    }

    public void onSolveEnd() {
    }

    /**
     * objects actually pre-registered in order to be available since the
     * beginning of demonstration
     */
    protected void preregisterObjects() {
        try {
            bindDynamicObject(Struct.atom("stdout"), System.out);
            bindDynamicObject(Struct.atom("stderr"), System.err);
            bindDynamicObject(Struct.atom("runtime"), Runtime.getRuntime());
            bindDynamicObject(Struct.atom("current_thread"), Thread.currentThread());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Deprecated from tuProlog 3.0 use new_object
     */
    public boolean java_object_3(Term className, Term argl, Term id) throws JavaException {
        return new_object_3(className, argl, id);
    }

    /**
     * Creates of a java object - not backtrackable case
     *
     * @param className
     * @param argl
     * @param id
     * @return
     * @throws JavaException
     */
    public boolean new_object_3(Term className, Term argl, Term id) throws JavaException {
        className = className.getTerm();
        Struct arg = (Struct) argl.getTerm();
        id = id.getTerm();
        if (!className.isAtom()) {
            throw new JavaException(new ClassNotFoundException("Java class not found: " + className));
        }
        String clName = ((Struct) className).getName();
        // check for array type
        if (clName.endsWith("[]")) {
            Object[] list = getArrayFromList(arg);
            int nargs = ((Number) list[0]).intValue();
            if (java_array(clName, nargs, id)) {
                return true;
            } else {
                throw new JavaException(new Exception());
            }
        }
        Signature args = parseArg(getArrayFromList(arg));
        if (args == null) {
            throw new IllegalArgumentException(
                    "Illegal constructor arguments  " + arg);
        }
        // object creation with argument described in args
        try {
            Class<?> cl = Class.forName(clName, true, dynamicLoader);
            Object[] args_value = args.getValues();
            Constructor<?> co = lookupConstructor(cl, args.getTypes(), args_value);
            if (co == null) {
                getEngine().warn("Constructor not found: class " + clName);
                throw new JavaException(new NoSuchMethodException("Constructor not found: class " + clName));
            }

            Object obj = co.newInstance(args_value);
            if (bindDynamicObject(id, obj)) {
                return true;
            } else {
                throw new JavaException(new Exception());
            }
        } catch (JavaException ex) {
            throw ex;
        } catch (ClassNotFoundException ex) {
            getEngine().warn("Java class not found: " + clName);
            throw new JavaException(ex);
        } catch (InvocationTargetException ex) {
            getEngine().warn("Invalid constructor arguments.");
            throw new JavaException(ex);
        } catch (NoSuchMethodException ex) {
            getEngine().warn("Constructor not found: " + Arrays.toString(args.getTypes()));
            throw new JavaException(ex);
        } catch (InstantiationException ex) {
            getEngine().warn("Objects of class " + clName + " cannot be instantiated");
            throw new JavaException(ex);
        } catch (IllegalArgumentException ex) {
            getEngine().warn("Illegal constructor arguments  " + args);
            throw new JavaException(ex);
        } catch (IllegalAccessException e) {
            getEngine().warn(
                    "Illegal access exception in calling constructor for class " + clName + " with arguments " + args);
            throw new JavaException(e);
        } catch (Exception ex) {
            throw new JavaException(ex);
        }
    }

    /**
     * @param interfaceName  represent the name of the target interface i.e. {@link java.util.function.Predicate}
     * @param implementation contains the function implementation i.e. <code>s -&gt; s.length() &gt; 4</code>
     * @param id             represent the <code>identification_name</code> of the created object function i.e. MyLambda
     * @throws JavaException
     * @throws Exception
     * @author Roberta Calegari
     * <p>
     * Creates of a lambda object - not backtrackable case
     */
    @SuppressWarnings("unchecked") //Modificato da Alberto
    public <T> boolean new_lambda_3(Term interfaceName, Term implementation, Term id) throws JavaException, Exception {
        if (lambdaPlugin != null) {
            String mode = lambdaPlugin.mode();
            if (mode.equalsIgnoreCase("active")) {
                try {
                    counter++;
                    String target_class = (interfaceName.toString()).substring(1,
                                                                               interfaceName.toString().length() - 1);
                    String lambda_expression = (implementation.toString()).substring(1, implementation.toString()
                                                                                                      .length() - 1);
                    target_class = StringEscapeUtils.unescapeJava(target_class);
                    lambda_expression = StringEscapeUtils.unescapeJava(lambda_expression);

                    Class<?> lambdaMetaFactory = alice.util.proxyGenerator.Generator.make(
                            ClassLoader.getSystemClassLoader(),
                            "MyLambdaFactory" + counter,
                            "" +
                            "public class MyLambdaFactory" + counter + " {\n" +
                            "  public " + target_class + " getFunction() {\n" +
                            " 		return " + lambda_expression + "; \n" +
                            "  }\n" +
                            "}\n"
                    );

                    Object myLambdaFactory = lambdaMetaFactory.newInstance();
                    Class<?> myLambdaClass = myLambdaFactory.getClass();
                    Method[] allMethods = myLambdaClass.getDeclaredMethods();
                    T myLambdaInstance = null;
                    for (Method m : allMethods) {
                        String mname = m.getName();
                        if (mname.startsWith("getFunction")) {
                            myLambdaInstance = (T) m.invoke(myLambdaFactory);
                        }
                    }
                    id = id.getTerm();
                    if (bindDynamicObject(id, myLambdaInstance)) {
                        return true;
                    } else {
                        throw new JavaException(new Exception());
                    }
                } catch (Exception ex) {
                    throw new JavaException(ex);
                }
            }
        }
        return false;
    }

    /**
     * Destroy the link to a java object - called not directly, but from
     * predicate java_object (as second choice, for backtracking)
     *
     * @throws JavaException
     */
    public boolean destroy_object_1(Term id) throws JavaException {
        id = id.getTerm();
        try {
            if (id.isGround()) {
                unregisterDynamic((Struct) id);
            }
            return true;
        } catch (Exception ex) {
            throw new JavaException(ex);
        }
    }

    /**
     * Deprecated from tuProlog 3.0 use new_class
     *
     * @throws JavaException
     */
    public boolean java_class_4(Term clSource, Term clName, Term clPathes, Term id) throws JavaException {
        return new_class_4(clSource, clName, clPathes, id);
    }

    private File getJavaHomePath() {
        return new File(System.getProperty("java.home"));
    }

    private File getJavaBinPath() {
        return new File(getJavaHomePath(), "bin");
    }

    private boolean isWindows() {
        final String osName = System.getProperty("os.name");
        return osName.contains("Windows") || osName.contains("windows");
    }

    private File getJavaCPath() {
        return new File(getJavaBinPath(), "javac" + (isWindows() ? ".exe" : ""));
    }

    private File getTempFile(String file) {
        return new File(System.getProperty("java.io.tmpdir"), file);
    }

    /**
     * The java class/4 creates, compiles and loads a new Java class from a source text
     *
     * @param clSource: is a string representing the text source of the new Java class
     * @param clName:   full class name
     * @param clPathes: is a (possibly emptyWithStandardOperators) Prolog list of class paths that may be required for a successful dynamic compilation of this class
     * @param id:       reference to an instance of the meta-class java.lang.Class rep- resenting the newly-created class
     * @return boolean: true if created false otherwise
     * @throws JavaException
     */
    public boolean new_class_4(Term clSource, Term clName, Term clPathes, Term id) throws JavaException {
        Struct classSource = (Struct) clSource.getTerm();
        Struct className = (Struct) clName.getTerm();
        Struct classPathes = (Struct) clPathes.getTerm();
        id = id.getTerm();
        try {
            String fullClassName = removeApices(className.toString());

            String fullClassPath = fullClassName.replace('.', '/');
            final String cp = classPathes.listStream()
                    .map(Struct.class::cast)
                    .map(Struct::getName)
                    .map(File::new)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.joining(File.separator, "-cp \"", "\""));

            String text = removeApices(classSource.toString());
            final File sourcePath = getTempFile(fullClassPath + ".java");

            try {
                FileWriter file = new FileWriter(sourcePath);
                file.write(text);
                file.close();
            } catch (IOException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn(
                        "(creation of " + fullClassPath + ".java fail failed)");
                throw new JavaException(ex);
            }

            try {

                final File javac = getJavaCPath();
                if (!javac.exists()) {
                    final String message = "Compilation of java sources failed because no javac could be found in '" +
                                           getJavaHomePath().getPath() + "/bin'";
                    getEngine().warn(message);
                    throw new IOException(message);
                }
                final String javacPath = javac.getAbsolutePath();
                Process jc = Runtime.getRuntime().exec(javacPath + " " + cp + " " + sourcePath.getAbsolutePath());
                int res = jc.waitFor();
                if (res != 0) {
                    getEngine().warn("Compilation of java sources failed");
                    getEngine().warn("(java compiler (javac) has stopped with errors)");
                    throw new IOException("Compilation of java sources failed");
                }
            } catch (IOException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn("(java compiler (javac) invocation failed)");
                throw new JavaException(ex);
            }
            try {
                Class<?> the_class;

                /**
                 * @author Alessio Mercurio
                 *
                 * On Dalvik VM we can only use the DexClassLoader.
                 */

                if (System.getProperty("java.vm.name").equals("Dalvik")) {
                    the_class = Class.forName(fullClassName, true, dynamicLoader);
                } else {
                    the_class = Class.forName(fullClassName, true, new ClassLoader(getTempFile("")));
                }

                if (bindDynamicObject(id, the_class)) {
                    return true;
                } else {
                    throw new JavaException(new Exception());
                }
            } catch (ClassNotFoundException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn(
                        "(Java Class compiled, but not created: "
                        + fullClassName + " )");
                throw new JavaException(ex);
            }
        } catch (JavaException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JavaException(ex);
        }
    }

    /**
     * Calls a method of a Java object
     *
     * @throws JavaException
     */
    public boolean java_call_3(Term objId, Term method_name, Term idResult)
            throws JavaException {
        objId = objId.getTerm();
        idResult = idResult.getTerm();
        Struct method = (Struct) method_name.getTerm();
        Object obj = null;
        Signature args = null;
        String methodName = null;
        try {
            methodName = method.getName();
            if (!objId.isAtom()) {
                if (objId instanceof Var) {
                    throw new JavaException(new IllegalArgumentException(objId
                                                                                 .toString()));
                }
                Struct sel = (Struct) objId;
                if (sel.getName().equals(".") && sel.getArity() == 2
                    && method.getArity() == 1) {
                    if (methodName.equals("set")) {
                        return java_set(sel.getTerm(0), sel.getTerm(1), method
                                .getTerm(0));
                    } else if (methodName.equals("get")) {
                        return java_get(sel.getTerm(0), sel.getTerm(1), method
                                .getTerm(0));
                    }
                }
            }
            args = parseArg(method);
            // object and argument must be instantiated
            if (objId instanceof Var) {
                throw new JavaException(new IllegalArgumentException(objId
                                                                             .toString()));
            }
            if (args == null) {
                throw new JavaException(new IllegalArgumentException());
            }
            String objName = removeApices(objId.toString());
            obj = staticObjects.containsKey(objName) ? staticObjects.get(objName) : currentObjects.get(objName);
            Object res = null;
            if (obj == null) {
                System.out.println("name " + objName + " null");
            }
            if (obj != null) {
                Class<?> cl = obj.getClass();
                Object[] args_values = args.getValues();
                Method m = lookupMethod(cl, methodName, args.getTypes(), args_values);
                if (m != null) {
                    try {
                        m.setAccessible(true);
                        res = m.invoke(obj, args_values);
                    } catch (IllegalAccessException ex) {
                        getEngine().warn("Method invocation failed: " + methodName + "( signature: " + args + " )");
                        throw new JavaException(ex);
                    }
                } else {
                    getEngine().warn("Method not found: " + methodName + "( signature: " + args + " )");
                    throw new JavaException(new NoSuchMethodException(
                            "Method not found: " + methodName + "( signature: " + args + " )"));
                }
            } else {
                if (objId.isCompound()) {
                    Struct id = (Struct) objId;

                    if (id.getArity() == 1 && id.getName().equals("class")) {
                        try {
                            String clName = removeApices(id.getArg(0).toString());
                            Class<?> cl = Class.forName(clName, true, dynamicLoader);

                            Method m = InspectionUtils.searchForMethod(cl, methodName, args.getTypes());
                            m.setAccessible(true);
                            res = m.invoke(null, args.getValues());
                        } catch (ClassNotFoundException ex) {
                            // if not found even as a class id -> consider as a
                            // String object value
                            getEngine().warn("Unknown class.");
                            throw new JavaException(ex);
                        }
                    } else {
                        // the object is the string itself
                        Method m = java.lang.String.class.getMethod(methodName, args.getTypes());
                        m.setAccessible(true);
                        res = m.invoke(objName, args.getValues());
                    }
                } else {
                    // the object is the string itself
                    Method m = java.lang.String.class.getMethod(methodName, args.getTypes());
                    m.setAccessible(true);
                    res = m.invoke(objName, args.getValues());
                }
            }
            if (parseResult(idResult, res)) {
                return true;
            } else {
                throw new JavaException(new Exception());
            }
        } catch (JavaException ex) {
            throw ex;
        } catch (InvocationTargetException ex) {
            getEngine().warn(
                    "Method failed: " + methodName + " - ( signature: " + args
                    + " ) - Original Exception: "
                    + ex.getTargetException());
            throw new JavaException(new IllegalArgumentException());
        } catch (NoSuchMethodException ex) {
            getEngine().warn("Method not found: " + methodName + " - ( signature: " + args + " )");
            throw new JavaException(ex);
        } catch (IllegalArgumentException ex) {
            getEngine().warn(
                    "Invalid arguments " + args + " - ( method: " + methodName
                    + " )");
            throw new JavaException(ex);
        } catch (Exception ex) {
            getEngine()
                    .warn("Generic error in method invocation " + methodName);
            throw new JavaException(ex);
        }
    }

    /**
     * @throws JavaException
     * @author Michele Mannino
     * <p>
     * Set global classpath
     */
    public boolean set_classpath_1(Term paths) throws JavaException {
        try {
            paths = paths.getTerm();
            if (!paths.isList()) {
                throw new IllegalArgumentException();
            }
            String[] listOfPaths = getStringArrayFromStruct((Struct) paths);
            dynamicLoader.removeAllURLs();
            dynamicLoader.addURLs(getURLsFromStringArray(listOfPaths));
            return true;
        } catch (IllegalArgumentException e) {
            getEngine().warn("Illegal list of paths " + paths);
            throw new JavaException(e);
        } catch (Exception e) {
            throw new JavaException(e);
        }
    }

    /**
     * @throws JavaException
     * @author Michele Mannino
     * <p>
     * Get global classpath
     */

    public boolean get_classpath_1(Term paths) throws JavaException {
        try {
            paths = paths.getTerm();
            if (!(paths instanceof Var)) {
                throw new IllegalArgumentException();
            }
            URL[] urls = dynamicLoader.getURLs();
            final Struct pathTerm = Struct.list(
                    Stream.of(urls).map(URL::getPath).map(File::new).map(File::getAbsolutePath).map(Struct::atom)
            );
            return unify(paths, pathTerm);
        } catch (IllegalArgumentException e) {
            getEngine().warn("Illegal list of paths " + paths);
            throw new JavaException(e);
        } catch (Exception e) {
            throw new JavaException(e);
        }
    }

    /**
     * set the field value of an object
     */
    private boolean java_set(Term objId, Term fieldTerm, Term what) {
        what = what.getTerm();
        if (!fieldTerm.isAtom() || what instanceof Var) {
            return false;
        }
        String fieldName = ((Struct) fieldTerm).getName();
        Object obj = null;
        try {
            Class<?> cl = null;
            if (objId.isCompound() && ((Struct) objId).getName().equals("class")) {
                String clName = null;
                // Case: class(className)
                if (((Struct) objId).getArity() == 1) {
                    clName = removeApices(((Struct) objId).getArg(0).toString());
                }
                if (clName != null) {
                    try {
                        cl = Class.forName(clName, true, dynamicLoader);
                    } catch (ClassNotFoundException ex) {
                        getEngine().warn("Java class not found: " + clName);
                        return false;
                    } catch (Exception ex) {
                        getEngine().warn(
                                "Static field "
                                + fieldName
                                + " not found in class "
                                + removeApices(((Struct) objId).getArg(0).toString()));
                        return false;
                    }
                }
            } else {
                String objName = removeApices(objId.toString());
                obj = currentObjects.get(objName);
                if (obj != null) {
                    cl = obj.getClass();
                } else {
                    return false;
                }
            }

            // first check for primitive data field
            Field field = cl.getField(fieldName);
            if (what instanceof Number) {
                Number wn = (Number) what;
                if (wn instanceof Int) {
                    field.setInt(obj, wn.intValue());
                } else if (wn instanceof alice.tuprolog.Double) {
                    field.setDouble(obj, wn.doubleValue());
                } else if (wn instanceof alice.tuprolog.Long) {
                    field.setLong(obj, wn.longValue());
                } else if (wn instanceof alice.tuprolog.Float) {
                    field.setFloat(obj, wn.floatValue());
                } else {
                    return false;
                }
            } else {
                String what_name = removeApices(what.toString());
                Object obj2 = currentObjects.get(what_name);
                if (obj2 != null) {
                    field.set(obj, obj2);
                } else {
                    // consider value as a simple string
                    field.set(obj, what_name);
                }
            }
            return true;
        } catch (NoSuchFieldException ex) {
            getEngine().warn(
                    "Field " + fieldName + " not found in class " + objId);
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * get the value of the field
     */
    private boolean java_get(Term objId, Term fieldTerm, Term what) {
        if (!fieldTerm.isAtom()) {
            return false;
        }
        String fieldName = ((Struct) fieldTerm).getName();
        Object obj = null;
        try {
            Class<?> cl = null;
            if (objId.isCompound() && ((Struct) objId).getName().equals("class")) {
                String clName = null;
                if (((Struct) objId).getArity() == 1) {
                    clName = removeApices(((Struct) objId).getArg(0).toString());
                }
                if (clName != null) {
                    try {
                        cl = Class.forName(clName, true, dynamicLoader);
                    } catch (ClassNotFoundException ex) {
                        getEngine().warn("Java class not found: " + clName);
                        return false;
                    } catch (Exception ex) {
                        getEngine().warn(
                                "Static field "
                                + fieldName
                                + " not found in class "
                                + removeApices(((Struct) objId).getArg(0).toString()));
                        return false;
                    }
                }
            } else {
                String objName = removeApices(objId.toString());
                obj = currentObjects.get(objName);
                if (obj == null) {
                    return false;
                }
                cl = obj.getClass();
            }

            Field field = cl.getField(fieldName);
            Class<?> fc = field.getType();
            field.setAccessible(true);
            if (fc.equals(Integer.TYPE) || fc.equals(Byte.TYPE)) {
                int value = field.getInt(obj);
                return unify(what, Int.of(value));
            } else if (fc.equals(java.lang.Long.TYPE)) {
                long value = field.getLong(obj);
                return unify(what, Long.of(value));
            } else if (fc.equals(java.lang.Float.TYPE)) {
                float value = field.getFloat(obj);
                return unify(what, Float.of(value));
            } else if (fc.equals(java.lang.Double.TYPE)) {
                double value = field.getDouble(obj);
                return unify(what, Double.of(value));
            } else {
                // the field value is an object
                Object res = field.get(obj);
                return bindDynamicObject(what, res);
            }

        } catch (NoSuchFieldException ex) {
            getEngine().warn("Field " + fieldName + " not found in class " + objId);
            return false;
        } catch (Exception ex) {
            getEngine().warn("Generic error in accessing the field");
            return false;
        }
    }

    public boolean java_array_set_primitive_3(Term obj_id, Term i, Term what)
            throws JavaException {
        Struct objId = (Struct) obj_id.getTerm();
        Number index = (Number) i.getTerm();
        what = what.getTerm();
        Object obj = null;
        if (!index.isInteger()) {
            throw new JavaException(new IllegalArgumentException(index.toString()));
        }
        try {
            Class<?> cl = null;
            String objName = removeApices(objId.toString());
            obj = currentObjects.get(objName);
            if (obj != null) {
                cl = obj.getClass();
            } else {
                throw new JavaException(new IllegalArgumentException(objId.toString()));
            }

            if (!cl.isArray()) {
                throw new JavaException(new IllegalArgumentException(objId.toString()));
            }
            String name = cl.toString();
            switch (name) {
                case "class [I": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    byte v = (byte) ((Number) what).intValue();
                    Array.setInt(obj, index.intValue(), v);
                    break;
                }
                case "class [D": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    double v = ((Number) what).doubleValue();
                    Array.setDouble(obj, index.intValue(), v);
                    break;
                }
                case "class [F": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    float v = ((Number) what).floatValue();
                    Array.setFloat(obj, index.intValue(), v);
                    break;
                }
                case "class [L": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    long v = ((Number) what).longValue();
                    Array.setFloat(obj, index.intValue(), v);
                    break;
                }
                case "class [C": {
                    String s = what.toString();
                    Array.setChar(obj, index.intValue(), s.charAt(0));
                    break;
                }
                case "class [Z": {
                    String s = what.toString();
                    if (s.equals("true")) {
                        Array.setBoolean(obj, index.intValue(), true);
                    } else if (s.equals("false")) {
                        Array.setBoolean(obj, index.intValue(), false);
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    break;
                }
                case "class [B": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    int v = ((Number) what).intValue();
                    Array.setByte(obj, index.intValue(), (byte) v);
                    break;
                }
                case "class [S": {
                    if (!(what instanceof Number)) {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                    short v = (short) ((Number) what).intValue();
                    Array.setShort(obj, index.intValue(), v);
                    break;
                }
                default:
                    throw new JavaException(new Exception());
            }
            return true;
        } catch (Exception ex) {
            throw new JavaException(ex);
        }
    }

    /**
     * Sets the value of the field 'i' with 'what'
     *
     * @param obj_id
     * @param i
     * @param what
     * @return
     * @throws JavaException
     */
    public boolean java_array_get_primitive_3(Term obj_id, Term i, Term what) throws JavaException {
        Struct objId = (Struct) obj_id.getTerm();
        Number index = (Number) i.getTerm();
        what = what.getTerm();
        Object obj = null;
        if (!index.isInteger()) {
            throw new JavaException(new IllegalArgumentException(index.toString()));
        }
        try {
            Class<?> cl = null;
            String objName = removeApices(objId.toString());
            obj = currentObjects.get(objName);
            if (obj != null) {
                cl = obj.getClass();
            } else {
                throw new JavaException(new IllegalArgumentException(objId.toString()));
            }

            if (!cl.isArray()) {
                throw new JavaException(new IllegalArgumentException(objId.toString()));
            }
            String name = cl.toString();
            switch (name) {
                case "class [I": {
                    Term value = Int.of(Array.getInt(obj, index.intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [D": {
                    Term value = Double.of(Array.getDouble(obj, index.intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [F": {
                    Term value = Float.of(Array.getFloat(obj, index
                            .intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [L": {
                    Term value = Long.of(Array.getLong(obj, index.intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [C": {
                    Term value = Struct.atom("" + Array.getChar(obj, index.intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [Z":
                    boolean b = Array.getBoolean(obj, index.intValue());
                    if (b) {
                        if (unify(what, Struct.truth(true))) {
                            return true;
                        } else {
                            throw new JavaException(new IllegalArgumentException(what.toString()));
                        }
                    } else {
                        if (unify(what, Struct.atom("false"))) {
                            return true;
                        } else {
                            throw new JavaException(new IllegalArgumentException(what.toString()));
                        }
                    }
                case "class [B": {
                    Term value = Int.of(Array.getByte(obj, index
                            .intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                case "class [S": {
                    Term value = Int.of(Array.getInt(obj, index
                            .intValue()));
                    if (unify(what, value)) {
                        return true;
                    } else {
                        throw new JavaException(new IllegalArgumentException(what.toString()));
                    }
                }
                default:
                    throw new JavaException(new Exception());
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
            throw new JavaException(ex);
        }

    }

    private boolean java_array(String type, int nargs, Term id) {
        try {
            Object array = null;
            String obtype = type.substring(0, type.length() - 2);

            switch (obtype) {
                case "boolean":
                    array = new boolean[nargs];
                    break;
                case "byte":
                    array = new byte[nargs];
                    break;
                case "char":
                    array = new char[nargs];
                    break;
                case "short":
                    array = new short[nargs];
                    break;
                case "int":
                    array = new int[nargs];
                    break;
                case "long":
                    array = new long[nargs];
                    break;
                case "float":
                    array = new float[nargs];
                    break;
                case "double":
                    array = new double[nargs];
                    break;
                default:
                    Class<?> cl = Class.forName(obtype, true, dynamicLoader);
                    array = Array.newInstance(cl, nargs);
                    break;
            }
            return bindDynamicObject(id, array);
        } catch (Exception ex) {
            // ex.printStackTrace();
            return false;
        }
    }

    /**
     * Returns an URL array from a String array
     *
     * @throws JavaException
     */
    private URL[] getURLsFromStringArray(String[] paths) throws MalformedURLException {
        URL[] urls = null;
        if (paths != null) {
            urls = new URL[paths.length];

            for (int i = 0; i < paths.length; i++) {
                if (paths[i] == null) {
                    continue;
                }
                if (paths[i].contains("http") || paths[i].contains("https") || paths[i].contains("ftp")) {
                    urls[i] = new URL(paths[i]);
                } else {
                    File file = new File(paths[i]);
                    urls[i] = (file.toURI().toURL());
                }
            }
        }
        return urls;
    }

    /**
     * Returns a String array from a Struct contains a list
     *
     * @throws JavaException
     */

    private String[] getStringArrayFromStruct(Struct list) {
        String[] args = new String[list.listSize()];
        Iterator<? extends Term> it = list.listIterator();
        int count = 0;
        while (it.hasNext()) {
            String path = removeApices(it.next().toString());
            args[count++] = path;
        }
        return args;
    }

    /**
     * creation of method signature from prolog data
     */
    private Signature parseArg(Struct method) {
        Object[] values = new Object[method.getArity()];
        Class<?>[] types = new Class[method.getArity()];
        for (int i = 0; i < method.getArity(); i++) {
            if (!parse_arg(values, types, i, method.getTerm(i))) {
                return null;
            }
        }
        return new Signature(values, types);
    }

    private Signature parseArg(Object[] objs) {
        Object[] values = new Object[objs.length];
        Class<?>[] types = new Class[objs.length];
        for (int i = 0; i < objs.length; i++) {
            if (!parse_arg(values, types, i, (Term) objs[i])) {
                return null;
            }
        }
        return new Signature(values, types);
    }

    private boolean parse_arg(Object[] values, Class<?>[] types, int i, Term term) {
        try {
            if (term == null) {
                values[i] = null;
                types[i] = null;
            } else if (term.isAtom()) {
                String name = removeApices(term.toString());
                if (name.equals("true")) {
                    values[i] = Boolean.TRUE;
                    types[i] = Boolean.TYPE;
                } else if (name.equals("false")) {
                    values[i] = Boolean.FALSE;
                    types[i] = Boolean.TYPE;
                } else {
                    Object obj = currentObjects.get(name);
                    if (obj == null) {
                        values[i] = name;
                    } else {
                        values[i] = obj;
                    }
                    types[i] = values[i].getClass();
                }
            } else if (term instanceof Number) {
                Number t = (Number) term;
                if (t instanceof Int) {
                    values[i] = t.intValue();
                    types[i] = java.lang.Integer.TYPE;
                } else if (t instanceof alice.tuprolog.Double) {
                    values[i] = t.doubleValue();
                    types[i] = java.lang.Double.TYPE;
                } else if (t instanceof alice.tuprolog.Long) {
                    values[i] = t.longValue();
                    types[i] = java.lang.Long.TYPE;
                } else if (t instanceof alice.tuprolog.Float) {
                    values[i] = t.floatValue();
                    types[i] = java.lang.Float.TYPE;
                }
            } else if (term instanceof Struct) {
                // argument descriptors
                Struct tc = (Struct) term;
                if (tc.getName().equals("as")) {
                    return parse_as(values, types, i, tc.getTerm(0), tc
                            .getTerm(1));
                } else {
                    Object obj = currentObjects.get(removeApices(tc.toString()));
                    if (obj == null) {
                        values[i] = removeApices(tc.toString());
                    } else {
                        values[i] = obj;
                    }
                    types[i] = values[i].getClass();
                }
            } else if (term instanceof Var && !((Var) term).isBound()) {
                values[i] = null;
                types[i] = Object.class;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * parsing 'as' operator, which makes it possible to define the specific
     * class of an argument
     */
    private boolean parse_as(Object[] values, Class<?>[] types, int i,
                             Term castWhat, Term castTo) {
        try {
            if (!(castWhat instanceof Number)) {
                String castTo_name = removeApices(((Struct) castTo).getName());
                String castWhat_name = removeApices(castWhat.getTerm().toString());

                if (castTo_name.equals(String.class.getName())
                    && castWhat_name.equals("true")) {
                    values[i] = "true";
                    types[i] = String.class;
                    return true;
                } else if (castTo_name.equals(String.class.getName())
                           && castWhat_name.equals("false")) {
                    values[i] = "false";
                    types[i] = String.class;
                    return true;
                } else if (castTo_name.endsWith("[]")) {
                    switch (castTo_name) {
                        case "boolean[]":
                            castTo_name = "[Z";
                            break;
                        case "byte[]":
                            castTo_name = "[B";
                            break;
                        case "short[]":
                            castTo_name = "[S";
                            break;
                        case "char[]":
                            castTo_name = "[C";
                            break;
                        case "int[]":
                            castTo_name = "[I";
                            break;
                        case "long[]":
                            castTo_name = "[L";
                            break;
                        case "float[]":
                            castTo_name = "[F";
                            break;
                        case "double[]":
                            castTo_name = "[D";
                            break;
                        default:
                            castTo_name = "[L" + castTo_name.substring(0, castTo_name.length() - 2) + ";";
                            break;
                    }
                }
                if (!castWhat_name.equals("null")) {
                    Object obj_to_cast = currentObjects.get(castWhat_name);
                    if (obj_to_cast == null) {
                        if (castTo_name.equals("boolean")) {
                            if (castWhat_name.equals("true")) {
                                values[i] = Boolean.TRUE;
                            } else if (castWhat_name.equals("false")) {
                                values[i] = Boolean.FALSE;
                            } else {
                                return false;
                            }
                            types[i] = Boolean.TYPE;
                        } else {
                            // conversion to array
                            return false;
                        }
                    } else {
                        values[i] = obj_to_cast;
                        try {
                            types[i] = Class.forName(castTo_name, true, dynamicLoader);
                        } catch (ClassNotFoundException ex) {
                            getEngine().warn(
                                    "Java class not found: " + castTo_name);
                            return false;
                        }
                    }
                } else {
                    values[i] = null;
                    switch (castTo_name) {
                        case "byte":
                            types[i] = Byte.TYPE;
                            break;
                        case "short":
                            types[i] = Short.TYPE;
                            break;
                        case "char":
                            types[i] = Character.TYPE;
                            break;
                        case "int":
                            types[i] = Integer.TYPE;
                            break;
                        case "long":
                            types[i] = java.lang.Long.TYPE;
                            break;
                        case "float":
                            types[i] = java.lang.Float.TYPE;
                            break;
                        case "double":
                            types[i] = java.lang.Double.TYPE;
                            break;
                        case "boolean":
                            types[i] = Boolean.TYPE;
                            break;
                        default:
                            try {
                                types[i] = Class.forName(castTo_name, true, dynamicLoader);
                            } catch (ClassNotFoundException ex) {
                                getEngine().warn(
                                        "Java class not found: " + castTo_name);
                                return false;
                            }
                            break;
                    }
                }
            } else {
                Number num = (Number) castWhat;
                String castTo_name = ((Struct) castTo).getName();
                switch (castTo_name) {
                    case "byte":
                        values[i] = (byte) num.intValue();
                        types[i] = Byte.TYPE;
                        break;
                    case "short":
                        values[i] = (short) num.intValue();
                        types[i] = Short.TYPE;
                        break;
                    case "int":
                        values[i] = num.intValue();
                        types[i] = Integer.TYPE;
                        break;
                    case "long":
                        values[i] = num.longValue();
                        types[i] = java.lang.Long.TYPE;
                        break;
                    case "float":
                        values[i] = num.floatValue();
                        types[i] = java.lang.Float.TYPE;
                        break;
                    case "double":
                        values[i] = num.doubleValue();
                        types[i] = java.lang.Double.TYPE;
                        break;
                    default:
                        return false;
                }
            }
        } catch (Exception ex) {
            getEngine().warn(
                    "Casting " + castWhat + " to " + castTo + " failed");
            return false;
        }
        return true;
    }

    /**
     * parses return value of a method invokation
     */
    private boolean parseResult(Term id, Object obj) {
        if (obj == null) {
            // return unify(id,Term.TRUE);
            return unify(id, Var.underscore());
        }
        try {
            if (obj instanceof Boolean) {
                if ((Boolean) obj) {
                    return unify(id, Struct.truth(true));
                } else {
                    return unify(id, Struct.atom("false"));
                }
            } else if (obj instanceof Byte) {
                return unify(id, Int.of(((Byte) obj).intValue()));
            } else if (obj instanceof Short) {
                return unify(id, Int.of(((Short) obj).intValue()));
            } else if (obj instanceof Integer) {
                return unify(id, Int.of(((Integer) obj).intValue()));
            } else if (obj instanceof java.lang.Long) {
                return unify(id, Long.of(((java.lang.Long) obj).longValue()));
            } else if (obj instanceof java.lang.Float) {
                return unify(id, Float.of(
                        ((java.lang.Float) obj).floatValue()));
            } else if (obj instanceof java.lang.Double) {
                return unify(id, Double.of(
                        ((java.lang.Double) obj).doubleValue()));
            } else if (obj instanceof String) {
                return unify(id, Struct.atom((String) obj));
            } else if (obj instanceof Character) {
                return unify(id, Struct.atom(((Character) obj).toString()));
            } else {
                return bindDynamicObject(id, obj);
            }
        } catch (Exception ex) {
            return false;
        }
    }

    private Object[] getArrayFromList(Struct list) {
        Object[] args = new Object[list.listSize()];
        Iterator<? extends Term> it = list.listIterator();
        int count = 0;
        while (it.hasNext()) {
            args[count++] = it.next();
        }
        return args;
    }

    /**
     * Register an object with the specified id. The life-time of the link to
     * the object is engine life-time, available besides the individual query.
     *
     * @param id  object identifier
     * @param obj the object
     * @return true if the operation is successful
     * @throws InvalidObjectIdException if the object id is not valid
     */
    public boolean register(Struct id, Object obj)
            throws InvalidObjectIdException {
        /*
         * note that this method act on the staticObject and
         * staticObject_inverse hashmaps
         */
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        // already registered object?
        synchronized (staticObjects) {
            Object aKey = staticObjects_inverse.get(obj);

            if (aKey != null) {
                // object already referenced
                return false;
            } else {
                String raw_name = removeApices(id.getTerm().toString());
                staticObjects.put(raw_name, obj);
                staticObjects_inverse.put(obj, id);
                return true;
            }
        }
    }

    /**
     * Register an object with the specified id. The life-time of the link to
     * the object is engine life-time, available besides the individual query.
     * <p>
     * The identifier must be a ground object.
     *
     * @param id object identifier
     * @return true if the operation is successful
     * @throws JavaException if the object id is not valid
     */
    public boolean register_1(Term id) throws JavaException {
        id = id.getTerm();
        Object obj = null;
        try {
            obj = getRegisteredDynamicObject((Struct) id);
            return register((Struct) id, obj);
        } catch (InvalidObjectIdException e) {
            getEngine().warn("Illegal object id " + id.toString());
            throw new JavaException(e);
        }
    }

    /**
     * Unregister an object with the specified id.
     * <p>
     * The identifier must be a ground object.
     *
     * @param id object identifier
     * @return true if the operation is successful
     * @throws JavaException if the object id is not valid
     */
    public boolean unregister_1(Term id) throws JavaException {
        id = id.getTerm();
        try {
            return unregister((Struct) id);
        } catch (InvalidObjectIdException e) {
            getEngine().warn("Illegal object id " + id.toString());
            throw new JavaException(e);
        }
    }

    /**
     * Registers an object, with automatic creation of the identifier.
     * <p>
     * If the object is already registered, its identifier is returned
     *
     * @param obj object to be registered.
     * @return fresh id
     */
    public Struct register(Object obj) {
        // already registered object?
        synchronized (staticObjects) {
            Object aKey = staticObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                // log("obj already registered: unify "+id+" "+aKey);
                return (Struct) aKey;
            } else {
                Struct id = generateFreshId();
                staticObjects.put(id.getName(), obj);
                staticObjects_inverse.put(obj, id);
                return id;
            }
        }
    }

    /**
     * Gets the reference to an object previously registered
     *
     * @param id object id
     * @return the object, if present
     * @throws InvalidObjectIdException
     */
    public Object getRegisteredObject(Struct id)
            throws InvalidObjectIdException {
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        synchronized (staticObjects) {
            return staticObjects.get(removeApices(id.toString()));
        }
    }

    /**
     * Unregisters an object, given its identifier
     *
     * @param id object identifier
     * @return true if the operation is successful
     * @throws InvalidObjectIdException if the id is not valid (e.g. is not ground)
     */
    public boolean unregister(Struct id) throws InvalidObjectIdException {
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        synchronized (staticObjects) {
            String raw_name = removeApices(id.toString());
            Object obj = staticObjects.remove(raw_name);
            if (obj != null) {
                staticObjects_inverse.remove(obj);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Registers an object only for the running query life-time
     *
     * @param id  object identifier
     * @param obj object
     */
    public void registerDynamic(Struct id, Object obj) {
        synchronized (currentObjects) {
            String raw_name = removeApices(id.toString());
            currentObjects.put(raw_name, obj);
            currentObjects_inverse.put(obj, id);
        }
    }

    /**
     * Registers an object for the query life-time, with the automatic
     * generation of the identifier.
     * <p>
     * If the object is already registered, its identifier is returned
     *
     * @param obj object to be registered
     * @return identifier
     */
    public Struct registerDynamic(Object obj) {
        // System.out.println("lib: "+this+" current id: "+this.id);

        // already registered object?
        synchronized (currentObjects) {
            Object aKey = currentObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                // log("obj already registered: unify "+id+" "+aKey);
                return (Struct) aKey;
            } else {
                Struct id = generateFreshId();
                currentObjects.put(id.getName(), obj);
                currentObjects_inverse.put(obj, id);
                return id;
            }
        }
    }

    /**
     * Gets a registered dynamic object (returns null if not presents)
     */
    public Object getRegisteredDynamicObject(Struct id)
            throws InvalidObjectIdException {
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        synchronized (currentObjects) {
            return currentObjects.get(removeApices(id.toString()));
        }
    }

    /**
     * Unregister the object, only for dynamic case
     *
     * @param id object identifier
     * @return true if the operation is successful
     */
    public boolean unregisterDynamic(Struct id) {
        synchronized (currentObjects) {
            String raw_name = removeApices(id.toString());
            Object obj = currentObjects.remove(raw_name);
            if (obj != null) {
                currentObjects_inverse.remove(obj);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Tries to bind specified id to a provided java object.
     * <p>
     * Term id can be a variable or a ground term.
     */
    protected boolean bindDynamicObject(Term id, Object obj) {
        // null object are considered to _ variable
        if (obj == null) {
            return unify(id, Var.underscore());
        }
        // already registered object?
        synchronized (currentObjects) {
            Object aKey = currentObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                // log("obj already registered: unify "+id+" "+aKey);
                return unify(id, (Term) aKey);
            } else {
                // object not previously referenced
                if (id instanceof Var) {
                    // get a ground term
                    Struct idTerm = generateFreshId();
                    unify(id, idTerm);
                    registerDynamic(idTerm, obj);
                    // log("not ground id for a new obj: "+id+" as ref for "+obj);
                    return true;
                } else {
                    // verify of the id is already used
                    String raw_name = removeApices(id.getTerm().toString());
                    Object linkedobj = currentObjects.get(raw_name);
                    if (linkedobj == null) {
                        registerDynamic((Struct) (id.getTerm()), obj);
                        // log("ground id for a new obj: "+id+" as ref for "+obj);
                        return true;
                    } else {
                        // an object with the same id is already
                        // present: must be the same object
                        return obj == linkedobj;
                    }
                }
            }
        }
    }

    /**
     * Generates a fresh numeric identifier
     *
     * @return
     */
    protected Struct generateFreshId() {
        return Struct.atom("$obj_" + id++);
    }

    /**
     * handling writeObject method is necessary in order to make the library
     * serializable, 'nullyfing' eventually objects registered in maps
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        HashMap<String, Object> bak00 = currentObjects;
        IdentityHashMap<Object, Struct> bak01 = currentObjects_inverse;
        try {
            currentObjects = null;
            currentObjects_inverse = null;
            out.defaultWriteObject();
        } catch (IOException ex) {
            currentObjects = bak00;
            currentObjects_inverse = bak01;
            throw new IOException();
        }
        currentObjects = bak00;
        currentObjects_inverse = bak01;
    }

    /**
     * handling readObject method is necessary in order to have the library
     * reconstructed after a serialization
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        currentObjects = new HashMap<String, Object>();
        currentObjects_inverse = new IdentityHashMap<Object, Struct>();
        preregisterObjects();
    }

}

/**
 * Signature class mantains information about type and value of a method
 * arguments
 */
@SuppressWarnings("serial")
class Signature implements Serializable {
    Class<?>[] types;
    Object[] values;

    public Signature(Object[] v, Class<?>[] c) {
        values = v;
        types = c;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    Object[] getValues() {
        return values;
    }

    public String toString() {
        String st = "";
        for (int i = 0; i < types.length; i++) {
            st = st + "\n  Argument " + i + " -  VALUE: " + values[i] + " TYPE: " + types[i];
        }
        return st;
    }
}

/**
 * used to load new classes without touching system class loader
 */
class ClassLoader extends URLClassLoader {

    ClassLoader(File path) {
        super(new URL[]{toUrl(path)});
    }

    private static URL toUrl(File path) {
        try {
            return path.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}