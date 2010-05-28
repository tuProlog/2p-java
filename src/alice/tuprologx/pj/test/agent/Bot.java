/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.agent;

import java.util.*;

import alice.tuprologx.pj.annotations.*;
import alice.tuprologx.pj.model.*;
import alice.tuprologx.pj.engine.*;

/**
 *
 * @author maurizio
 */
public class Bot {

    Bot(Maze maze) {
        this.maze = maze;
        currentPath = new Stack<String>();
        visitedNodes = new ArrayList<String>();
    }

    Maze maze;

    Stack<String> currentPath;
    ArrayList<String> visitedNodes;

    void explore() {
        while (!maze.is_exit().toJava()) {
            System.out.println("[BOT]: I am here " + maze.currentSite.toJava());
            currentPath.push((String)maze.currentSite.toJava());
            visitedNodes.add((String)maze.currentSite.toJava());
            boolean moved = false;
            for (Atom a : maze.reachable_sites()) {
                String node = a.toJava();                
                if (!visitedNodes.contains(node)) {
                    System.out.println("[BOT]: Moving to node "+node);
                    maze.moveTo(node);
                    moved = true;
                    break;
                }
            }
            if (!moved) {                
                currentPath.pop();
                System.out.println("[BOT]: Going back to: " + currentPath.lastElement());
                maze.moveTo(currentPath.pop());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String topology = "porta(a,g).\n" +
                          "porta(b,a).\n"+
                         "porta(a,d).\n"+
                         "porta(e,b).\n"+
                         "porta(g,h).\n"+
                         "porta(e,f).\n"+
                         "porta(f,i).";
        Bot b = new Bot((Maze)PJ.newInstance(Maze.class,new Theory(topology)));
        b.explore();
    }
}
