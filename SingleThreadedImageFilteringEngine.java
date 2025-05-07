package tp3;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class SingleThreadedImageFilteringEngine implements IImageFilteringEngine {

    private BufferedImage currentImage;

    @Override
    public void loadImage(String inputImage) throws Exception {
        try {
            File inputFile = new File(inputImage);
            currentImage = ImageIO.read(inputFile);
            if (currentImage == null) {
                throw new Exception("Failed to load image: " + inputImage);
            }
        } catch (Exception e) {
            throw new Exception("Error loading image: " + e.getMessage(), e);
        }
    }

    @Override
    public void writeOutPngImage(String outFile) throws Exception {
        if (currentImage == null) {
            throw new Exception("No image to save");
        }
        try {
            File outputFile = new File(outFile);
            ImageIO.write(currentImage, "png", outputFile);
        } catch (Exception e) {
            throw new Exception("Error saving image: " + e.getMessage(), e);
        }
    }

    @Override
    public void setImg(BufferedImage newImg) {
        currentImage = newImg;
    }

    @Override
    public BufferedImage getImg() {
        return currentImage;
    }

    @Override
    public void applyFilter(IFilter someFilter) {
        if (currentImage == null) {
            throw new IllegalStateException("No image loaded to apply filter");
        }

        // Get filter margin
        int margin = someFilter.getMargin();

        int doubleMargin = 2 * margin;


        // Create output image with same dimensions and type
        int width = currentImage.getWidth();
        int height = currentImage.getHeight();
        BufferedImage outImage = new BufferedImage(width - margin, height - margin, currentImage.getType());


        // Apply filter to pixels within bounds (excluding margin)
        for (int y = margin; y < height - margin; y++) {
            for (int x = margin; x < width - margin; x++) {
                someFilter.applyFilterAtPoint(x, y, currentImage, outImage);
            }
        }


        // Update current image to the filtered output
        currentImage = outImage;
    }
}