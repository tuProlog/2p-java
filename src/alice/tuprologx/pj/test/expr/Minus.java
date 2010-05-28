/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.annotations.Termifiable;

@Termifiable(predicate="minus")
public class Minus extends BinaryExpr {

    public Minus() {}

    public Minus(Object left, Object right) {
        super(left, right);
    }

    public String toString() {
        return "-("+left+","+right+")";
    }

    public double eval() {
        return left.eval() - right.eval();
    }
}