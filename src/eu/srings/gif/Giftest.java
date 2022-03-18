package eu.srings.gif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Giftest {


	public static void main(String[] args) throws IOException
	{

		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start("giftestfolder/img1.gif");
		e.setFrameRate(5);
		e.setRepeat(0);
		
		BufferedImage im;
		
		im = ImageIO.read(new File("giftestfolder/im(1).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(2).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(3).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(4).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(5).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(6).jpg"));
		e.addFrame(im);
		im = ImageIO.read(new File("giftestfolder/im(7).jpg"));
		e.addFrame(im);
		
		


		e.finish();

	}

}
