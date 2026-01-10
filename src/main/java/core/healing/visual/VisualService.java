package core.healing.visual;

import core.healing.IHealingDriver;
import core.platform.utils.Logger;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Service for visual comparison using OpenCV and SSIM (Structural Similarity
 * Index).
 */
public class VisualService {
    private static VisualService instance;
    private static final String SCREENSHOT_DIR = "target/golden-screenshots";
    private static boolean opencvInitialized = false;

    static {
        try {
            OpenCV.loadShared();
            opencvInitialized = true;
            Logger.info("OpenCV loaded successfully for Visual Healing");
        } catch (Exception e) {
            Logger.error("Failed to load OpenCV: %s", e.getMessage());
            opencvInitialized = false;
        }
    }

    private VisualService() {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized VisualService getInstance() {
        if (instance == null) {
            instance = new VisualService();
        }
        return instance;
    }

    /**
     * Capture screenshot of an element.
     */
    public BufferedImage captureScreenshot(IHealingDriver driver, String locator) {
        if (!opencvInitialized) {
            Logger.warn("OpenCV not initialized, skipping screenshot capture");
            return null;
        }

        try {
            // Use driver's screenshot capability
            byte[] screenshotBytes = driver.screenshot(locator, false);
            if (screenshotBytes == null || screenshotBytes.length == 0) {
                Logger.debug("No screenshot data returned for locator: %s", locator);
                return null;
            }

            // Convert bytes to BufferedImage
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(screenshotBytes);
            return ImageIO.read(bis);
        } catch (Exception e) {
            Logger.debug("Failed to capture screenshot for %s: %s", locator, e.getMessage());
            return null;
        }
    }

    /**
     * Save screenshot to disk.
     */
    public boolean saveScreenshot(BufferedImage image, String elementId) {
        if (image == null || elementId == null) {
            return false;
        }

        try {
            String filePath = SCREENSHOT_DIR + File.separator + sanitizeFilename(elementId) + ".png";
            File outputFile = new File(filePath);
            ImageIO.write(image, "png", outputFile);
            Logger.debug("Screenshot saved: %s", filePath);
            return true;
        } catch (IOException e) {
            Logger.warn("Failed to save screenshot for %s: %s", elementId, e.getMessage());
            return false;
        }
    }

    /**
     * Load screenshot from disk.
     */
    public BufferedImage loadScreenshot(String elementId) {
        if (elementId == null) {
            return null;
        }

        try {
            String filePath = SCREENSHOT_DIR + File.separator + sanitizeFilename(elementId) + ".png";
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.debug("Screenshot not found: %s", filePath);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            Logger.warn("Failed to load screenshot for %s: %s", elementId, e.getMessage());
            return null;
        }
    }

    /**
     * Calculate SSIM (Structural Similarity Index) between two images.
     * Returns a value between 0.0 (completely different) and 1.0 (identical).
     */
    public double calculateSSIM(BufferedImage img1, BufferedImage img2) {
        if (!opencvInitialized || img1 == null || img2 == null) {
            return 0.0;
        }

        try {
            // Convert BufferedImage to OpenCV Mat
            Mat mat1 = bufferedImageToMat(img1);
            Mat mat2 = bufferedImageToMat(img2);

            // Resize to same dimensions
            if (mat1.width() != mat2.width() || mat1.height() != mat2.height()) {
                int targetWidth = Math.min(mat1.width(), mat2.width());
                int targetHeight = Math.min(mat1.height(), mat2.height());
                Size targetSize = new Size(targetWidth, targetHeight);

                Imgproc.resize(mat1, mat1, targetSize);
                Imgproc.resize(mat2, mat2, targetSize);
            }

            // Convert to grayscale for comparison
            Mat gray1 = new Mat();
            Mat gray2 = new Mat();
            if (mat1.channels() > 1) {
                Imgproc.cvtColor(mat1, gray1, Imgproc.COLOR_BGR2GRAY);
            } else {
                gray1 = mat1;
            }
            if (mat2.channels() > 1) {
                Imgproc.cvtColor(mat2, gray2, Imgproc.COLOR_BGR2GRAY);
            } else {
                gray2 = mat2;
            }

            // Calculate SSIM components
            Mat i1 = new Mat();
            Mat i2 = new Mat();
            gray1.convertTo(i1, CvType.CV_64F);
            gray2.convertTo(i2, CvType.CV_64F);

            Mat i1_2 = new Mat();
            Mat i2_2 = new Mat();
            Mat i1_i2 = new Mat();
            Core.multiply(i1, i1, i1_2);
            Core.multiply(i2, i2, i2_2);
            Core.multiply(i1, i2, i1_i2);

            // Gaussian filter for smoothing
            Mat mu1 = new Mat();
            Mat mu2 = new Mat();
            Size gaussianSize = new Size(11, 11);
            double sigma = 1.5;

            Imgproc.GaussianBlur(i1, mu1, gaussianSize, sigma);
            Imgproc.GaussianBlur(i2, mu2, gaussianSize, sigma);

            Mat mu1_2 = new Mat();
            Mat mu2_2 = new Mat();
            Mat mu1_mu2 = new Mat();
            Core.multiply(mu1, mu1, mu1_2);
            Core.multiply(mu2, mu2, mu2_2);
            Core.multiply(mu1, mu2, mu1_mu2);

            Mat sigma1_2 = new Mat();
            Mat sigma2_2 = new Mat();
            Mat sigma12 = new Mat();

            Imgproc.GaussianBlur(i1_2, sigma1_2, gaussianSize, sigma);
            Core.subtract(sigma1_2, mu1_2, sigma1_2);

            Imgproc.GaussianBlur(i2_2, sigma2_2, gaussianSize, sigma);
            Core.subtract(sigma2_2, mu2_2, sigma2_2);

            Imgproc.GaussianBlur(i1_i2, sigma12, gaussianSize, sigma);
            Core.subtract(sigma12, mu1_mu2, sigma12);

            // Constants for stability
            double C1 = 6.5025, C2 = 58.5225;

            // SSIM formula: (2*mu1_mu2 + C1)(2*sigma12 + C2) / ((mu1^2 + mu2^2 +
            // C1)(sigma1^2 + sigma2^2 + C2))
            Mat t1 = new Mat();
            Mat t2 = new Mat();
            Mat t3 = new Mat();

            // Numerator
            Core.multiply(mu1_mu2, new Scalar(2), t1);
            Core.add(t1, new Scalar(C1), t1);

            Core.multiply(sigma12, new Scalar(2), t2);
            Core.add(t2, new Scalar(C2), t2);

            Core.multiply(t1, t2, t3); // numerator

            // Denominator
            Mat t4 = new Mat();
            Mat t5 = new Mat();

            Core.add(mu1_2, mu2_2, t1);
            Core.add(t1, new Scalar(C1), t1);

            Core.add(sigma1_2, sigma2_2, t2);
            Core.add(t2, new Scalar(C2), t2);

            Core.multiply(t1, t2, t4); // denominator

            // SSIM map
            Mat ssimMap = new Mat();
            Core.divide(t3, t4, ssimMap);

            // Mean SSIM
            Scalar meanScalar = Core.mean(ssimMap);
            double ssim = meanScalar.val[0];

            // Cleanup
            mat1.release();
            mat2.release();
            gray1.release();
            gray2.release();

            return Math.max(0.0, Math.min(1.0, ssim)); // Clamp to [0, 1]

        } catch (Exception e) {
            Logger.error("SSIM calculation failed: %s", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Convert BufferedImage to OpenCV Mat.
     */
    private Mat bufferedImageToMat(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);

        byte[] pixels = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(convertedImage.getHeight(), convertedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }

    /**
     * Sanitize filename to remove invalid characters.
     */
    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Check if OpenCV is properly initialized.
     */
    public boolean isAvailable() {
        return opencvInitialized;
    }
}
