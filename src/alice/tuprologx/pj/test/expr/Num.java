/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package alice.tuprologx.pj.test.expr;

import alice.tuprologx.pj.annotations.Termifiable;
/**
 *
 * @author maurizio
 */
@Termifiable(predicate="num")
public class Num implements IExpr {
    Integer num;

    public Num() {}

    Num(int i) {
        this.num = i;
    }

    public double eval() {
        return num;
    }

    public String toString() {
        return ""+num;
    }

    public Integer getNum() {return num;}
    public void setNum(Integer num) {this.num = num;}
}
