package clip2dataurl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageWriter {
	public static final boolean DEBUG3 = false;

	static void writeImageToStream(Image img, OutputStream wos) throws IOException {
		if (DEBUG3) System.err.println("Java9+");
		if (img instanceof BufferedImage) {
			if (DEBUG3) System.err.println("BufferedImage");
			ImageIO.write((BufferedImage)img, "png", wos);
		} else if (img instanceof java.awt.image.MultiResolutionImage) {  // needs Java 9+
			if (DEBUG3) System.err.println("MultiResolutionImage");
			java.awt.image.MultiResolutionImage mrimage = (java.awt.image.MultiResolutionImage)img;
			
			List<Image> variants = mrimage.getResolutionVariants();
			Image img2 = ((java.awt.image.MultiResolutionImage)img).getResolutionVariant(5, 5);
			// if (img2 instanceof BufferedImage) { // just assume!
				ImageIO.write((BufferedImage)(variants.get(0)), "png", wos);
			//}
		}else {
			if (DEBUG3) System.err.println(" img is class "+img.getClass().getName());
		}
	}
}
