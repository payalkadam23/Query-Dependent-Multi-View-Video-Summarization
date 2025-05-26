package cameradetectionsyatem;

import Feature.FeatureMatcher;
import Feature.FeatureMinimization;
import java.util.HashMap;
import java.util.Map;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.scene.image.WritableImage;

public class CameraDetectionSystem extends Application {
 private List<String> imagePaths = new ArrayList<>();
    private MediaPlayer[] mediaPlayers = new MediaPlayer[4];
    private MediaView[] mediaViews = new MediaView[4];
    private VBox root;
    private String snapshotDirectory = "E:\\Snapshots";
    private String croppedImagesDirectory = "E:\\CroppedImages";
   
    private String videoFramesDirectory = "E:\\VideoFrames";
    private int[] snapshotCount = new int[4];
    private ImageView selectedImageView;
    private Rectangle selectionRect;
    private Timeline snapshotTimeline;

   @Override
public void start(Stage primaryStage) {
    root = new VBox(30); // Spacing between buttons and media views

    HBox buttonBox = new HBox(20); // Spacing between buttons
    for (int i = 0; i < 4; i++) {
        Button browseButton = new Button("Browse " + (i + 1));
        browseButton.setPrefWidth(100); // Set preferred width
        browseButton.setPrefHeight(50); // Set preferred height
        int finalI = i;
        browseButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Video File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                playVideo(selectedFile.toURI().toString(), finalI);
            }
        });
        buttonBox.getChildren().add(browseButton);
    }

    Button browseImageButton = new Button("Browse Image");
    browseImageButton.setPrefWidth(100); // Set preferred width
    browseImageButton.setPrefHeight(50); // Set preferred height
    browseImageButton.setOnAction(event -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            displaySelectedImage(selectedFile.toURI().toString());
        }
    });
    buttonBox.getChildren().add(browseImageButton);

    Button createSnapshotButton = new Button("Create Snapshot");
    createSnapshotButton.setPrefWidth(150); // Set preferred width
    createSnapshotButton.setPrefHeight(50); // Set preferred height
    createSnapshotButton.setOnAction(event -> {
        scheduleSnapshot();
    });
    buttonBox.getChildren().add(createSnapshotButton);

    Button playButton = new Button("Play");
    playButton.setPrefWidth(100); // Set preferred width
    playButton.setPrefHeight(50); // Set preferred height
    playButton.setOnAction(event -> {
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        }
    });

    Button pauseButton = new Button("Pause");
    pauseButton.setPrefWidth(100); // Set preferred width
    pauseButton.setPrefHeight(50); // Set preferred height
    pauseButton.setOnAction(event -> {
        for (int i = 0; i < mediaViews.length; i++) {
            if (mediaPlayers[i] != null && mediaPlayers[i].getStatus() == MediaPlayer.Status.PLAYING) {
                System.out.println("Pausing video " + i);
                mediaPlayers[i].pause();
                takeSnapshot(videoFramesDirectory);
            } else {
                System.out.println("Video " + i + " is not playing. Status: " + (mediaPlayers[i] != null ? mediaPlayers[i].getStatus() : "null"));
            }
        }
    });

  Button CNNButton = new Button("CNN");
    CNNButton.setPrefWidth(100); // Set preferred width
    CNNButton.setPrefHeight(50); // Set preferred height
    CNNButton.setOnAction(event -> {
         try {
                    camera.main(new String[]{"E:\\Snapshots", "E:\\CroppedImages\\1.png"});
                    // Optionally show a message dialog or update UI on completion
                    System.out.println("Processing complete.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Handle exception
                }
           try {
        // Call the method from FeatureMinimization
        FeatureMinimization.runFeatureMinimization();
        
        // Optionally show a message dialog or update UI on completion
        System.out.println("Processing complete.");
    } catch (IOException ex) {
        ex.printStackTrace();
        // Handle exception
    }
            try {
        // Call the method from FeatureMatcher
        FeatureMatcher.runFeatureMatching();
        
        // Optionally show a message dialog or update UI on completion
        System.out.println("Feature matching complete.");
    } catch (IOException ex) {
        ex.printStackTrace();
        // Handle exception
    }
    });
        Button makeVideoButton = new Button("Make Video");

        makeVideoButton.setPrefWidth(100); // Set preferred width
        makeVideoButton.setPrefHeight(50); // Set preferred height
        makeVideoButton.setOnAction((ActionEvent event) -> {
         
          
            // Output the number of matched images
           System.out.println("Match Image Snap are: " + SIFTDetector.MAtchImagePath.size());

            
            
      
       
        try {
            // Call the main method of SIFTDetector
            SIFTDetector.main(new String[0]);
        } catch (IOException ex) {
            Logger.getLogger(CameraDetectionSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
       
          
        
            System.out.println("Video Created");
        });


Button createVideoButton = new Button("Filter Images");
createVideoButton.setPrefWidth(150); // Set preferred width
createVideoButton.setPrefHeight(50); // Set preferred height
createVideoButton.setOnAction(event -> {
    
        try {
           /* filterImages();
         createVideos();*/
           PSNRCalculator.main(new String[0]);
           
              ImageFilter.main(new String[0]);
        } catch (IOException ex) {
            Logger.getLogger(CameraDetectionSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
});


    


    buttonBox.getChildren().addAll(playButton, pauseButton,CNNButton, makeVideoButton,createVideoButton);

    root.getChildren().addAll(buttonBox);

    // Initialize the ImageView for displaying the selected image
    selectedImageView = new ImageView();
    selectedImageView.setFitWidth(600);
    selectedImageView.setFitHeight(400);
    StackPane imagePane = new StackPane(selectedImageView);
  VBox imageBox = new VBox();
    imageBox.getChildren().add(imagePane);
    root.getChildren().add(imageBox);


    root.getChildren().add(imagePane);

    // Wrap the root pane inside a scroll pane
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(root);

    // Set preferred size of the scroll pane
    scrollPane.setPrefSize(1300, 600);

    // Set the scroll policies to always display both horizontal and vertical scroll bars
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

    // Create the scene with the scroll pane
    Scene scene = new Scene(scrollPane);

    primaryStage.setTitle("Video Player");
    primaryStage.setScene(scene);
    primaryStage.show();
}


  private void playVideo(String source, int index) {
        if (mediaPlayers[index] != null) {
            mediaPlayers[index].stop();
        }

        Media media = new Media(source);
        mediaPlayers[index] = new MediaPlayer(media);

        mediaPlayers[index].setOnEndOfMedia(() -> mediaPlayers[index].stop());

        mediaViews[index] = new MediaView(mediaPlayers[index]);
        mediaViews[index].setFitWidth(800);
        mediaViews[index].setFitHeight(600);

        root.getChildren().add(mediaViews[index]);
    }




    private void scheduleSnapshot() {
        if (snapshotTimeline != null) {
            snapshotTimeline.stop();
        }
        snapshotTimeline = new Timeline(new KeyFrame(Duration.seconds(0.25), event -> {
            takeSnapshot(snapshotDirectory);
        }));
        snapshotTimeline.setCycleCount(Timeline.INDEFINITE);
        snapshotTimeline.play();
    }

    private void takeSnapshot(String directoryPath) {
        for (int i = 0; i < mediaViews.length; i++) {
            if (mediaPlayers[i] != null && (mediaPlayers[i].getStatus() == MediaPlayer.Status.PLAYING || mediaPlayers[i].getStatus() == MediaPlayer.Status.PAUSED)) {
                MediaView mediaView = mediaViews[i];
                if (mediaView == null) {
                    return;
                }

                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        System.out.println("Snapshot directory created: " + directoryPath);
                    } else {
                        System.err.println("Failed to create snapshot directory: " + directoryPath);
                        return;
                    }
                }

                Image snapshot = mediaView.snapshot(null, null);
                String fileName = "snapshot_" + i  + "_"  + snapshotCount[i] + ".png";
                File file = new File(directoryPath, fileName);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                    System.out.println("Snapshot saved: " + file.getAbsolutePath());
                    snapshotCount[i]++;
                } catch (IOException e) {
                }
            }
        }
    }
private void displaySelectedImage(String imageUrl) {
    Image image = new Image(imageUrl);
    selectedImageView.setImage(image);

    StackPane imagePane = new StackPane(selectedImageView);

    // Create the selection rectangle
    selectionRect = new Rectangle();
    selectionRect.setFill(null);
    selectionRect.setStroke(Color.RED);
    selectionRect.setStrokeWidth(2);

    selectedImageView.setOnMousePressed(event -> {
        // Set the initial position of the selection rectangle
        selectionRect.setX(event.getX());
        selectionRect.setY(event.getY());
        selectionRect.setWidth(0);
        selectionRect.setHeight(0);

        // Clear the previous selection rectangle
        imagePane.getChildren().remove(selectionRect);
        imagePane.getChildren().add(selectionRect);
    });

    selectedImageView.setOnMouseDragged(event -> {
        // Update the position and dimensions of the selection rectangle
        double currentX = event.getX();
        double currentY = event.getY();
        double startX = selectionRect.getX();
        double startY = selectionRect.getY();
        selectionRect.setWidth(Math.abs(currentX - startX));
        selectionRect.setHeight(Math.abs(currentY - startY));
        selectionRect.setX(Math.min(currentX, startX));
        selectionRect.setY(Math.min(currentY, startY));
    });

    selectedImageView.setOnMouseReleased(event -> {
        // Perform cropping and remove the selection rectangle
        double x = selectionRect.getX();
        double y = selectionRect.getY();
        double width = selectionRect.getWidth();
        double height = selectionRect.getHeight();

        // Ensure width and height are positive
      /*  if (width < 0) {
            x += width;
            width = -width;
        }
        if (height < 0) {
            y += height;
            height = -height;
        }*/

        // Crop the image based on the selection rectangle
        int croppedX = (int) Math.max(0, x);
        int croppedY = (int) Math.max(0, y);
        int croppedWidth = (int) Math.min(width, image.getWidth() - croppedX);
        int croppedHeight = (int) Math.min(height, image.getHeight() - croppedY);

        // Create a cropped image
        Image croppedImage = new WritableImage(image.getPixelReader(), croppedX, croppedY, croppedWidth, croppedHeight);

        // Save or display the cropped image as needed
        saveCroppedImage(croppedImage, 800,600);

        // Remove the selection rectangle
        imagePane.getChildren().remove(selectionRect);
    });

    // Add the image pane to the root
    root.getChildren().add(imagePane);
}





private void saveCroppedImage(Image image, int width, int height) {
    String croppedImageDirectory = "E:\\CroppedImages";
    File directory = new File(croppedImageDirectory);
    if (!directory.exists()) {
        directory.mkdirs();
    }

    String fileName =  1 + ".png";
    File file = new File(croppedImageDirectory, fileName);

    try {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        System.out.println("Cropped image saved: " + file.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Error saving cropped image: " + e.getMessage());
    }
}
private void filterImages() throws IOException {
    String folderPath = "E:\\Snapshots";
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

            // Iterate through the map and compare images using Jaccard similarity
            for (List<String> imagePathList : imageHashes.values()) {
                if (imagePathList.size() > 1) {
                    // Calculate Jaccard similarity for each pair of images
                    for (int i = 0; i < imagePathList.size(); i++) {
                        for (int j = i + 1; j < imagePathList.size(); j++) {
                            File imageFile1 = new File(imagePathList.get(i));
                            File imageFile2 = new File(imagePathList.get(j));
                            double jaccardSimilarity = calculateJaccardSimilarity(imageFile1, imageFile2);
                            if (jaccardSimilarity >= 0.9) {
                                // If similarity exceeds threshold, copy only a subset of images
                                copyFile(imageFile1, "E:\\Duplicates");
                                imagesCopied++;
                                break; // No need to compare further images in the set
                            }
                        }
                    }
                } else {
                    // If there's only one instance, copy it to the duplicates directory
                    File imageFile = new File(imagePathList.get(0));
                    copyFile(imageFile, "E:\\Duplicates");
                    imagesCopied++;
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

// Method to calculate Jaccard similarity between two images
private double calculateJaccardSimilarity(File imageFile1, File imageFile2) {
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
        return (double) intersection.size() / union.size();
    } catch (IOException e) {
        System.err.println("Error reading image files: " + e.getMessage());
        return 0.0; // Return 0 in case of errors
    }
}

// Method to convert image to set of pixels
private Set<String> getImagePixelSet(File imageFile) throws IOException {
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


private int getImageHashCode(File imageFile) throws IOException {
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



private void copyFile(File sourceFile, String destinationDirectory) {
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

/*
private double calculateDiceSimilarity(File imageFile1, File imageFile2) {
    try {
        // Read images and convert to sets of pixels
        Set<String> pixelSet1 = getImagePixelSet(imageFile1);
        Set<String> pixelSet2 = getImagePixelSet(imageFile2);

        // Calculate intersection size
        Set<String> intersection = new HashSet<>(pixelSet1);
        intersection.retainAll(pixelSet2);

        // Calculate Dice similarity
        return (2.0 * intersection.size()) / (pixelSet1.size() + pixelSet2.size());
    } catch (IOException e) {
        System.err.println("Error reading image files: " + e.getMessage());
        return 0.0; // Return 0 in case of errors
    }
}

private void filterImages() throws IOException {
    String folderPath = "E:\\Snapshots";
    int totalImagesCopied = 0; // Track the total number of images copied

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

            // Iterate through the map and compare images using Dice similarity
            for (List<String> imagePathList : imageHashes.values()) {
                if (imagePathList.size() > 1) {
                    // Calculate Dice similarity for each pair of images
                    for (int i = 0; i < imagePathList.size(); i++) {
                        for (int j = i + 1; j < imagePathList.size(); j++) {
                            File imageFile1 = new File(imagePathList.get(i));
                            File imageFile2 = new File(imagePathList.get(j));
                            double diceSimilarity = calculateDiceSimilarity(imageFile1, imageFile2);
                            if (diceSimilarity >= 0.9) {
                                // If similarity exceeds threshold, copy only a subset of images
                                int imagesCopied = copySimilarImages(imagePathList, 100);
                                totalImagesCopied += imagesCopied;
                                break; // No need to compare further images in the set
                            }
                        }
                    }
                } else {
                    // If there's only one instance, copy it to the duplicates directory
                    File imageFile = new File(imagePathList.get(0));
                    copyFile(imageFile, "E:\\Duplicates");
                    totalImagesCopied++;
                }
            }

            System.out.println("Total images copied: " + totalImagesCopied);
        } else {
            System.out.println("The folder is empty.");
        }
    } else {
        System.out.println("The specified folder does not exist or is not a directory.");
    }
}
*/


private static void createVideos() {
    System.out.println("Creating Video...");

    String outputFilename = "E://video1.mp4";
     System.out.println("Creating video...");
    final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

    Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
    writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
            screenBounds.width / 2, screenBounds.height / 2);

    long startTime = System.nanoTime();
    long frameInterval = TimeUnit.SECONDS.toMillis(1) /30;

    File[] imageFiles = new File("E://Duplicates").listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

    if (imageFiles != null) {
        try {
            for (File imageFile : imageFiles) {
                BufferedImage screen = ImageIO.read(imageFile);
                if (screen != null) {
                    BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
                    writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
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
        System.err.println("No image files found in directory: E://Duplicates");
        // Close the writer to prevent incomplete video file
        writer.close();
        return;
    }

    writer.close();
    System.out.println("Video created successfully.");
}


    // Method to convert image to a specified type
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

    public static void main(String[] args) {
        launch(args);
    }
}
