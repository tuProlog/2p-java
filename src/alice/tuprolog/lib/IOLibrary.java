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

/**
 * This class provides basic I/O predicates.
 *
 * Library/Theory Dependency: BasicLibrary
 *
 *
 *
 */
public class IOLibrary extends Library {

    protected String inputStreamName = "stdin";
    protected InputStream inputStream = System.in;
    protected String outputStreamName = "stdout";
    protected OutputStream outputStream = System.out;
    private Random gen = new Random();


    public IOLibrary() {
        gen.setSeed(System.currentTimeMillis());
    }

    public boolean see_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        if (!arg0.isAtom()) {
            return false;
        }
        if (((Struct) arg0).getName().equals("stdin")) {
            inputStream = System.in;
        } else {
            inputStream = new FileInputStream(((Struct) arg0).getName());
        }
        inputStreamName = ((Struct) arg0).getName();
        return true;
    }

    public boolean seen_0(Struct g) throws Exception {
        inputStream.close();
        return true;
    }

    public boolean seeing_1(Struct g) {
        Term arg0 = g.getTerm(0);
        return unify(arg0, new Struct(inputStreamName));
    }

    public boolean tell_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        if (!arg0.isAtom()) {
            return false;
        }
        if (((Struct) arg0).getName().equals("stdout")) {
            outputStream = System.out;
        } else {
            outputStream = new FileOutputStream(((Struct) arg0).getName());
        }
        outputStreamName = ((Struct) arg0).getName();
        return true;
    }

    public boolean told_0(Struct g) throws Exception {
        outputStream.close();
        return true;
    }

    public boolean telling_1(Struct g) {
        Term arg0 = g.getTerm(0);
        return unify(arg0, new Struct(outputStreamName));
    }

    public boolean put_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        if (!arg0.isAtom()) {
            return false;
        } else {
            String ch = ((Struct) arg0).getName();
            if (ch.length() > 1) {
                return false;
            } else {
                if (outputStreamName.equals("stdout")) {
                    getEngine().stdOutput(ch);
                } else {
                    outputStream.write((byte) ch.charAt(0));
                }
                return true;
            }
        }
    }

    public boolean get0_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        int ch = inputStream.read();
        if (ch == -1) {
            return unify(arg0, new Int(-1));
        } else {
            return unify(arg0, new Struct(new Character((char) ch).toString()));
        }
    }

    public boolean get_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        int ch = 0;
        do {
            ch = inputStream.read();
        } while (ch < 0x20 && ch >= 0);
        if (ch == -1) {
            return unify(arg0, new Int(-1));
        } else {
            return unify(arg0, new Struct(new Character(((char) ch)).toString()));
        }
    }

    public boolean tab_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        if (!arg0.isNumber()) {
            return false;
        } else {
            int n = ((alice.tuprolog.Number) arg0).intValue();
            if (outputStreamName.equals("stdout")) {
                for (int i = 0; i < n; i++) {
                    getEngine().stdOutput("\t");
                }
            } else {
                for (int i = 0; i < n; i++) {
                    outputStream.write(0x20);
                }
            }
            return true;
        }
    }

    public boolean read_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        int ch = 0;
        String st = "";
        do {
            ch = inputStream.read();
            if (ch == -1 || ch=='.') {
                break;
            }
            st += new Character(((char) ch)).toString();
        } while (true);

        return unify(arg0, getEngine().toTerm(st));
    }

    public boolean write_1(Struct g) throws Exception {
        Term arg0 = g.getTerm(0);
        if (outputStreamName.equals("stdout")) {
            getEngine().stdOutput(arg0.toString());
        } else {
            outputStream.write(arg0.toString().getBytes());
        }
        return true;
    }


    public boolean nl_0(Struct g) throws Exception {
        if (outputStreamName.equals("stdout")) {
            getEngine().stdOutput("\n");
        } else {
            outputStream.write('\n');
        }
        return true;
    }

    /**
     * reads a source text from a file.
     * <p>
     * It's useful used with agent predicate:
     * text_from_file(File,Source), agent(Source).
     */
    public boolean text_from_file_2(Struct g) {
        Term fileName = g.getTerm(0);
        if (!fileName.isStruct()) {
            return false;
        } else {
            try {
                Struct goal = new Struct(alice.util.Tools.loadText(
                        alice.util.Tools.removeApices(((Struct) fileName).toString())));
                return unify(g.getTerm(1), goal);
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // miscellanea

    public boolean rand_float_1(Struct g) {
        return unify(g.getTerm(0), new alice.tuprolog.Double(gen.nextFloat()));
    }

    public boolean rand_int_2(Struct g) {
        Term arg = g.getTerm(0);
        if (!arg.isNumber()) {
            return false;
        }
        return unify(g.getTerm(1), new Int(gen.nextInt(((alice.tuprolog.Number) arg).intValue())));
    }

    public String getTheory() {
        return
                "consult(File) :- text_from_file(File,Text), add_theory(Text).\n" +
                "solve_file(File,Goal) :- text_from_file(File,Text),text_term(Text,Goal),call(Goal).\n" +
                "agent_file(X)  :- text_from_file(X,Y),agent(Y).\n";
    }


    // to allow serialization -> nullify streams before serialization

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        InputStream inputStreamBak = inputStream;
        OutputStream outputStreamBak = outputStream;
        inputStream = null;
        outputStream = null;
        try {
            out.defaultWriteObject();
        } catch (IOException ex) {
            inputStream = inputStreamBak;
            outputStream = outputStreamBak;
            throw new IOException();
        }
        inputStream = inputStreamBak;
        outputStream = outputStreamBak;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (outputStreamName.equals("user")) {
            outputStream = System.out;
        }
        if (inputStreamName.equals("user")) {
            inputStream = System.in;
        }
    }
}