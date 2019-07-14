/*
 *   Tools.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package alice.util;

import java.io.*;

/**
 * miscellaneous static services
 */
public class Tools {

    private Tools() {
        throw new IllegalStateException();
    }

    /**
     * loads a text file and returns its
     * content as string
     */
    public static String loadText(String fileName) throws IOException {
        //FileInputStream is=new FileInputStream(fileName);
        try {
            BufferedInputStream is = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(fileName));
            byte[] info = new byte[is.available()];
            is.read(info);
            return new String(info);
        } catch (Exception ex) {
        }
        // resource not found among system resources: try as a file
        try {
            FileInputStream is = new FileInputStream(fileName);
            byte[] info = new byte[is.available()];
            is.read(info);
            is.close(); // ED 2013-05-21
            return new String(info);
        } catch (Exception ex) {
        }
        throw new IOException("File not found.");
    }

    /**
     * loads a text file from an InputStream
     */
    public static String loadText(InputStream is) throws IOException {
        byte[] info = new byte[is.available()];
        is.read(info);
        return new String(info);
    }

    /**
     * give a command line argument list, this method gets the option
     * of the specified prefix
     */
    public static String getOpt(String[] args, String prefix) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(prefix)) {
                return args[i + 1];
            }
        }
        return null;
    }

    /**
     * give a command line argument list, this method tests for
     * the presence of the option of the specified prefix
     */
    public static boolean isOpt(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static String removeApices(String st) {
        if (st.startsWith("'") && st.endsWith("'")) {
            return st.substring(1, st.length() - 1);
        } else {
            return st;
        }
    }

    private static char escapedOf(int repr) {
        switch (repr) {
            case 't': return '\t';
            case 'n': return '\n';
            case 'r': return '\r';
            case 'f': return '\f';
            case 'b': return '\b';
            case '\\': return '\\';
            case '"': return '"';
            case '\'': return '\'';
            default: throw new IllegalArgumentException("Invalid escape char: " + repr);
        }
    }

    public static String unescape(String string) {
        StringReader sr = new StringReader(string);
        StringBuilder sb = new StringBuilder(string.length());
        try {
            for (int c; (c = sr.read()) >= 0;) {
                if (c == '\\') {
                    if ((c = sr.read()) >= 0) {
                        sb.append(escapedOf(c));
                    } else {
                        sb.append('\\');
                    }
                } else {
                    sb.append((char) c);
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
