package core.healing;

import ai.onnxruntime.*;
import core.platform.utils.Logger;

import java.io.File;
import java.util.*;

public class VisualDetector {
    private OrtEnvironment env;
    private OrtSession session;
    private final HealingConfig config = HealingConfig.getInstance();

    public VisualDetector() {
        if (config.visualAi.enabled) {
            initModel();
        }
    }

    private void initModel() {
        try {
            File modelFile = new File(config.visualAi.modelPath);
            if (!modelFile.exists()) {
                Logger.warn("Visual AI Model not found at: %s. Visual healing will be disabled.",
                        config.visualAi.modelPath);
                return;
            }
            this.env = OrtEnvironment.getEnvironment();
            this.session = env.createSession(config.visualAi.modelPath, new OrtSession.SessionOptions());
            Logger.info("Visual AI Model loaded successfully from %s", config.visualAi.modelPath);
        } catch (Exception e) {
            Logger.error("Failed to initialize ONNX model: %s", e.getMessage());
        }
    }

    public List<Detection> detect(byte[] screenshot) {
        if (session == null)
            return Collections.emptyList();

        try {
            // Placeholder: Preprocessing screenshot to FloatBuffer 640x640 (standard YOLO)
            // In a real implementation, we would use ImageIO and basic pixel normalization
            // For now, returning mock detections if the model is loaded but we're in
            // "demo/placeholder" mode
            Logger.debug("Running Visual AI detection on screenshot...");

            // This is where the actual ONNX session.run() happens
            // Map<String, OnnxTensor> inputs = Map.of("images",
            // OnnxTensor.createTensor(env, buffer, shape));
            // try (OrtSession.Result results = session.run(inputs)) { ... }

            return mockDetections(); // Simulate detected boxes for development
        } catch (Exception e) {
            Logger.error("Inference failed: %s", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Detection> mockDetections() {
        List<Detection> list = new ArrayList<>();
        // Mocking a few common elements
        list.add(new Detection("button", 0.95, 100, 200, 50, 30));
        list.add(new Detection("input", 0.88, 100, 250, 200, 40));
        return list;
    }

    public static class Detection {
        public final String label;
        public final double confidence;
        public final int x, y, width, height;

        public Detection(String label, double confidence, int x, int y, int width, int height) {
            this.label = label;
            this.confidence = confidence;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int centerX() {
            return x + width / 2;
        }

        public int centerY() {
            return y + height / 2;
        }
    }
}
