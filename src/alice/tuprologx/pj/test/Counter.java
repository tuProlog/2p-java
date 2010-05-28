/*
 * Pippo.java
 *
 * Created on March 12, 2007, 5:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test;

import alice.tuprologx.pj.annotations.Termifiable;

/**
 *
 * @author maurizio
 */
/*@Termifiable*/ public class Counter {
    
    public Counter() {
        value = 0;
    }
    
    public Counter(int i) {
        value = i;
    }
    
    private int value;
        
    public int getValue() {return value;}

    public void setValue(int value) {this.value = value;}

    public String toString() {
        return "Counter="+value;
    }
}