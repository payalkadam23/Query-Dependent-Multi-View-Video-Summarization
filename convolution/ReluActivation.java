/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConvNuralNetwork.Convolution;

/**
 *
 * @author virendra
 */
public class ReluActivation {
    public static int ReluActivationval = 0; 
    public static int ActivationFunction(int val)
    {
        
        if (val < 0) {
            ReluActivationval = 0;
        }
        else
            ReluActivationval = val;
    return ReluActivationval;
    
    }
}
