package tp3;

import java.awt.image.BufferedImage;

public class GaussianContourExtractorFilter implements IFilter {

    private final int margin = 5;

    @Override
    public int getMargin() {
        return margin; // 11x11 neighborhood requires a margin of 5 pixels
    }

    @Override
    public void applyFilterAtPoint(int x, int y, BufferedImage imgIn, BufferedImage imgOut) {

        if (x < margin || x >= imgIn.getWidth() - margin || y < margin || y >= imgIn.getHeight() - margin) {
          // For border pixels, copy input pixel
          imgOut.setRGB(x, y, 0);
        }
        // Initialize horizontal and vertical gradients
        double deltaX = 0.0;
        double deltaY = 0.0;

        // Iterate over 11x11 neighborhood (dx, dy from -5 to +5)
        for (int dx = -margin; dx <= margin; dx++) {
            for (int dy = -margin; dy <= margin; dy++) {
                // Check if neighbor pixel is within image bounds
                int nx = x + dx;
                int ny = y + dy;
                    // Get blue component of neighbor pixel
                    int rgb = imgIn.getRGB(nx, ny);
                    int blue   = (rgb      ) & 0x000000FF;

                    // Compute Gaussian weight: exp(-(dx^2 + dy^2)/4)
                    double gaussianWeight = Math.exp(-(dx * dx + dy * dy) / 4.0);

                    // Compute sign(dx) and sign(dy)
                    int signDx = Integer.compare(dx, 0); // -1, 0, or +1
                    int signDy = Integer.compare(dy, 0); // -1, 0, or +1

                    // Accumulate contributions to gradients
                    deltaX += signDx * blue * gaussianWeight;
                    deltaY += signDy * blue * gaussianWeight;
                
            }
        }

        // Compute gradient magnitude: sqrt(deltaX^2 + deltaY^2)
        double gradientMagnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Compute gray value: max(0, 255 - (1/2) * gradientMagnitude)
        int grayValue = (int) Math.max(0, 255 - 0.5 * gradientMagnitude);
        // Ensure grayValue is in range 0-255
        //grayValue = Math.min(255, Math.max(0, grayValue));

        // Create new RGB value with red, green, blue set to grayValue, preserving alpha
        int rgbIn = imgIn.getRGB(x, y);
        int newRgb = (rgbIn & 0xFF000000) | (grayValue << 16) | (grayValue << 8) | grayValue;

        // Set output pixel
        imgOut.setRGB(x, y, newRgb);
    }
}

// This code implements a Gaussian contour extractor filter for image processing.
// It applies a Gaussian-weighted gradient filter to each pixel in an image,
// calculating the gradient magnitude based on the blue component of neighboring pixels.
// The filter uses an 11x11 neighborhood and computes the gradient in both x and y directions.
// The resulting gradient magnitude is used to determine the gray value of the output pixel.