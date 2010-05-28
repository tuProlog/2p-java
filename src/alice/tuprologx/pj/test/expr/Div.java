/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.annotations.Termifiable;

@Termifiable(predicate="div")
public class Div extends BinaryExpr {

    public Div() {}

    public Div(Object left, Object right) {
        super(left, right);
    }

    public String toString() {
        return "/("+left+","+right+")";
    }

    public double eval() {
        return left.eval() / right.eval();
    }
}