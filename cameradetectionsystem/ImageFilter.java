
package cameradetectionsyatem;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.Dimension;
import java.awt.Toolkit;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ImageFilter {
    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    static ArrayList<String> matchedImagePaths = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        filterImages();
        createVideo();
    }

    public static void filterImages() throws IOException {
        String folderPath = "E:\\FilteredImages";
        int imagesCopied = 0; // Track the total number of images copied

        // Create a File object with the folder path
        File folder = new File(folderPath);

        // Check if the specified path exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                // Map to store image file paths and their content hash codes
                Map<Integer, List<String>> imageHashes = new HashMap<>();

                // Iterate through the files and calculate hash codes
                for (File snapshotFile : files) {
                    int imageHashCode = getImageHashCode(snapshotFile);
                    // Check if the hash code already exists
                    if (!imageHashes.containsKey(imageHashCode)) {
                        // If not, add it to the map with an empty list
                        imageHashes.put(imageHashCode, new ArrayList<>());
                    }
                    // Add the file path to the list for this hash code
                    imageHashes.get(imageHashCode).add(snapshotFile.getAbsolutePath());
                }

                // Iterate through the map and compare images using Jaccard similarity with SIFT
                for (List<String> imagePathList : imageHashes.values()) {
                    if (imagePathList.size() > 1) {
                        // Calculate Jaccard similarity for each pair of images
                        for (int i = 0; i < imagePathList.size(); i++) {
                            for (int j = i + 1; j < imagePathList.size(); j++) {
                                File imageFile1 = new File(imagePathList.get(i));
                                File imageFile2 = new File(imagePathList.get(j));
                                copyMatchedImages(imageFile1, imageFile2);
                            }
                        }
                    } else {
                        // If there's only one instance, copy it to the duplicates directory
                        File imageFile = new File(imagePathList.get(0));
                        copyFile(imageFile, "E:\\Duplicates");
                        imagesCopied++;
                        System.out.println("Copied image: " + imageFile.getName() + " to E:\\Duplicates");
                    }
                }

                System.out.println("Total images copied: " + imagesCopied);
            } else {
                System.out.println("The folder is empty.");
            }
        } else {
            System.out.println("The specified folder does not exist or is not a directory.");
        }
    }

    private static void copyMatchedImages(File imageFile1, File imageFile2) {
        double jaccardSimilarity = calculateJaccardSimilarityWithSIFT(imageFile1, imageFile2);
        if (jaccardSimilarity >= 0.5 && siftMatches(imageFile1, imageFile2)) {
            // If both similarity conditions are met, copy both images
            copyFile(imageFile1, "E:\\MatchedImages");
            copyFile(imageFile2, "E:\\MatchedImages");
            System.out.println("Copied similar images: " + imageFile1.getName() + " and " + imageFile2.getName());
        }
    }

    private static double calculateJaccardSimilarityWithSIFT(File imageFile1, File imageFile2) {
        try {
            // Read images and convert to sets of pixels
            Set<String> pixelSet1 = getImagePixelSet(imageFile1);
            Set<String> pixelSet2 = getImagePixelSet(imageFile2);

            // Calculate intersection and union sizes
            Set<String> intersection = new HashSet<>(pixelSet1);
            intersection.retainAll(pixelSet2);
            Set<String> union = new HashSet<>(pixelSet1);
            union.addAll(pixelSet2);

            // Calculate Jaccard similarity
            double jaccardSimilarity = (double) intersection.size() / union.size();
            System.out.println("Jaccard similarity between " + imageFile1.getName() + " and " + imageFile2.getName() + ": " + jaccardSimilarity);
            return jaccardSimilarity;
        } catch (IOException e) {
            System.err.println("Error reading image files: " + e.getMessage());
            return 0.0; // Return 0 in case of errors
        }
    }

    private static boolean siftMatches(File imageFile1, File imageFile2) {
        // Load images as OpenCV Mats
        Mat img1 = Highgui.imread(imageFile1.getAbsolutePath());
        Mat img2 = Highgui.imread(imageFile2.getAbsolutePath());

        if (img1.empty() || img2.empty()) {
            System.err.println("Error: Could not read images.");
            return false;
        }

        // Convert images to grayscale
        Mat grayImg1 = new Mat();
        Mat grayImg2 = new Mat();
        Imgproc.cvtColor(img1, grayImg1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(img2, grayImg2, Imgproc.COLOR_BGR2GRAY);

        // Detect keypoints and descriptors using SIFT
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        detector.detect(grayImg1, keypoints1);
        detector.detect(grayImg2, keypoints2);
        extractor.compute(grayImg1, keypoints1, descriptors1);
        extractor.compute(grayImg2, keypoints2, descriptors2);

        // Match descriptors using FLANN matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        // Calculate matches distance
        List<DMatch> matchesList = matches.toList();
        double maxDist = 0;
        double minDist = Double.MAX_VALUE;
        for (DMatch match : matchesList) {
            double dist = match.distance;
            if (dist < minDist) minDist = dist;
            if (dist > maxDist) maxDist = dist;
        }

        // Filter good matches based on distance threshold
        LinkedList<DMatch> goodMatches = new LinkedList<>();
        for (DMatch match : matchesList) {
            if (match.distance <= Math.max(2 * minDist, 0.02)) {
                goodMatches.addLast(match);
            }
        }

        // Return true if there are enough good matches
        boolean matched = goodMatches.size() >= 10;
        System.out.println("SIFT matches between " + imageFile1.getName() + " and " + imageFile2.getName() + ": " + matched);
        return matched;
    }

    private static Set<String> getImagePixelSet(File imageFile) throws IOException {
        Set<String> pixelSet = new HashSet<>();
        BufferedImage image = ImageIO.read(imageFile);
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);
                    pixelSet.add(Integer.toString(pixel)); // Convert pixel value to string for set
                }
            }
        }
        return pixelSet;
    }

    private static void copyFile(File sourceFile, String destinationDirectory) {
        try {
            System.out.println("Copying file: " + sourceFile.getName());
            File destinationDir = new File(destinationDirectory);
            if (!destinationDir.exists()) {
                System.out.println("Destination directory does not exist. Creating: " + destinationDirectory);
                if (!destinationDir.mkdirs()) {
                    System.err.println("Failed to create destination directory: " + destinationDirectory);
                    return;
                }
            }
            File destinationFile = new File(destinationDirectory, sourceFile.getName());
            System.out.println("Destination file: " + destinationFile.getAbsolutePath());
            try (FileInputStream inputStream = new FileInputStream(sourceFile);
                 FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                System.out.println("File copied successfully.");
            } catch (IOException e) {
                System.err.println("Failed to copy file: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createVideo() {
        System.out.println("Creating Video...");

        String outputFilename = "E://video1.mp4";
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

        Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width / 2, screenBounds.height / 2);

        long startTime = System.nanoTime();
        long frameInterval = TimeUnit.SECONDS.toMillis(1) / 24; // 24 frames per second

        File[] imageFiles = new File("E://Duplicates").listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (imageFiles != null) {
            try {
                for (File imageFile : imageFiles) {
                    BufferedImage screen = ImageIO.read(imageFile);
                    if (screen != null) {
                        BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                        writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
                        System.out.println("Encoded frame: " + imageFile.getName());
                    } else {
                        System.err.println("Error reading image file: " + imageFile.getName());
                    }
                    Thread.sleep(frameInterval);
                }
            } catch (Exception e) {
                System.err.println("Error creating video: " + e.getMessage());
            }
        } else {
            System.err.println("No image files found in directory: E://FilteredImages");
        }

        writer.close();
        System.out.println("Video created successfully.");
    }

    private static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }


 

    private static int getImageHashCode(File imageFile) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] data = outputStream.toByteArray();
            return Arrays.hashCode(data);
        }
    }
}
 