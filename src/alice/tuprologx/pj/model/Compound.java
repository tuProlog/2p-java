/*
 * CompoundRest.java
 *
 * Created on 13 marzo 2007, 12.51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package alice.tuprologx.pj.model;


import java.util.*;
/**
 *
 * @author Maurizio
 */
public abstract class Compound<X extends Compound<?>> extends Term<X> {
//public abstract class Compound<X extends Term<?>> extends Term<Compound<X>> {
    public abstract int arity();
    
    public abstract String getName();
}
