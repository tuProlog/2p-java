package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.annotations.Termifiable;

@Termifiable(predicate="plus")
public class Plus extends BinaryExpr {

    public Plus() {}

    public Plus(Object left, Object right) {
        super(left, right);
    }

    public String toString() {
        return "+("+left+","+right+")";
    }

    public double eval() {
        return left.eval() + right.eval();
    }
}