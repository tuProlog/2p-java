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
package alice.tuprologx.lib;
import  alice.tuprolog.*;
import java.io.*;
import  java.util.*;
import alice.logictuple.*;
import alice.respect.TupleCentre;
import alice.respect.TupleCentreId;

/**
 *
 * this library allows the interaction
 * of prolog (and java) agents thorugh
 * a locally defined tuple centre
 *
 * refer to tuplemedium documentation
 * for understanding of coordination primitives
 *
 */
public class RespectTCLibrary extends Library {

    private TupleCentre medium;

    public RespectTCLibrary(){
        try {
            medium=new TupleCentre(new TupleCentreId("prolog"));
        } catch(Exception ex){
        }
    }

    public void setMedium(TupleCentre centre){
        medium=centre;
    }

    public boolean out_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            medium.out(new LogicTuple(arg0));
            return true;
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean in_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            LogicTuple tuple=(LogicTuple)medium.in(new LogicTuple(arg0));
            return (unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean inp_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            LogicTuple tuple=(LogicTuple)medium.inp(new LogicTuple(arg0));
            return (tuple!=null && unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean rd_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            LogicTuple tuple=(LogicTuple)medium.rd(new LogicTuple(arg0));
            return (unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean rdp_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            LogicTuple tuple=(LogicTuple)medium.rdp(new LogicTuple(arg0));
            return (tuple!=null && unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean set_spec_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            medium.setSpec(alice.util.Tools.removeApices(arg0.toString()));
            return true;
        } catch(Exception x) {
            x.printStackTrace();
            return false;
        }
    }

    public boolean get_spec_1(Struct g){
        Term arg0 = g.getTerm(0);
        try {
            String spec=medium.getSpec();
            return (unify(arg0,new Struct(spec)));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }
}