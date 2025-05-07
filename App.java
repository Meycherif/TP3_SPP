package tp3;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.awt.Color ;
import java.io.File;


public class App {

	static public void main(String[] args) throws Exception {
		// reading image in
		IImageFilteringEngine im = new SingleThreadedImageFilteringEngine();
		im.loadImage("./TEST_IMAGES/15226222451_5fd668d81a_c.jpg");
		im.applyFilter(new GrayLevelFilter());
		im.applyFilter(new GaussianContourExtractorFilter());
		im.writeOutPngImage("./TEST_IMAGES/tmp.png");
		
	  } // EndMain

}
