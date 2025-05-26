package ConvNuralNetwork.Convolution;

import ConvNuralNetwork.Convolution.POJO.JAVABeans;
import java.util.Vector;

public class Flatterning {

    public static void FlatternMat(int MaxPoolVal) {
        Vector<Integer> FlatternVector = JAVABeans.getFlatternList();
        FlatternVector.add(MaxPoolVal);
        JAVABeans.setFlatternList(FlatternVector);
    }
}
