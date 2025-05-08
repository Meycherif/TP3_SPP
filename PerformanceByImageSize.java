package tp3;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

public class PerformanceByImageSize {

    public static void main(String[] args) throws Exception {
       
        String[] imageFiles = {
            "TEST_IMAGES/15226222451_5fd668d81a_c.jpg",
            "TEST_IMAGES/FourCircles.png"
            
        };

        int threads = 4; // nombre de threads fixe

        // Création du fichier CSV pour enregistrer les résultats
        PrintWriter writer = new PrintWriter(new FileWriter("performance_by_size.csv"));
        writer.println("Image,Width,Height,Pixels,Gray (ms),Contour (ms)");

        for (String imagePath : imageFiles) {
            BufferedImage img = ImageIO.read(new File(imagePath));
            int width = img.getWidth();
            int height = img.getHeight();
            int pixels = width * height;

            MultiThreadedImageFilteringEngine engine = new MultiThreadedImageFilteringEngine(threads);
            engine.setImg(img);

            long startGray = System.currentTimeMillis();
            engine.applyFilter(new GrayLevelFilter());
            long endGray = System.currentTimeMillis();
            long timeGray = endGray - startGray;

            long startContour = System.currentTimeMillis();
            engine.applyFilter(new GaussianContourExtractorFilter());
            long endContour = System.currentTimeMillis();
            long timeContour = endContour - startContour;

            System.out.println(imagePath + ": " + timeGray + " ms (gray), " + timeContour + " ms (contour)");

            writer.println(imagePath + "," + width + "," + height + "," + pixels + "," + timeGray + "," + timeContour);
        }

        writer.close();
        System.out.println("Fichier CSV généré : performance_by_size.csv");
    }
}
