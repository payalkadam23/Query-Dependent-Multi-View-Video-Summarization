package cameradetectionsyatem;


import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import static com.xuggle.mediatool.demos.CaptureScreenToFile.convertToType;
import com.xuggle.xuggler.ICodec;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

public class SIFTDetector {
     static ArrayList<String> MAtchImagePath = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Read files and extract features
        ReadFile();

        // Output the number of matched images
        System.out.println("Match Image Snap are: " + MAtchImagePath.size());
copyMatchedImages();
        // Create video from matched images
        createVideo();
    }

      public static void ReadFile() throws IOException {
         // Specify the folder path
        String folderPath = "E:\\Snapshots";

        // Create a File object with the folder path
        File folder = new File(folderPath);

        // Check if the specified path exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
          
            File[] files = folder.listFiles();
            
            
            if (files != null) {
                for (File file : files) {
                     
                 int a;
                    a = MatchVideoSnap(file.getAbsolutePath());
               
                  if(a < 20)
        {
            System.out.println("list add");
        MAtchImagePath.add(file.getAbsolutePath());
        
        }
                }
            } else {
                System.out.println("The folder is empty.");
            }
        } else {
            System.out.println("The specified folder does not exist or is not a directory.");
        }
       }
static void copyMatchedImages() {
        String destinationDirectory = "E:\\MatchedImages";

        // Create the destination directory if it doesn't exist
        File destDir = new File(destinationDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        for (String filePath : MAtchImagePath) {
            File sourceFile = new File(filePath);
            File destFile = new File(destDir, sourceFile.getName());

            try {
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied: " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to copy: " + sourceFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
    static void createVideo() throws IOException {
        System.out.println("Creating Video...");
        String outputFilename = "E://video.mp4";
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

        Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
        ArrayList<String> text = MAtchImagePath;

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                screenBounds.width / 2, screenBounds.height / 2);

        long startTime = System.nanoTime();
        long frameInterval = TimeUnit.SECONDS.toMillis(1) / 60;

        for (String filePath : text) {
            BufferedImage screen = ImageIO.read(new File(filePath));

            if (screen != null) {
                BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            } else {
                System.err.println("Error reading image file: " + filePath);
            }

            try {
                Thread.sleep(frameInterval);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        writer.close();
    }

   
    
    public static int MatchVideoSnap(String args) throws IOException {

        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("\nOpenCV library loaded");

        // Paths to object and scene images
        String bookObject = "E:\\CroppedImages\\1.png";
        String bookScene = args;

        // Load images
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);

        // Detect keypoints and compute descriptors for object and scene images using SURF
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        
        // Detect keypoints
        featureDetector.detect(objectImage, objectKeyPoints);
        featureDetector.detect(sceneImage, sceneKeyPoints);
        
        // Convert MatOfKeyPoint to KeyPoint[]
        KeyPoint[] objectKeyPointsArray = objectKeyPoints.toArray();
        KeyPoint[] sceneKeyPointsArray = sceneKeyPoints.toArray();
        
        // Compute descriptors
        Mat objectDescriptors = new Mat();
        Mat sceneDescriptors = new Mat();
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);
        //descriptorExtractor.compute(objectImage, objectKeyPointsArray, objectDescriptors);
       // descriptorExtractor.compute(sceneImage, sceneKeyPointsArray, sceneDescriptors);
 descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);
        // Match descriptors using FLANN (Fast Library for Approximate Nearest Neighbors)
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(objectDescriptors, sceneDescriptors, matches);

        // Filter matches based on distance threshold
        double maxDist = 0.7;
        double minDist = Double.MAX_VALUE;
        List<DMatch> matchesList = matches.toList();
        for (DMatch match : matchesList) {
            double dist = match.distance;
             if (dist < minDist) {
                minDist = dist;
            }
        }

       
        List<DMatch> goodMatches = new ArrayList<>();
        for (DMatch match : matchesList) {
            if (match.distance < 2 * minDist) {
                goodMatches.add(match);
            }
        }
        System.out.println("Matching scene image: " + new File(bookScene).getName());
        System.out.println("CameraDetectionSystem.SIFTDetector.main()"+goodMatches.size());
            System.out.println("Match Image Snap are: "+MAtchImagePath.size());
       return goodMatches.size();
     
      
    }
}

/*
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import static com.xuggle.mediatool.demos.CaptureScreenToFile.convertToType;
import com.xuggle.xuggler.ICodec;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

public class SIFTDetector {
    static ArrayList<String> MAtchImagePath = new ArrayList<>();
    static Set<Integer> selectedFeatureIds = new HashSet<>();
    static Set<Integer> matchedFeatureIds = new HashSet<>();

    public static void main(String[] args) throws IOException {
        // Read features from files
        readFeaturesFromFile("D:\\Camera\\selected_features.txt", selectedFeatureIds);
        readFeaturesFromFile("D:\\Camera\\matched_features.txt", matchedFeatureIds);
        
        // Read files and extract features
        ReadFile();

        // Output the number of matched images
        System.out.println("Match Image Snap are: " + MAtchImagePath.size());
        copyMatchedImages();
        // Create video from matched images
        createVideo();
    }

    public static void ReadFile() throws IOException {
        // Specify the folder path
        String folderPath = "E:\\Snapshots";

        // Create a File object with the folder path
        File folder = new File(folderPath);

        // Check if the specified path exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // Match features in the snapshot image
                    int matchCount = MatchVideoSnap(file.getAbsolutePath());

                    // Only add the image if it has matching features
                    if (matchCount < 20 && isFeatureInSet(file.getName())) {
                        MAtchImagePath.add(file.getAbsolutePath());
                    }
                }
            } else {
                System.out.println("The folder is empty.");
            }
        } else {
            System.out.println("The specified folder does not exist or is not a directory.");
        }
    }

    private static void readFeaturesFromFile(String filePath, Set<Integer> featureIds) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] pairs = line.split(",");
                for (String pair : pairs) {
                    String[] parts = pair.split(":");
                    if (parts.length == 2) {
                        try {
                            Integer key = Integer.parseInt(parts[0].trim());
                            featureIds.add(key);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid key format in line: " + pair);
                        }
                    } else {
                        System.err.println("Invalid pair format: " + pair);
                    }
                }
            }
        }
    }

    private static boolean isFeatureInSet(String imageName) {
        // Example: Check if the image name contains a feature ID from the selected set
        // You should adapt this based on how features are associated with image names.
        for (Integer featureId : selectedFeatureIds) {
            if (imageName.contains(featureId.toString())) {
                return true;
            }
        }
        return false;
    }

    static void copyMatchedImages() {
        String destinationDirectory = "E:\\MatchedImages";

        // Create the destination directory if it doesn't exist
        File destDir = new File(destinationDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        for (String filePath : MAtchImagePath) {
            File sourceFile = new File(filePath);
            File destFile = new File(destDir, sourceFile.getName());

            try {
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied: " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to copy: " + sourceFile.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    private static void createVideo() {
        System.out.println("Creating Video...");

        String outputFilename = "E://video.mp4";
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

        Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                screenBounds.width / 2, screenBounds.height / 2);

        long startTime = System.nanoTime();
        long frameInterval = TimeUnit.SECONDS.toMillis(1) / 24;

        File[] imageFiles = new File("E://MatchedImages").listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

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
                // Close the writer to prevent incomplete video file
                writer.close();
                return;
            }
        } else {
            System.err.println("No image files found in directory: E://MatchedImages");
            // Close the writer to prevent incomplete video file
            writer.close();
            return;
        }

        writer.close();
        System.out.println("Video created successfully.");
    }

    public static int MatchVideoSnap(String args) throws IOException {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("\nOpenCV library loaded");

        // Paths to object and scene images
        String bookObject = "E:\\CroppedImages\\1.png";
        String bookScene = args;

        // Load images
        Mat objectImage = Highgui.imread(bookObject, Highgui.CV_LOAD_IMAGE_COLOR);
        Mat sceneImage = Highgui.imread(bookScene, Highgui.CV_LOAD_IMAGE_COLOR);

        // Detect keypoints and compute descriptors for object and scene images using SIFT
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        
        // Detect keypoints
        featureDetector.detect(objectImage, objectKeyPoints);
        featureDetector.detect(sceneImage, sceneKeyPoints);
        
        // Compute descriptors
        Mat objectDescriptors = new Mat();
        Mat sceneDescriptors = new Mat();
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);
        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);
        
        // Match descriptors using FLANN (Fast Library for Approximate Nearest Neighbors)
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(objectDescriptors, sceneDescriptors, matches);

        // Filter matches based on distance threshold
        double maxDist = 0.7;
        double minDist = Double.MAX_VALUE;
        List<DMatch> matchesList = matches.toList();
        for (DMatch match : matchesList) {
            double dist = match.distance;
            if (dist < minDist) {
                minDist = dist;
            }
        }

        List<DMatch> goodMatches = new ArrayList<>();
        for (DMatch match : matchesList) {
            if (match.distance < 2 * minDist) {
                goodMatches.add(match);
            }
        }
        System.out.println("Matching scene image: " + new File(bookScene).getName());
        System.out.println("Good matches: " + goodMatches.size());
        return goodMatches.size();
    }
}
*/