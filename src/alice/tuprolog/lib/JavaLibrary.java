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

import alice.tuprolog.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import alice.tuprolog.Number;

/**
 *
 * This class represents a tuProlog library enabling the interaction
 * with the Java environment from tuProlog.
 *
 * Works only with JDK 1.2 (because of setAccessible method)
 *
 * The most specific method algorithm used to find constructors / methods
 * has been inspired by the article
 *     "What Is Interactive Scripting?", by Michael Travers
 *     Dr. Dobb's -- Software Tools for the Professional Programmer
 *     January 2000
 *     CMP  Media Inc., a United News and Media Company
 *
 * Library/Theory Dependency:  BasicLibrary
 *
 *
 *
 */
public class JavaLibrary extends Library {

    /**
     * java objects referenced by prolog terms (keys)
     */
    private HashMap currentObjects = new HashMap();
    /**
     * inverse map useful for implementation issue
     */
    private HashMap currentObjects_inverse = new HashMap();

    private HashMap staticObjects = new HashMap();
    private HashMap staticObjects_inverse = new HashMap();

    /** progressive conter used to identify registered objects */
    private int id = 0;



    /**
     * library theory
     */
    public String getTheory() {
        return
                //
                // operators defined by the JavaLibrary theory
                //
                ":- op(800,xfx,'<-').\n" +
                ":- op(850,xfx,'returns').\n" +
                ":- op(200,xfx,'as').\n" +
                ":- op(600,xfx,'.'). \n" +
                //
                // flags defined by the JavaLibrary theory
                //
                ":- flag(java_object_backtrackable,[true,false],false,true).\n" +
                //
                //
                "java_object(ClassName,Args,Id):- current_prolog_flag(java_object_backtrackable,false),!,java_object_nb(ClassName,Args,Id).\n" +
                "java_object(ClassName,Args,Id):- !,java_object_bt(ClassName,Args,Id).\n" +

                "java_object_bt(ClassName,Args,Id):- java_object_nb(ClassName,Args,Id).\n" +
                "java_object_bt(ClassName,Args,Id):- destroy_object(Id).\n" +

                "Obj <- What :- java_call(Obj,What,Res), Res \\== false.\n" +
                "Obj <- What returns Res :- java_call(Obj,What,Res).\n" +
                "java_array_set(Array,Index,Object):-           class('java.lang.reflect.Array') <- set(Array as 'java.lang.Object',Index,Object as 'java.lang.Object').\n" +
                "java_array_set_int(Array,Index,Object):-       class('java.lang.reflect.Array') <- setInt(Array as 'java.lang.Object',Index,Object as 'int').\n" +
                "java_array_set_short(Array,Index,Object):-     class('java.lang.reflect.Array') <- setShort(Array as 'java.lang.Object',Index,Object as 'short').\n" +
                "java_array_set_long(Array,Index,Object):-      class('java.lang.reflect.Array') <- setLong(Array as 'java.lang.Object',Index,Object as 'long').\n" +
                "java_array_set_float(Array,Index,Object):-     class('java.lang.reflect.Array') <- setFloat(Array as 'java.lang.Object',Index,Object as 'float').\n" +
                "java_array_set_double(Array,Index,Object):-    class('java.lang.reflect.Array') <- setDouble(Array as 'java.lang.Object',Index,Object as 'double').\n" +
                "java_array_set_char(Array,Index,Object):-      class('java.lang.reflect.Array') <- setChar(Array as 'java.lang.Object',Index,Object as 'char').\n" +
                "java_array_set_byte(Array,Index,Object):-      class('java.lang.reflect.Array') <- setByte(Array as 'java.lang.Object',Index,Object as 'byte').\n" +
                "java_array_set_boolean(Array,Index,Object):-   class('java.lang.reflect.Array') <- setBoolean(Array as 'java.lang.Object',Index,Object as 'boolean').\n" +

                "java_array_get(Array,Index,Object):-           class('java.lang.reflect.Array') <- get(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_int(Array,Index,Object):-       class('java.lang.reflect.Array') <- getInt(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_short(Array,Index,Object):-     class('java.lang.reflect.Array') <- getShort(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_long(Array,Index,Object):-      class('java.lang.reflect.Array') <- getLong(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_float(Array,Index,Object):-     class('java.lang.reflect.Array') <- getFloat(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_double(Array,Index,Object):-    class('java.lang.reflect.Array') <- getDouble(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_char(Array,Index,Object):-      class('java.lang.reflect.Array') <- getChar(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_byte(Array,Index,Object):-      class('java.lang.reflect.Array') <- getByte(Array as 'java.lang.Object',Index) returns Object.\n" +
                "java_array_get_boolean(Array,Index,Object):-   class('java.lang.reflect.Array') <- getBoolean(Array as 'java.lang.Object',Index) returns Object.\n" +

                "java_array_length(Array,Length):-              class('java.lang.reflect.Array') <- getLength(Array as 'java.lang.Object') returns Length.\n" +
                "java_object_string(Object,String):-    Object <- toString returns String.    \n";
    }


    public   void dismiss() {
            currentObjects.clear();
            currentObjects_inverse.clear();
    }

    public   void dismissAll() {
            currentObjects.clear();
            currentObjects_inverse.clear();
            staticObjects.clear();
            staticObjects_inverse.clear();
    }

    public   void onSolveBegin(Term goal) {
            id = 0;
            currentObjects.clear();
            currentObjects_inverse.clear();
            Iterator it = staticObjects_inverse.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry en = (Map.Entry) it.next();
                bindObject((Struct) en.getValue(), en.getKey());
            }
            preregisterObjects();
    }

    public   void onSolveEnd() {
    }

    /**
     * objects actually pre-registered in order to be
     * available since the beginning of demonstration
     */
    protected void preregisterObjects() {
        try {
            bindObject(new Struct("stdout"), System.out);
            bindObject(new Struct("stderr"), System.err);
            bindObject(new Struct("runtime"), Runtime.getRuntime());
            bindObject(new Struct("current_thread"), Thread.currentThread());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //----------------------------------------------------------------------------

    /**
     * Creates of a java object - not backtrackable case
     */
    public   boolean java_object_nb_3(Term className, Struct arg, Term id) {
        try {
            if (!className.isAtom()) {
                return false;
            }
            String clName = ((Struct) className).getName();
            // check for array type
            if (clName.endsWith("[]")) {
                Object[] list = getArrayFromList(arg);
                int nargs = ((Number) list[0]).intValue();
                return java_array(clName, nargs, id);
            }
            Signature args = parseArg(getArrayFromList(arg));
            if (args == null) {
                return false;
            }
            // object creation with argument described in args
            try {
                Class cl = Class.forName(clName);
                Object[] args_value = args.getValues();
                //
                //Constructor co=cl.getConstructor(args.getTypes());
                Constructor co = lookupConstructor(cl, args.getTypes(), args_value);
                //
                //
                if (co==null){
                    getEngine().warn("Constructor not found: class " + clName);
                    return false;
                }

                Object obj = co.newInstance(args_value);
                return bindObject(id, obj);
            } catch (ClassNotFoundException ex) {
                getEngine().warn("Java class not found: " + clName);
                return false;
            } catch (InvocationTargetException ex) {
                getEngine().warn("Invalid constructor arguments.");
                return false;
            } catch (NoSuchMethodException ex) {
                getEngine().warn("Constructor not found: " + args.getTypes());
                return false;
            } catch (InstantiationException ex) {
                getEngine().warn("Objects of class " + clName + " cannot be instantiated");
                return false;
            } catch (IllegalArgumentException ex) {
                getEngine().warn("Illegal constructor arguments  " + args);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * Destroy the link to a java object - called not directly, but from
     * predicate java_object (as second choice, for backtracking)
     */
    public   boolean destroy_object_1(Term id) {
        try {
            if (id.isGround()) {
                unregisterObject(id);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * Creates of a java class
     */
    public   boolean java_class_4(Struct classSource, Struct className, Struct classPathes, Term id) {
        try {
            String fullClassName = alice.util.Tools.removeApices(className.toString());

            String fullClassPath = fullClassName.replace('.', '/');
            Iterator it = classPathes.listIterator();
            String cp = "";
            while (it.hasNext()) {
                if (cp.length() > 0) {
                    cp += ";";
                }
                cp += alice.util.Tools.removeApices(((Struct) it.next()).toString());
            }
            if (cp.length() > 0) {
                cp = " -classpath " + cp;
            }

            String text = alice.util.Tools.removeApices(classSource.toString());

            //System.out.println("class source: "+text+
            //                   "\nid: "+id+
            //                   "\npath: "+fullClassPath);
            try {
                FileWriter file = new FileWriter(fullClassPath + ".java");
                file.write(text);
                file.close();
            } catch (IOException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn("(creation of " + fullClassPath + ".java fail failed)");
                return false;
            }
            String cmd = "javac " + cp + " " + fullClassPath + ".java";
            //System.out.println("EXEC: "+cmd);
            try {
                Process jc = Runtime.getRuntime().exec(cmd);
                int res = jc.waitFor();
                if (res != 0) {
                    getEngine().warn("Compilation of java sources failed");
                    getEngine().warn("(java compiler (javac) has stopped with errors)");
                    return false;
                }
            } catch (IOException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn("(java compiler (javac) invocation failed)");
                return false;
            }
            try {
                Class the_class = Class.forName(fullClassName, true, new ClassLoader());
                return bindObject(id, the_class);
            } catch (ClassNotFoundException ex) {
                getEngine().warn("Compilation of java sources failed");
                getEngine().warn("(Java Class compiled, but not created: " + fullClassName + " )");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


    /**
     *
     * Calls a method of a Java object
     *
     */
    public   boolean java_call_3(Term objId, Struct method, Term idResult) {
        Object obj = null;
        Signature args = null;
        String methodName = null;
        try {
            methodName = method.getName();
            // check for accessing field   Obj.Field <- set/get(X)
            //  in that case: objId is '.'(Obj, Field)

            if (!objId.isAtom()) {
                if (objId.isVar()) {
                    return false;
                }
                Struct sel = (Struct) objId;
                if (sel.getName().equals(".") && sel.getArity() == 2 && method.getArity() == 1) {
                    if (methodName.equals("set"))
                        return java_set(sel.getTerm(0), sel.getTerm(1), method.getTerm(0));
                    else if (methodName.equals("get"))
                        return java_get(sel.getTerm(0), sel.getTerm(1), method.getTerm(0));
                }
            }

            args = parseArg(method);

            // object and argument must be instantiated
            if (objId.isVar() || args == null)
                return false;

            //System.out.println(args);
            String objName = alice.util.Tools.removeApices(objId.toString());
            obj = currentObjects.get(objName);
            Term tres = null;
            Object res = null;

            if (obj != null) {
                Class cl = obj.getClass();
                //
                //
                Object[] args_values = args.getValues();
                Method m = lookupMethod(cl, methodName, args.getTypes(), args_values);
                //
                //
                if (m != null) {
                    try {
                        // works only with JDK 1.2
                        m.setAccessible(true);

                        res = m.invoke(obj, args_values);

                    } catch (IllegalAccessException ex) {
                        getEngine().warn("Method invocation failed: " + methodName+ "( signature: " + args + " )");
                        ex.printStackTrace();
                        return false;
                    }
                } else {
                    getEngine().warn("Method not found: " + methodName+ "( signature: " + args + " )");
                    return false;
                }
            } else {
                if (objId.isCompound()) {
                    Struct id = (Struct) objId;
                    if (id.getArity() == 1 && id.getName().equals("class")) {
                        try {
                            Class cl = Class.forName(alice.util.Tools.removeApices(id.getArg(0).toString()));
                            Method m = cl.getMethod(methodName, args.getTypes());
                            m.setAccessible(true);
                            res = m.invoke(null, args.getValues());
                        } catch (ClassNotFoundException ex) {
                            // if not found even as a class id -> consider as a String object value
                            getEngine().warn("Unknown class.");
                            return false;
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
            return parseResult(idResult, res);
        } catch (InvocationTargetException ex) {
            getEngine().warn("Method failed: " + methodName + " - ( signature: " + args +
                        " ) - Original Exception: "+ex.getTargetException());
            return false;
        } catch (NoSuchMethodException ex) {
            getEngine().warn("Method not found: " + methodName+ " - ( signature: " + args + " )");
            return false;
        } catch (IllegalArgumentException ex) {
            getEngine().warn("Invalid arguments " + args+ " - ( method: " + methodName + " )");
            //ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            getEngine().warn("Generic error in method invocation " + methodName);
            return false;
        }
    }

    /*
     * set the field value of an object
     */
    private boolean java_set(Term objId, Term fieldTerm, Term what) {
        //System.out.println("SET "+objId+" "+fieldTerm+" "+what);
        what = what.getTerm();
        if (!fieldTerm.isAtom() || what.isVar())
            return false;
        String fieldName = ((Struct) fieldTerm).getName();
        Object obj = null;
        try {
            Class cl = null;
            if (objId.isCompound() &&
                    ((Struct) objId).getArity() == 1 && ((Struct) objId).getName().equals("class")) {
                String clName = alice.util.Tools.removeApices(((Struct) objId).getArg(0).toString());
                try {
                    cl = Class.forName(clName);
                } catch (ClassNotFoundException ex) {
                    getEngine().warn("Java class not found: " + clName);
                    return false;
                } catch (Exception ex) {
                    getEngine().warn("Static field " + fieldName + " not found in class " + alice.util.Tools.removeApices(((Struct) objId).getArg(0).toString()));
                    return false;
                }
            } else {
                String objName = alice.util.Tools.removeApices(objId.toString());
                obj = currentObjects.get(objName);
                if (obj != null) {
                    cl = obj.getClass();
                } else {
                    return false;
                }
            }

            // first check for primitive data field
            Field field = cl.getField(fieldName);
            if (what.isNumber()) {
                Number wn = (Number) what;
                if (wn.isTypeInt()) {
                    field.setInt(obj, wn.intValue());
                } else if (wn.isTypeDouble()) {
                    field.setDouble(obj, wn.doubleValue());
                } else if (wn.isTypeLong()) {
                    field.setLong(obj, wn.longValue());
                } else if (wn.isTypeFloat()) {
                    field.setFloat(obj, wn.floatValue());
                } else {
                    return false;
                }
            } else {
                String what_name = alice.util.Tools.removeApices(what.toString());
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
            getEngine().warn("Field " + fieldName + " not found in class " + objId);
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /*
     * get the value of the field
     */
    private boolean java_get(Term objId, Term fieldTerm, Term what) {
        //System.out.println("GET "+objId+" "+fieldTerm+" "+what);
        if (!fieldTerm.isAtom()) {
            return false;
        }
        String fieldName = ((Struct) fieldTerm).getName();
        Object obj = null;
        try {
            Class cl = null;
            if (objId.isCompound() &&
                    ((Struct) objId).getArity() == 1 && ((Struct) objId).getName().equals("class")) {
                String clName = alice.util.Tools.removeApices(((Struct) objId).getArg(0).toString());
                try {
                    cl = Class.forName(clName);
                } catch (ClassNotFoundException ex) {
                    getEngine().warn("Java class not found: " + clName);
                    return false;
                } catch (Exception ex) {
                    getEngine().warn("Static field " + fieldName + " not found in class " + alice.util.Tools.removeApices(((Struct) objId).getArg(0).toString()));
                    return false;
                }
            } else {
                String objName = alice.util.Tools.removeApices(objId.toString());
                obj = currentObjects.get(objName);
                if (obj == null) {
                    return false;
                }
                cl = obj.getClass();
            }

            Field field = cl.getField(fieldName);
            Class fc = field.getType();
            // work only with JDK 1.2
            field.setAccessible(true);

            // first check for primitive types
            if (fc.equals(Integer.TYPE) || fc.equals(Byte.TYPE)) {
                int value = field.getInt(obj);
                return unify(what, new alice.tuprolog.Int(value));
            } else if (fc.equals(java.lang.Long.TYPE)) {
                long value = field.getLong(obj);
                return unify(what, new alice.tuprolog.Long(value));
            } else if (fc.equals(java.lang.Float.TYPE)) {
                float value = field.getFloat(obj);
                return unify(what, new alice.tuprolog.Float(value));
            } else if (fc.equals(java.lang.Double.TYPE)) {
                double value = field.getDouble(obj);
                return unify(what, new alice.tuprolog.Double(value));
            } else {
                // the field value is an object
                Object res = field.get(obj);
                return bindObject(what, res);
            }
            //} catch (ClassNotFoundException ex){
            //    getEngine().warn("object of unknown class "+objId);
            //ex.printStackTrace();
            //    return false;
        } catch (NoSuchFieldException ex) {
            getEngine().warn("Field " + fieldName + " not found in class " + objId);
            return false;
        } catch (Exception ex) {
            getEngine().warn("Generic error in accessing the field");
            //ex.printStackTrace();
            return false;
        }
    }

    private boolean java_array(String type, int nargs, Term id) {
        try {
            Object array = null;
            String obtype = type.substring(0, type.length() - 2);
            if (obtype.equals("boolean")) {
                array = new boolean[nargs];
            } else if (obtype.equals("byte")) {
                array = new byte[nargs];
            } else if (obtype.equals("char")) {
                array = new char[nargs];
            } else if (obtype.equals("short")) {
                array = new short[nargs];
            } else if (obtype.equals("int")) {
                array = new int[nargs];
            } else if (obtype.equals("long")) {
                array = new long[nargs];
            } else if (obtype.equals("float")) {
                array = new float[nargs];
            } else if (obtype.equals("double")) {
                array = new double[nargs];
            } else {
                Class cl = Class.forName(obtype);
                array = Array.newInstance(cl, nargs);
            }
            return bindObject(id, array);
        } catch (Exception ex) {
            //ex.printStackTrace();
            return false;
        }
    }

    /**
     * creation of method signature from prolog data
     */
    private Signature parseArg(Struct method) {
        Object[] values = new Object[method.getArity()];
        Class[] types = new Class[method.getArity()];
        for (int i = 0; i < method.getArity(); i++) {
            if (!parse_arg(values, types, i, (Term) method.getTerm(i)))
                return null;
        }
        return new Signature(values, types);
    }

    private Signature parseArg(Object[] objs) {
        Object[] values = new Object[objs.length];
        Class[] types = new Class[objs.length];
        for (int i = 0; i < objs.length; i++) {
            if (!parse_arg(values, types, i, (Term) objs[i]))
                return null;
        }
        return new Signature(values, types);
    }

    private boolean parse_arg(Object[] values, Class[] types, int i, Term term) {
        try {
            if (term == null) {
                values[i] = null;
                types[i] = null;
            } else if (term.isAtom()) {
                String name = alice.util.Tools.removeApices(term.toString());
                if (name.equals("true")){
                	values[i]=Boolean.TRUE;
					types[i] = Boolean.TYPE;
                } else if (name.equals("false")){
					values[i]=Boolean.FALSE;
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
            } else if (term.isNumber()) {
                Number t = (Number) term;
                if (t.isTypeInt()) {
                    values[i] = new java.lang.Integer(t.intValue());
                    types[i] = java.lang.Integer.TYPE;
                } else if (t.isTypeDouble()) {
                    values[i] = new java.lang.Double(t.doubleValue());
                    types[i] = java.lang.Double.TYPE;
                } else if (t.isTypeLong()) {
                    values[i] = new java.lang.Long(t.longValue());
                    types[i] = java.lang.Long.TYPE;
                } else if (t.isTypeFloat()) {
                    values[i] = new java.lang.Float(t.floatValue());
                    types[i] = java.lang.Float.TYPE;
                }
            } else if (term.isStruct()) {
                // argument descriptors
                Struct tc = (Struct) term;
                if (tc.getName().equals("as")) {
                    return parse_as(values, types, i, tc.getTerm(0), tc.getTerm(1));
                } else {
                    Object obj = currentObjects.get(alice.util.Tools.removeApices(tc.toString()));
                    if (obj == null) {
                        values[i] = alice.util.Tools.removeApices(tc.toString());
                    } else {
                        values[i] = obj;
                    }
                    types[i] = values[i].getClass();
                }
            } else if (term.isVar() && !((Var) term).isBound()) {
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
     *
     * parsing 'as' operator, which makes it possible
     * to define the specific class of an argument
     *
     */
    private boolean parse_as(Object[] values, Class[] types, int i, Term castWhat, Term castTo) {
        try {
            if (!castWhat.isNumber()) {
                String castTo_name = alice.util.Tools.removeApices(((Struct) castTo).getName());
                String castWhat_name = alice.util.Tools.removeApices(castWhat.getTerm().toString());
                //System.out.println(castWhat_name+" "+castTo_name);
                if (castTo_name.equals("java.lang.String") && 
                	castWhat_name.equals("true")){
                	values[i]="true";
                	types[i]=String.class;	
                	return true;
               	} else if (castTo_name.equals("java.lang.String") && 
									castWhat_name.equals("false")){
					values[i]="false";
					types[i]=String.class;	
					return true;
				} else if (castTo_name.endsWith("[]")) {
                    if (castTo_name.equals("boolean[]")) {
                        castTo_name = "[Z";
                    } else if (castTo_name.equals("byte[]")) {
                        castTo_name = "[B";
                    } else if (castTo_name.equals("short[]")) {
                        castTo_name = "[S";
                    } else if (castTo_name.equals("char[]")) {
                        castTo_name = "[C";
                    } else if (castTo_name.equals("int[]")) {
                        castTo_name = "[I";
                    } else if (castTo_name.equals("long[]")) {
                        castTo_name = "[L";
                    } else if (castTo_name.equals("float[]")) {
                        castTo_name = "[F";
                    } else if (castTo_name.equals("double[]")) {
                        castTo_name = "[D";
                    } else {
                        castTo_name = "[L" + castTo_name.substring(0, castTo_name.length() - 2) + ";";
                    }
                }
                if (!castWhat_name.equals("null")) {
                    Object obj_to_cast = currentObjects.get(castWhat_name);
                    if (obj_to_cast == null) {
                        if (castTo_name.equals("boolean")) {
                            if (castWhat_name.equals("true")) {
                                values[i] = new Boolean(true);
                            } else if (castWhat_name.equals("false")) {
                                values[i] = new Boolean(false);
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
                            types[i] = (Class.forName(castTo_name));
                        } catch (ClassNotFoundException ex) {
                            getEngine().warn("Java class not found: " + castTo_name);
                            return false;
                        }
                    }
                } else {
                    values[i] = null;
                    if (castTo_name.equals("byte")) {
                        types[i] = Byte.TYPE;
                    } else if (castTo_name.equals("short")) {
                        types[i] = Short.TYPE;
                    } else if (castTo_name.equals("char")) {
                        types[i] = Character.TYPE;
                    } else if (castTo_name.equals("int")) {
                        types[i] = java.lang.Integer.TYPE;
                    } else if (castTo_name.equals("long")) {
                        types[i] = java.lang.Long.TYPE;
                    } else if (castTo_name.equals("float")) {
                        types[i] = java.lang.Float.TYPE;
                    } else if (castTo_name.equals("double")) {
                        types[i] = java.lang.Double.TYPE;
                    } else if (castTo_name.equals("boolean")) {
                        types[i] = java.lang.Boolean.TYPE;
                    } else {
                        try {
                            types[i] = (Class.forName(castTo_name));
                        } catch (ClassNotFoundException ex) {
                            getEngine().warn("Java class not found: " + castTo_name);
                            return false;
                        }
                    }
                }
            } else {
                Number num = (Number) castWhat;
                String castTo_name = ((Struct) castTo).getName();
                if (castTo_name.equals("byte")) {
                    values[i] = new Byte((byte) num.intValue());
                    types[i] = Byte.TYPE;
                } else if (castTo_name.equals("short")) {
                    values[i] = new Short((short) num.intValue());
                    types[i] = Short.TYPE;
                } else if (castTo_name.equals("int")) {
                    values[i] = new Integer(num.intValue());
                    types[i] = Integer.TYPE;
                } else if (castTo_name.equals("long")) {
                    values[i] = new java.lang.Long(num.longValue());
                    types[i] = java.lang.Long.TYPE;
                } else if (castTo_name.equals("float")) {
                    values[i] = new java.lang.Float(num.floatValue());
                    types[i] = java.lang.Float.TYPE;
                } else if (castTo_name.equals("double")) {
                    values[i] = new java.lang.Double(num.doubleValue());
                    types[i] = java.lang.Double.TYPE;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            getEngine().warn("Casting " + castWhat + " to " + castTo + " failed");
            return false;
        }
        return true;
    }


    /**
     *  parses return value
     *  of a method invokation
     */
    private boolean parseResult(Term id, Object obj) {
        if (obj == null) {
            //return unify(id,Term.TRUE);
            return unify(id, new Var());
        }
        try {
            if (Boolean.class.isInstance(obj)) {
                if (((Boolean) obj).booleanValue()) {
                    return unify(id, Term.TRUE);
                } else {
                    return unify(id, Term.FALSE);
                }
            } else if (Byte.class.isInstance(obj)) {
                return unify(id, new Int(((Byte) obj).intValue()));
            } else if (Short.class.isInstance(obj)) {
                return unify(id, new Int(((Short) obj).intValue()));
            } else if (Integer.class.isInstance(obj)) {
                return unify(id, new Int(((Integer) obj).intValue()));
            } else if (java.lang.Long.class.isInstance(obj)) {
                return unify(id, new alice.tuprolog.Long(((java.lang.Long) obj).longValue()));
            } else if (java.lang.Float.class.isInstance(obj)) {
                return unify(id, new alice.tuprolog.Float(((java.lang.Float) obj).floatValue()));
            } else if (java.lang.Double.class.isInstance(obj)) {
                return unify(id, new alice.tuprolog.Double(((java.lang.Double) obj).doubleValue()));
            } else if (String.class.isInstance(obj)) {
                return unify(id, new Struct((String) obj));
            } else if (Character.class.isInstance(obj)) {
                return unify(id, new Struct(((Character) obj).toString()));
            } else {
                return bindObject(id, obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private Object[] getArrayFromList(Struct list) {
        Object args[] = new Object[list.listSize()];
        Iterator it = list.listIterator();
        int count = 0;
        while (it.hasNext()) {
            args[count++] = it.next();
        }
        return args;
    }

    /**
     * registers an object with a specified id
     */
    public   boolean register(Struct id, Object obj) throws InvalidObjectIdException {
        /*
         * note that this method act on the staticObject
         * and staticObject_inverse hashmaps
         */
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        // already registered object?
        synchronized (staticObjects){
            Object aKey = staticObjects_inverse.get(obj);

            if (aKey != null) {
                // object already referenced
                return false;
            } else {
                String raw_name = alice.util.Tools.removeApices(id.getTerm().toString());
                staticObjects.put(raw_name, obj);
                staticObjects_inverse.put(obj, id);
                return true;
            }
        }
    }

    /**
     * registers an object and returns a fresh id
     */
    public   Struct register(Object obj) {
        //System.out.println("lib: "+this+" current id: "+this.id);
        Term idt = new Var();
        boolean res = bindObject(idt,obj);
        if (!res){
            return null;
        } else {
            return (Struct)(idt.getTerm());
        }
    }

    /**
     * gets a registered object
     * (returns null if not presents)
     */
    public   Object getRegisteredObject(Struct id) throws InvalidObjectIdException {
        if (!id.isGround()) {
            throw new InvalidObjectIdException();
        }
        synchronized (staticObjects){
            return staticObjects.get(alice.util.Tools.removeApices(id.toString()));
        }
    }

    /**
     * tries to bind specified id to a provided java object
     */
    protected boolean bindObject(Term id, Object obj) {
        // null object are considered to _ variable
        if (obj == null) {
            return unify(id, new Var());
        }
        // already registered object?
        synchronized (currentObjects){
            Object aKey = currentObjects_inverse.get(obj);
            if (aKey != null) {
                // object already referenced -> unifying terms
                // referencing the object
                //log("obj already registered: unify "+id+" "+aKey);
                return unify(id, (Term) aKey);
            } else {
                // object not previously referenced
                if (!id.isGround()) {
                    // get a ground term
                    Term idTerm = getGroundObject(id, getObjId());
                    unify(id, idTerm);
                    registerObject(id, obj);
                    //log("not ground id for a new obj: "+id+" as ref for "+obj);
                    return true;
                } else {
                    // verify of the id is already used
                    String raw_name = alice.util.Tools.removeApices(id.getTerm().toString());
                    Object linkedobj = currentObjects.get(raw_name);
                    if (linkedobj == null) {
                        registerObject(id, obj);
                        //log("ground id for a new obj: "+id+" as ref for "+obj);
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

    private void log(String m) {
        System.out.println("JavaLibrary: " + m);
    }


    /**
     *
     * get a ground object id related to a term id
     *
     */
    protected Term getGroundObject(Term term, int id) {
        Term t = term.getTerm();
        if (t.isVar()) {
            //return new Struct("$obj_" + id + "_"+((Var) t).getName());
            return new Struct("$obj_" + id);
        } else if (t.isStruct()) {
            Struct co = (Struct) t;
            Term[] args = new Term[co.getArity()];
            for (int i = 0; i < co.getArity(); i++) {
                args[i] = getGroundObject(co.getTerm(i), id);
            }
            return new Struct(co.getName(), args);
        } else {
            return t;
        }
    }

    protected int getObjId() {
        return id++;
    }

    protected void registerObject(Term term, Object obj) {
        synchronized (currentObjects){
            String raw_name = alice.util.Tools.removeApices(term.getTerm().toString());
            currentObjects.put(raw_name, obj);
            currentObjects_inverse.put(obj, term.getTerm());
        }
    }

    protected void unregisterObject(Term term) {
        synchronized (currentObjects){
            String raw_name = alice.util.Tools.removeApices(term.getTerm().toString());
            Object obj = currentObjects.remove(raw_name);
            if (obj != null) {
                currentObjects_inverse.remove(obj);
            }
        }
    }

    /**
     *  handling writeObject method is necessary in order to
     *  make the library serializable, 'nullyfing'  eventually
     *  objects registered in maps
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        HashMap bak00 = currentObjects;
        HashMap bak01 = currentObjects_inverse;
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
     *  handling readObject method is necessary in order to
     *  have the library reconstructed after a serialization
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        currentObjects = new HashMap();
        currentObjects_inverse = new HashMap();
        preregisterObjects();
    }

	// --------------------------------------------------
    // ---------------

    //
    private static Method lookupMethod(Class target, String name,
                                       Class[] argClasses, Object[] argValues) throws NoSuchMethodException {
        // first try for exact match
        try {
            Method m = target.getMethod(name, argClasses);
            return m;
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of luck
                return null;
            }
        }

        // go the more complicated route
        Method[] methods = target.getMethods();
        Vector goodMethods = new Vector();
        for (int i = 0; i != methods.length; i++) {
            if (name.equals(methods[i].getName()) &&
                    matchClasses(methods[i].getParameterTypes(), argClasses))
                goodMethods.addElement(methods[i]);
        }
        switch (goodMethods.size()) {
            case 0:
                // no methods have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                //    double --> float
                // (the first found is used - no most specific
                //  method algorithm is applied )

                for (int i = 0; i != methods.length; i++) {
                    if (name.equals(methods[i].getName())) {
                        Class[] types = methods[i].getParameterTypes();
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
                return (Method) goodMethods.firstElement();
            default:
                return mostSpecificMethod(goodMethods);
        }
    }

    private static Constructor lookupConstructor(Class target, Class[] argClasses, Object[] argValues) throws NoSuchMethodException {
        // first try for exact match
        try {
            return target.getConstructor(argClasses);
        } catch (NoSuchMethodException e) {
            if (argClasses.length == 0) { // if no args & no exact match, out of luck
                return null;
            }
        }

        // go the more complicated route
        Constructor[] constructors = target.getConstructors();
        Vector goodConstructors = new Vector();
        for (int i = 0; i != constructors.length; i++) {
            if (matchClasses(constructors[i].getParameterTypes(), argClasses))
                goodConstructors.addElement(constructors[i]);
        }
        switch (goodConstructors.size()) {
            case 0:
                // no constructors have been found checking for assignability
                // and (int -> long) conversion. One last chance:
                // looking for compatible methods considering also
                // type conversions:
                //    double --> float
                // (the first found is used - no most specific
                //  method algorithm is applied )

                for (int i = 0; i != constructors.length; i++) {
                    Class[] types = constructors[i].getParameterTypes();
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
                return (Constructor) goodConstructors.firstElement();
            default:
                return mostSpecificConstructor(goodConstructors);
        }
    }

    // 1st arg is from method, 2nd is actual parameters
    private static boolean matchClasses(Class[] mclasses, Class[] pclasses) {
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

    private static boolean matchClass(Class mclass, Class pclass) {
        boolean assignable = mclass.isAssignableFrom(pclass);
        if (assignable) {
            return true;
        } else {
            if (mclass.equals(java.lang.Long.TYPE) && (pclass.equals(java.lang.Integer.TYPE))) {
                return true;
            }
        }
        return false;
    }

    private static Method mostSpecificMethod(Vector methods) throws NoSuchMethodException {
        for (int i = 0; i != methods.size(); i++) {
            for (int j = 0; j != methods.size(); j++) {
                if ((i != j) &&
                        (moreSpecific((Method) methods.elementAt(i), (Method) methods.elementAt(j)))) {
                    methods.removeElementAt(j);
                    if (i > j) i--;
                    j--;
                }
            }
        }
        if (methods.size() == 1)
            return (Method) methods.elementAt(0);
        else
            throw new NoSuchMethodException(">1 most specific method");
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Method c1, Method c2) {
        Class[] p1 = c1.getParameterTypes();
        Class[] p2 = c2.getParameterTypes();
        int n = p1.length;
        for (int i = 0; i != n; i++) {
            if (!matchClass(p2[i], p1[i])) {
                return false;
            }
        }
        return true;
    }

    private static Constructor mostSpecificConstructor(Vector constructors) throws NoSuchMethodException {
        for (int i = 0; i != constructors.size(); i++) {
            for (int j = 0; j != constructors.size(); j++) {
                if ((i != j) &&
                        (moreSpecific((Constructor) constructors.elementAt(i), (Constructor) constructors.elementAt(j)))) {
                    constructors.removeElementAt(j);
                    if (i > j) i--;
                    j--;
                }
            }
        }
        if (constructors.size() == 1)
            return (Constructor) constructors.elementAt(0);
        else
            throw new NoSuchMethodException(">1 most specific constructor");
    }

    // true if c1 is more specific than c2
    private static boolean moreSpecific(Constructor c1, Constructor c2) {
        Class[] p1 = c1.getParameterTypes();
        Class[] p2 = c2.getParameterTypes();
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
    //   (required X, provided a DEFAULT -
    //        with DEFAULT to X conversion 'conceivable':
    //        for instance *double* to *int* is NOT considered good
    //
    //   required a float,  provided an  int  OK
    //   required a double, provided a   int  OK
    //   required a long,   provided a   int ==> already considered by
    //                                   previous match test
    //   required a float,  provided a   double OK
    //   required a int,    provided a   double => NOT CONSIDERED
    //   required a long,   provided a   double => NOT CONSIDERED
    //
    private static Object[] matchClasses(Class[] mclasses, Class[] pclasses, Object[] values) {
        if (mclasses.length == pclasses.length) {
            Object[] newvalues = new Object[mclasses.length];

            for (int i = 0; i != mclasses.length; i++) {
                boolean assignable = mclasses[i].isAssignableFrom(pclasses[i]);
                if (assignable ||
                        (mclasses[i].equals(java.lang.Long.TYPE) && pclasses[i].equals(java.lang.Integer.TYPE))) {
                    newvalues[i] = values[i];
                } else if (mclasses[i].equals(java.lang.Float.TYPE) &&
                        pclasses[i].equals(java.lang.Double.TYPE)) {
                    // arg required: a float, arg provided: a double
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Float(((java.lang.Double) values[i]).floatValue());
                } else if (mclasses[i].equals(java.lang.Float.TYPE) &&
                        pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a float, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Float(((java.lang.Integer) values[i]).intValue());
                } else if (mclasses[i].equals(java.lang.Double.TYPE) &&
                        pclasses[i].equals(java.lang.Integer.TYPE)) {
                    // arg required: a double, arg provided: an int
                    // so we need an explicit conversion...
                    newvalues[i] = new java.lang.Double(((java.lang.Integer) values[i]).doubleValue());
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

}

/**
 * Signature class mantains information
 * about type and value of a method
 * arguments
 */
class Signature implements Serializable {
    Class[] types;
    Object[] values;

    public Signature(Object[] v, Class[] c) {
        values = v;
        types = c;
    }

    public Class[] getTypes() {
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

/** used to load new classes without touching system class loader */
class ClassLoader extends java.lang.ClassLoader {
}

/**
 * Information about an EventListener
 */
class ListenerInfo implements Serializable {
    public String listenerInterfaceName;
    public EventListener listener;
    //public String eventName;
    public String eventFullClass;

    public ListenerInfo(EventListener l, String eventClass, String n) {
        listener = l;
        //this.eventName=eventName;
        this.eventFullClass = eventClass;
        listenerInterfaceName = n;
    }
}

