# Query-Dependent-Multi-View-Video-Summarization
A query-based multi-view video summarization framework that extracts frames, uses CNNs for feature extraction, reduces redundancy with Symmetric Uncertainty and Prim’s Algorithm, and applies SIFT for query relevance to generate concise, user-focused video summaries.
**Features**
-User-Relevant Summaries : Matches video frames with the user’s query to generate meaningful summaries tailored to user interest.
-Feature Extraction with CNN: Uses Convolutional Neural Networks to extract semantic features from video frames.
-Less Repetition: Employs Symmetric Uncertainty (SU) to reduce redundancy by identifying and removing repetitive or similar content.
-Better Frame Selection: Applies Prim’s Algorithm on a graph-based model to select the most relevant and non-redundant frames for the final summary.

**Requirements**
- Java 8 or above
- NetBeans, IntelliJ IDEA, or Eclipse IDE
- OpenCV (for image and video processing)
- Deep learning framework (e.g., TensorFlow for Java or custom-trained CNN model in .pb or .onnx if applicable)

**How to Run**
1. Clone this repository or download the ZIP.
2. Open the project in your preferred Java IDE.
3. Build the project (make sure dependencies like OpenCV are configured).
4. Run the `Main.java` or designated entry point.
5. Provide a query image and input videos from multiple views.

**Input / Output**
- Input: Video files from multiple cameras + a user-provided query image.
- Output: A summarized video or sequence of key frames that best match the query.

**Folder Structure**
├── CameraDetectionSystem/ # Module for video frame processing and feature detection
│ ├── BEANS.java
│ ├── Camera.java
│ ├── CameraDetectionSystem.java
│ ├── ImageFilter.java
│ ├── PSNRCalculator.java
│ └── SIFTDetector.java
│
└── ConvNeuralNetwork/ # Module for core CNN operations (custom-built)
├── Convolution.java
├── Flattening.java
├── MatrixMultiplicationExample.java
├── MaxPooling.java
├── ReluActivation.java
├── Kernels.java
└── JavaBeans.java
