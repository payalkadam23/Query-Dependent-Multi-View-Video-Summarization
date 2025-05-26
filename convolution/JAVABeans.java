package ConvNuralNetwork.Convolution.POJO;

import java.util.Vector;

public class JAVABeans {
    private static Vector<Integer> FlatternList = new Vector<>();
   

    private static Vector<Integer> CNNFeatures = new Vector<>();

    public static Vector<Integer> getFlatternList() {
        return FlatternList;
    }

    public static void setFlatternList(Vector<Integer> FlatternList) {
        JAVABeans.FlatternList = FlatternList;
    }

 

   
    

    public static Vector<Integer> getCNNFeatures() {
        return CNNFeatures;
    }

    public static void setCNNFeatures(Vector<Integer> CNNFeatures) {
        JAVABeans.CNNFeatures = CNNFeatures;
    }
}
