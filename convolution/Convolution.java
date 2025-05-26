package ConvNuralNetwork.Convolution;

import ConvNuralNetwork.Convolution.Kernals.Kernals;
import ConvNuralNetwork.Convolution.POJO.JAVABeans;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.imageio.ImageIO;

public class Convolution {

    public static Vector<Integer> GenerateConvMatrix(String path) {
        JAVABeans.setFlatternList(new Vector<Integer>());
        try {
            BufferedImage image = ImageIO.read(new File(path));
            int width = image.getWidth();
            int height = image.getHeight();

            // Apply convolution and max-pooling
            for (int i = 0; i <= height - 3; i += 3) {
                for (int j = 0; j <= width - 3; j += 3) {
                    int[][] matrixConv = new int[3][3];
                    for (int m = 0; m < 3; m++) {
                        for (int n = 0; n < 3; n++) {
                            int pixel = image.getRGB(j + n, i + m);
                            Color color = new Color(pixel);
                            int temp = (color.getRed() > 128) ? 1 : 0;
                            matrixConv[m][n] = temp;
                        }
                    }
                    int maxPoolVal = MaxPooling.MaxpoolingResult(matrixConv);
                    Flatterning.FlatternMat(maxPoolVal);
                }
            }

            System.out.println("Array Size: " + JAVABeans.getFlatternList().size());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return JAVABeans.getFlatternList();
    }

    public static void main(String[] args) {
        // Test your function here if needed
    }
}
