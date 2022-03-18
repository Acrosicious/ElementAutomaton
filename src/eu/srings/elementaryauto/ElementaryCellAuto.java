package eu.srings.elementaryauto;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.srings.gif.AnimatedGifEncoder;

/**
 * Ein 1 Dimensionaler Zelluraerer Automat
 * 
 * @author Sebastian Rings
 *
 */

public class ElementaryCellAuto 
{
	JSlider slide = new JSlider(0, 150, 0);
	JLabel label1 = new JLabel("Rule: ");
	JLabel label2 = new JLabel("last: ");
	JLabel label3 = new JLabel("Delay: ");
	JLabel label4 = new JLabel("Pattern: ");
	
	int delay = 0;
	JTextField ruleTF = new JTextField();
	JTextField first = new JTextField();
	PixelMap map;
	Thread T;
	

	public ElementaryCellAuto()
	{
		map = new PixelMap();

		JFrame f = new JFrame("Elementary Automaton");
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		map.setSize(new Dimension(800, 600));

		JPanel options = new JPanel();
		options.setSize(new Dimension(800, 50));
		map.setLocation(0, 50);


		options.setLayout(null);
		ruleTF.setBounds(45, 10, 100, 20);
		ruleTF.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				map.clearMap();
				new Thread(new Runnable() {

					@Override
					public void run() {
						String r = ruleTF.getText();
						ruleTF.select(0, r.length());
						char[] f = null;
						if(!first.getText().equals(""))
						{
							try
							{
								f = first.getText().toCharArray();
							}
							catch(NumberFormatException e)
							{
								f = null;
							}
						}
						
						label2.setText("Last: " + r);
						runRule(Integer.parseInt(r), map.getHeight() / map.getPixelSize(), 
								map.getWidth() / map.getPixelSize(), f);
					}
				}).start();
				
			}
		});
		options.add(ruleTF);
		
		first.setBounds(520, 10, 100, 20);
		options.add(first);
		
		label1.setBounds(10, 10, 80, 20);
		options.add(label1);
		
		label2.setBounds(150, 10, 80, 20);
		options.add(label2);

		slide.setBounds(250, 10, 200, 20);
		slide.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				delay = slide.getValue();

			}
		});
		options.add(slide);
		
		label3.setBounds(210, 10, 80, 20);
		options.add(label3);
		
		label4.setBounds(460, 10, 80, 20);
		options.add(label4);


		map.setDrag_enabled(false);
		map.setMarkPoints_enabled(false);
		map.setZoom_enabled(false);
		map.setPixelSize(1);
		map.setBorder_color(Color.WHITE);
		
		ruleTF.requestFocus();


		f.setSize(800, 650);
		f.setLayout(null);
		f.add(options);
		f.add(map);
		f.setResizable(false);
		
		f.setVisible(true);
	}

	private void runRule(int rule, int n, int width, char[] firstLine) 
	{
		Random R = new Random();
		boolean[] line = new boolean[width];
		
//		map.setPixelSize(1);
		map.setPosition(0, 0);

		for(int i = 0; i < width; i++)
		{
			if(firstLine != null)
			{
				if(firstLine[0] == '2')
				{
					line[line.length/2] = true;
				}
				else
				{
					line[i] = firstLine[i % firstLine.length] == '1';
				}
			}
			else
			{
				line[i] = R.nextBoolean();
			}
		}

		drawLine(line, 0);
		
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		e.start("giftestfolder/img2.gif");
		e.setFrameRate(200);
		e.setRepeat(0);
		
		for(int y = 1; y < n; y++)
		{
			line = getNextLine(line, rule);
			drawLine(line, y);

//			BufferedImage bi = PixelMap.createBufferedImage(map);
//			e.addFrame(bi);
			if(delay > 0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

		}

		BufferedImage bi = PixelMap.createBufferedImage(map);
		e.addFrame(bi);

		e.finish();

	}

	private void drawLine(boolean[] line, int y)
	{
		for(int i = 0; i < line.length; i++)
		{
			if(line[i])
				map.addPoint(i, y);
		}
	}

	private boolean[] getNextLine(boolean[] line, int rule)
	{
		if(rule > 255 || rule < 0)
			return null;

		String r = Integer.toBinaryString(rule);
		boolean[] ruleBool = new boolean[8];

		for(int i = 0; i < r.length(); i++)
		{
			ruleBool[7-i] = r.toCharArray()[r.length()-1-i] == '1';
		}

		boolean newLine[] = new boolean[line.length];

		for(int i = 0; i < line.length; i++)
		{

			int x = i-1 >= 0 ? i-1 : i-1+line.length;
			int y = i;
			int z = i+1 < line.length ? i+1 : i+1-line.length;
			
			String s = "";
			s += line[x] ? "1" : "0";
			s += line[y] ? "1" : "0";
			s += line[z] ? "1" : "0";

			switch(s)
			{
			case "111": newLine[i] = ruleBool[0];
			break;
			case "110": newLine[i] = ruleBool[1];
			break;
			case "101": newLine[i] = ruleBool[2];
			break;
			case "100": newLine[i] = ruleBool[3];
			break;
			case "011": newLine[i] = ruleBool[4];
			break;
			case "010": newLine[i] = ruleBool[5];
			break;
			case "001": newLine[i] = ruleBool[6];
			break;
			case "000": newLine[i] = ruleBool[7];
			break;
			}
		}

		return newLine;

	}

	public static void main(String[] args)
	{
		new ElementaryCellAuto();
	}
}
