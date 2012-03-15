package controllers;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Header;


/**
 * Class QRController is resposible for creating, storing and rendering QRCode.
 * @author Kronics_2
 *
 */
public class QRController extends Controller {
	
	/** 
	 * This function generates the QRcode of an URL based on the poll with a determined polltoken.
	 * @param polltoken - The poll token to use for linking the QRCode image to the poll.
	 * @param size - The size of the image.
	 * @return - Returns the image or null if error.
	 */
	
	private static BufferedImage generateImage (String polltoken, int size) {
		String httpheader = Http.Request.current().headers.get("host").value();
		String URL = ("http://" + httpheader + "/" + polltoken);
		
		BitMatrix bm;
        Writer writer = new QRCodeWriter();
		try {
			bm = writer.encode(URL, BarcodeFormat.QR_CODE, size, size);
		
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int grayValue = (bm.get(x, y) ? 1 : 0) & 0xff;
                image.setRGB(x, y, (grayValue == 0 ?  0xFFFFFF : 0));
            }
        }

		return image;
		
		} catch (Exception e) {
            e.printStackTrace(System.err);
		}
		return null;
	}
	
	/**
	 * This function is called from the Routes file, it creates a file in the tmp folder with the image of
	 * the QRCode if it doesn't exist, if it does, uses it. 
	 */
	
	public static void returnImage () {
		try {
			String polltoken = params.get("token");
			int size = params.get("size", Integer.class).intValue();

			String path = Play.applicationPath.getPath();
			boolean exists = (new File(path + "/tmp/qrcodes/" + polltoken + "_" + size + ".jpg")).exists();

			if (!exists) {
				FileOutputStream fos = new FileOutputStream(path +  "/tmp/qrcodes/" + polltoken + "_" + size + ".jpg");
				BufferedImage image = generateImage(polltoken, size);
				ImageIO.write(image, "jpg", fos);
				fos.close();
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		} 
	}
}
