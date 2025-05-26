package cameradetectionsyatem;

import ConvNuralNetwork.Convolution.Convolution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class camera {

    static String modefile = "";
    static int globalFeatureNumber = 1; // Global feature number across all images

    public static void main(String[] args) throws IOException {
        // Paths to directories
        String snapshotsDir = "E:\\Snapshots";
        String croppedImagesDir = "E:\\CroppedImages";

        // Process snapshots directory
        processImages(snapshotsDir, "train");

        // Process cropped images directory
        processImages(croppedImagesDir, "test");

        System.out.println("Processing complete.");
    }

    public static void processImages(String directoryPath, String mode) throws IOException {
        File folder = new File(directoryPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                clearFeatureFiles(mode); // Clear existing features for this mode

                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String imagePath = file.getAbsolutePath();
                    Vector<Integer> CNNFeatures = Convolution.GenerateConvMatrix(imagePath);

                    // Write features to file starting from 1 for each image
                    writeFeaturesToFile(CNNFeatures, mode);
                    
                    // Reset global feature number for the next image
                    globalFeatureNumber = 1;
                }
            } else {
                System.out.println("The folder is empty: " + directoryPath);
            }
        } else {
            System.out.println("The specified folder does not exist or is not a directory: " + directoryPath);
        }
    }

    public static void writeFeaturesToFile(Vector<Integer> CNNFeatures, String mode) throws IOException {
        if (mode.equals("train")) {
            modefile = "D:\\camera\\train.txt";
        } else {
            modefile = "D:\\camera\\test.txt";
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(modefile), true));

        // Write features prefixed with sequential numbers for each image
        for (int i = 0; i < CNNFeatures.size(); i++) {
            int feature = CNNFeatures.elementAt(i);
            bw.write(globalFeatureNumber + ":" + feature + ",");
            globalFeatureNumber++;
        }
        bw.newLine();

        bw.close();
    }

    public static void clearFeatureFiles(String mode) {
        try {
            String modefile;
            if (mode.equals("train")) {
                modefile = "D:\\camera\\train.txt";
            } else {
                modefile = "D:\\camera\\test.txt";
            }

            File file = new File(modefile);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
                System.out.println("Cleared existing features from " + modefile);
                globalFeatureNumber = 1; // Reset global feature number after clearing files
            }
        } catch (IOException e) {
            System.out.println("Error clearing feature files: " + e.getMessage());
        }
    }
}
