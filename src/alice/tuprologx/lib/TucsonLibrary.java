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
import alice.logictuple.*;
import alice.tucson.api.*;

/**
 *
 * This library enables the access to the TuCSoN
 * coordination infrastructure.
 *
 */
public class TucsonLibrary extends Library {

    private TucsonContext context;
    private AgentId agentId;

    public TucsonLibrary() throws TucsonException {
    }

    public void setContext(TucsonContext context){
        this.context=context;
    }

    public String getTheory(){
        return
            ":- op(  600, xfx,  '?'). \n"+
            ":- op(  550, xfx,  '@'). \n"+
            //"TC@InetAddr ? out(T) :- !,out(T,TC@InetAddr). \n"+
            //"TC@InetAddr ? in(T)  :- !,in(T,TC@InetAddr). \n"+
            //"TC@InetAddr ? inp(T) :- !,inp(T,InetAddr,TC). \n"+
            //"TC@InetAddr ? rd(T)  :- !,rd(T,InetAddr,TC). \n"+
            //"TC@InetAddr ? rdp(T) :- !,rdp(T,InetAddr,TC). \n"+
            //"TC@InetAddr ? set_spec(T) :- !,set_spec(T,InetAddr,TC). \n"+
            //"TC@InetAddr ? get_spec(T) :- !,get_spec(T,InetAddr,TC). \n"+
            "TC ? out(T) :- out(T,TC). \n"+
            "TC ? in(T)  :- in(T,TC). \n"+
            "TC ? inp(T) :- inp(T,TC). \n"+
            "TC ? rd(T)  :- rd(T,TC). \n"+
            "TC ? rdp(T) :- rdp(T,TC). \n"+
            "TC ? set_spec(T)  :- set_spec(T,TC). \n"+
            "TC ? get_spec(T) :- get_spec(T,TC). \n"+
            "out(T):-out(T,default@localhost). \n"+
            "in(T):-in(T,default@localhost). \n"+
            "rd(T):-rd(T,default@localhost). \n"+
            "inp(T):-inp(T,default@localhost). \n"+
            "rdp(T):-rdp(T,default@localhost). \n"+
            "set_spec(T):-set_spec(T,default@localhost). \n"+
            "get_spec(T):-get_spec(T,default@localhost). \n"+
            "enter_context([]):- !,enter_default_context.\n"+
            "enter_context([agent_id(Id)|_]):- !,enter_context2(Id).\n"+
            "enter_context([_|L]):- enter_context(L).\n";
    }


    public boolean out_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            context.out(tid, new LogicTuple(arg0));
            return true;
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean in_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            LogicTuple tuple=context.in(tid,new LogicTuple(arg0));
            return unify(arg0,tuple.toTerm());
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean inp_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            LogicTuple tuple=context.inp(tid, new LogicTuple(arg0));
            return (tuple!=null && unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean rd_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            LogicTuple tuple=context.rd(tid, new LogicTuple(arg0));
            return unify(arg0,tuple.toTerm());
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean rdp_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            LogicTuple tuple=context.rdp(tid, new LogicTuple(arg0));
            return (tuple!=null && unify(arg0,tuple.toTerm()));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean set_spec_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            System.out.println(tid);
            context.setSpec(tid, alice.util.Tools.removeApices(arg0.toString()));
            return true;
        } catch(Exception x) {
            x.printStackTrace();
            return false;
        }
    }

    public boolean get_spec_2(Struct g){
        Term arg0 = g.getTerm(0);
        Term arg1 = g.getTerm(1);
        try {
            TupleCentreId tid=new TupleCentreId(arg1);
            if (context==null){
                context=Tucson.enterDefaultContext();
            }
            String spec = context.getSpec(tid);
            return (unify(arg0,new Struct(spec)));
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean exit_context_0(Struct g){
        try {
            context.exit();
            return true;
        } catch(Exception x) {
            //x.printStackTrace();
            return false;
        }
    }

    public boolean enter_default_context_0(Struct g){
        if (context!=null){
            try {
                context.exit();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        try {
            context=Tucson.enterDefaultContext();
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public boolean enter_context2_1(Struct g){
        Term arg0 = g.getTerm(0);
        if (context!=null){
            try {
                context.exit();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        try {
            context=Tucson.enterContext(new DefaultContextDescription(new AgentId(arg0)));
            return true;
        } catch (Exception ex){
            return false;
        }
    }
}