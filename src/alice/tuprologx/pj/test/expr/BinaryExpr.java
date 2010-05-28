/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.expr;

/**
 *
 * @author maurizio
 */
public abstract class BinaryExpr implements IExpr {
    IExpr left;
    IExpr right;

    public BinaryExpr() {}

    public BinaryExpr(Object left, Object right) {
        this.left = (left instanceof Integer) ? new Num((Integer)left) : (IExpr) left;
        this.right = (left instanceof Integer) ? new Num((Integer)right) : (IExpr) right;
    }

    public IExpr getLeft() {return left;}
    public IExpr getRight() {return right;}
    public void setLeft(IExpr _left) {left = _left;}
    public void setRight(IExpr _right) {right = _right;}
}
