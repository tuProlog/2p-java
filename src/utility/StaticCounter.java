/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class StaticCounter {
    private static int[] parameters={0,0,0,0,0,0,0,0,0,0};

    public static void inc(int index){
        parameters[index]++;
    }

    public static int get(int index){
        return parameters[index];
    }
    public static int[] getAll(){
        return parameters;
    }
    public static void printAll(){
        for (int i=0; i<parameters.length;i++){
            System.out.print(parameters[i]);
            if(i<parameters.length-1) System.out.print(", ");
        }
        System.out.print("\n");
    }
}
