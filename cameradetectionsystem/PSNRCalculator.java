package cameradetectionsyatem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class PSNRCalculator {

    // Method to calculate MSE between two images
    private static double calculateMSE(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        if (width != img2.getWidth() || height != img2.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions.");
        }

        long sumSquaredError = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;

                int diffR = r1 - r2;
                int diffG = g1 - g2;
                int diffB = b1 - b2;

                // Sum the square of differences for each color channel
                sumSquaredError += diffR * diffR + diffG * diffG + diffB * diffB;
            }
        }

        // Return average MSE (Mean Squared Error)
        return sumSquaredError / (3.0 * width * height);
    }

    // Method to calculate PSNR
    public static double calculatePSNR(BufferedImage img1, BufferedImage img2) {
        double mse = calculateMSE(img1, img2);
        if (mse == 0) {
            return Double.POSITIVE_INFINITY;  // Perfect match
        }
        double maxPixelValue = 255.0;
        return 10 * Math.log10((maxPixelValue * maxPixelValue) / mse);
    }

    // Simplified Method to calculate SSIM (Structural Similarity Index)
    public static double calculateSSIM(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        if (width != img2.getWidth() || height != img2.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions.");
        }

        double C1 = 6.5025, C2 = 58.5225;
        double meanX = 0, meanY = 0;
        double varX = 0, varY = 0, covariance = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                int gray1 = (rgb1 >> 16) & 0xff; // Convert to grayscale
                int gray2 = (rgb2 >> 16) & 0xff; // Convert to grayscale

                meanX += gray1;
                meanY += gray2;

                varX += gray1 * gray1;
                varY += gray2 * gray2;
                covariance += gray1 * gray2;
            }
        }

        int numPixels = width * height;
        meanX /= numPixels;
        meanY /= numPixels;

        varX = varX / numPixels - meanX * meanX;
        varY = varY / numPixels - meanY * meanY;
        covariance = covariance / numPixels - meanX * meanY;

        // SSIM formula
        return ((2 * meanX * meanY + C1) * (2 * covariance + C2)) / 
               ((meanX * meanX + meanY * meanY + C1) * (varX + varY + C2));
    }

    // Method to copy image to the target directory
    private static void copyImageToDirectory(File source, File targetDirectory) throws IOException {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();  // Create target directory if it doesn't exist
        }
        File targetFile = new File(targetDirectory, source.getName());
        Files.copy(source.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Main method to handle the process
    public static void main(String[] args) {
        // Directory where images are stored
        String snapshotDirectoryPath = "E:\\Snapshots";
        String targetDirectoryPath = "E:\\FilteredImages";  // Directory to save copied images
        double psnrThreshold = 10.0;  // Set your PSNR threshold value for high-quality images
        double ssimThreshold = 0.3;   // Set your SSIM threshold value for high-quality images

        // Read images from the Snapshots directory
        File snapshotDirectory = new File(snapshotDirectoryPath);
        if (!snapshotDirectory.isDirectory()) {
            System.out.println("Snapshots directory not found.");
            return;
        }

        // Get all image files in the directory
        File[] imageFiles = snapshotDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

        if (imageFiles == null || imageFiles.length < 2) {
            System.out.println("Not enough images in the Snapshots directory.");
            return;
        }

        try {
            // Load the first image to compare with others
            BufferedImage img1 = ImageIO.read(imageFiles[0]);  // First image in the directory

            System.out.println("Comparing first image: " + imageFiles[0].getName() + " with others...");

            // Loop through all the other images and compare with img1
            for (int i = 1; i < imageFiles.length; i++) {
                BufferedImage img2 = ImageIO.read(imageFiles[i]);  // Load the next image

                // Calculate PSNR and SSIM
                double psnrValue = calculatePSNR(img1, img2);
                double ssimValue = calculateSSIM(img1, img2);

                System.out.println("PSNR between " + imageFiles[0].getName() + " and " + imageFiles[i].getName() + ": " + psnrValue);
                System.out.println("SSIM between " + imageFiles[0].getName() + " and " + imageFiles[i].getName() + ": " + ssimValue);

                // If both PSNR and SSIM are above their respective thresholds, copy the image
                if (psnrValue >= psnrThreshold && ssimValue >= ssimThreshold) {
                    copyImageToDirectory(imageFiles[i], new File(targetDirectoryPath));
                    System.out.println(imageFiles[i].getName() + " copied to " + targetDirectoryPath + " due to high PSNR (" + psnrValue + ") and SSIM (" + ssimValue + ")");
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading images: " + e.getMessage());
        }
    }
}
