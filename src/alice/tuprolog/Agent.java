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
package alice.tuprolog;

import java.io.*;

/**
 * Provides a prolog virtual machine embedded in a separate thread.
 * It needs a theory and optionally a goal.
 * It parses the theory, solves the goal and stops.
 *
 * @see alice.tuprolog.Prolog
 *
 *
 *
 */
public class Agent {

    private Prolog core;
    private String      theoryText;
    private InputStream theoryInputStream;
    private String      goalText;

    /**
     * Builds a prolog agent providing it a theory
     *
     * @param theory the text representing the theory
     */
    public Agent(String theory){
        theoryText=theory;
        core=new Prolog();
    }

    /**
     * Builds a prolog agent providing it a theory and a goal
     */
    public Agent(String theory,String goal){
        theoryText=theory;
        goalText=goal;
        core=new Prolog();
    }

    /**
     * Constructs the Agent with a theory provided
     * by an input stream
     */
    public Agent(InputStream is){
        theoryInputStream=is;
        core=new Prolog();
    }

    /**
     * Constructs the Agent with a theory provided
     * by an input stream and a goal
     */
    public Agent(InputStream is,String goal){
        theoryInputStream=is;
        goalText=goal;
        core=new Prolog();
    }

    /**
     * Starts agent execution
     */
    final  public void spawn(){
        new Agent.AgentThread(this).start();
    }

    private void body(){
        OutputListener l=new OutputListener() {
            public void onOutput(OutputEvent ev) {
                System.out.print(ev.getMsg());
        }};
        core.addOutputListener(l);
        try {
            if (theoryText==null){
                core.setTheory(new Theory(theoryInputStream));
            }  else {
                core.setTheory(new Theory(theoryText));
            }
            if (goalText!=null){
                core.solve(goalText);
            }
        } catch (Exception ex){
            System.err.println("invalid theory or goal.");
            ex.printStackTrace();
        }
    }

    final class AgentThread extends Thread {
        Agent agent;
        AgentThread(Agent agent){
            this.agent=agent;
        }
        final public void run(){
            agent.body();
        }
    }

    public static void main(String args[]){
        if (args.length==1 || args.length==2){
            FileInputStream is;
            try {
                if (args.length==1){
                    new Agent(new BufferedInputStream(ClassLoader.getSystemResourceAsStream(args[0]))).spawn();
                } else {
                    new Agent(new BufferedInputStream(ClassLoader.getSystemResourceAsStream(args[0])),args[1]).spawn();
                }
            } catch (Exception ex){
                System.err.println("invalid theory.");
            }
        } else {
            System.err.println("args: <theory file> { goal }");
            System.exit(-1);
        }
    }
}