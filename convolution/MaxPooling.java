package ConvNuralNetwork.Convolution;

public class MaxPooling {
    public static int MaxPoolingMatVal = 0;

    public static int MaxpoolingResult(int[][] MaxpoolingMat) {
        MaxPoolingMatVal = findMax(MaxpoolingMat);
        return MaxPoolingMatVal;
    }

    static int findMax(int mat[][]) {
        int maxElement = Integer.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (mat[i][j] > maxElement) {
                    maxElement = mat[i][j];
                }
            }
        }
        return maxElement;
    }
}
