package clip2dataurl;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Converts clipboard content into a data url and paste back to clipboard. Only
 * implemented for image, html, text and (a single) file.
 * 
 * @author J.Weimar
 */
public class ClipToDataUrl {

	private static final boolean DEBUG1 = false;
	private static final boolean DEBUG2 = false;

	public static void main(String[] args) throws UnsupportedFlavorException, IOException, InterruptedException {
		if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
			System.out.println("java Clip2DataUrl.jar converts the content of the clipboard to a data url and posts this on the clipboard.");
		}
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = clipboard.getContents(null);
		String dataURL = null;
		if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			// is an image
			dataURL = getImageFromClipboard(transferable);
		} else if (transferable.isDataFlavorSupported(DataFlavor.fragmentHtmlFlavor)) {
			// is html
			dataURL = getHtmlFromClipboard(transferable);
		} else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			// is a file ore mor prexcisely, a list of files
			dataURL = getFileFromClipboard(transferable);
		} else if (transferable.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
			// is plain text
			dataURL = getTextFromClipboard(transferable);
		} else {
			System.err.println("Not convertable");
		}
		if (dataURL != null) {
			// set the clipboard contents
			StringSelection data = new StringSelection(dataURL);
			clipboard.setContents(data, null);
		} else {
			StringSelection data = new StringSelection("error: dataURL is null");
			clipboard.setContents(data, null);
		}
		Thread.sleep(1000);
	}

	/**
	 * extracts image from the transferable, converts it into png and base64 endodes
	 * it, prefixed by "data:image/png;base64,"
	 * 
	 * @param transferable comes from clipboard, is assumed to support
	 *                     DataFlavor.imageFlavor.
	 * @return a string
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private static String getImageFromClipboard(Transferable transferable)
			throws UnsupportedFlavorException, IOException {
		Image img = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
		if (img != null) {
			BufferedImage buffered = (BufferedImage) img;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String header = "data:image/png;base64,";
			bos.write(header.getBytes());
			OutputStream wos = Base64.getEncoder().wrap(bos);
			ImageIO.write(buffered, "png", wos);
			wos.close();
			if (DEBUG1) System.out.println(bos.toString());
			return bos.toString();
		}
		return "error: image null";
	}

	/**
	 * extracts html from the transferable, base64 endodes it, prefixed by
	 * "data:text/html;base64,"
	 * 
	 * @param transferable comes from clipboard, is assumed to support
	 *                     DataFlavor.fragmentHtmlFlavor.
	 * @return a string
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private static String getHtmlFromClipboard(Transferable transferable)
			throws UnsupportedFlavorException, IOException {
		String html = (String) transferable.getTransferData(DataFlavor.fragmentHtmlFlavor);
		if (html != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String header = "data:text/html;base64,";
			bos.write(header.getBytes());
			OutputStream wos = Base64.getEncoder().wrap(bos);
			wos.write(html.getBytes());
			wos.close();
			if (DEBUG1) System.out.println(bos.toString());
			return bos.toString();
		}
		return "error: html null";
	}

	/**
	 * extracts text from the transferable, base64 endodes it, prefixed by
	 * "data:text/text;base64,"
	 * 
	 * @param transferable comes from clipboard, is assumed to support a reader.
	 * @return a string
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private static String getTextFromClipboard(Transferable transferable)
			throws UnsupportedFlavorException, IOException {
		Reader text = DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(transferable);
		if (text != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			String header = "data:text/plain;base64,";
			bos.write(header.getBytes());
			OutputStream wos = Base64.getEncoder().wrap(bos);
			Writer cw = new OutputStreamWriter(wos);
			// java >=10: use sr.transferTo(cw);
			// java 8:
			int charsInBuf = 0;
			char[] buf = new char[4096];
			while ((charsInBuf = text.read(buf)) != -1) {
				cw.write(buf, 0, charsInBuf);
				if (DEBUG2) System.out.println("read " + charsInBuf + " chars");
			}
			cw.close();
			if (DEBUG1) System.out.println(bos.toString());
			return bos.toString();
		}
		return "error: text null";
	}

	/**
	 * extracts the first file from the transferable, reads the file, deteremines
	 * the mime type, base64 endodes it, prefixes it by the "data:"+mime
	 * type+";base64,".
	 * 
	 * @param transferable comes from clipboard, is assumed to support
	 *                     DataFlavor.javaFileListFlavor.
	 * @return a string
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private static String getFileFromClipboard(Transferable transferable)
			throws UnsupportedFlavorException, IOException {
		@SuppressWarnings("unchecked")
		List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

		if (fileList == null) {
			return "error: file List null";
		}
		if (fileList.isEmpty()) {
			return "error: file List is empty";
		}
		if (fileList.size() > 1) {
			return "error: too many (" + fileList.size() + ") files.";
		}
		File first = fileList.get(0);
		Path path = first.toPath();
		String mimeType = Files.probeContentType(path);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String header1 = "data:";
		bos.write(header1.getBytes());
		bos.write(mimeType.getBytes());
		String header2 = ";base64,";
		bos.write(header2.getBytes());

		byte[] bytes = Files.readAllBytes(path);

		OutputStream wos = Base64.getEncoder().wrap(bos);
		wos.write(bytes);
		wos.close();
		if (DEBUG1) System.out.println(bos.toString());
		return bos.toString();
	}
}
