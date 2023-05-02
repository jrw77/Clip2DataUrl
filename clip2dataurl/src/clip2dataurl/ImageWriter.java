package clip2dataurl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageWriter {
	public static final boolean DEBUG3 = false;
	
	static void writeImageToStream(Image img, OutputStream wos) throws IOException {
		if (DEBUG3) System.err.println("Java8");
		ImageIO.write((BufferedImage)img, "png", wos);
	}
}
