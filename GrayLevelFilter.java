package tp3;

import java.awt.image.BufferedImage;

public class GrayLevelFilter implements IFilter {

    @Override
    public int getMargin() {
        return 0; // No margin needed, as the filter processes each pixel independently
    }

    @Override
    public void applyFilterAtPoint(int x, int y, BufferedImage imgIn, BufferedImage imgOut) {
        // Get the RGB value of the input pixel at (x, y)
        int rgb = imgIn.getRGB(x, y);

        // Extract red, green, and blue components
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        // Compute the average of red, green, and blue
        int average = (red + green + blue) / 3;

        // Create new RGB value with red, green, and blue set to the average
        // Preserve the alpha channel (if present) by keeping the top 8 bits
        int newRgb = (rgb & 0xFF000000) | (average << 16) | (average << 8) | average;

        // Set the output pixel at (x, y) to the new RGB value
        imgOut.setRGB(x, y, newRgb);
    }
}