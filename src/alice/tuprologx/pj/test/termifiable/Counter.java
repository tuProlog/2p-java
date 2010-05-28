/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.termifiable;

import alice.tuprologx.pj.annotations.Termifiable;

/**
 *
 * @author maurizio
 */
@Termifiable(predicate="counter")
public class Counter {
    private Integer value;

    Counter(Integer i) {
        setValue(i);
    }

    public Counter() {        
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String toString() {
        return "Counter(value:"+value+")";
    }
}
