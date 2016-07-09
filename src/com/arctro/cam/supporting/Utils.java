package com.arctro.cam.supporting;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.Arrays;

import com.arctro.cam.processor.Block;

public class Utils {
	//The length of a pixel in a byte array
	public static final int PIXEL_LENGTH = 3;
	//The size of a row/column in a byte array
	public static final int BLOCK_SIZE = 32;
	//The size of a row/column of a block in a byte array
	public static final int BLOCK_BYTE_SIZE = PIXEL_LENGTH * BLOCK_SIZE;
	//The size of a block
	public static final int S_BLOCK_SIZE = (int) Math.pow(BLOCK_SIZE, 2);
	//The size of a block in a byte array
	public static final int S_BLOCK_BYTE_SIZE = S_BLOCK_SIZE * PIXEL_LENGTH;
	
	//Max packets and frames in a second
	public static int MAX_SEND_RATE = 25; //Unused
	
	//Turns the byte array into a BufferedImage
	public static BufferedImage createImageFromBytes(byte[] imageData, int width, int height) {
	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	    img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(imageData, imageData.length), new Point()));
	    
	    return img;
	}
	
	public static byte[] createPacket(Block b){
		byte[] image = b.getData().getImage();
		byte[] packet = new byte[5+image.length];
		
		packet[0] = 1;
		
		//X and Y at the first position
		packet[1] = (byte) b.getX();
		packet[2] = (byte) b.getY();
		
		//Reserved for compression quality
		packet[3] = 0;
		
		//Reserved for future use
		packet[4] = 0;
		
		//Transfer the image to the end of the packet
		for(int i = 0; i < image.length; i++){
			packet[i+5] = image[i];
		}
		
		return packet;
	}
	
	public static Block blockFromPacket(byte[] p){
		if(p[0] != 1){
			return null;
		}
		
		Block b = new Block();
		
		b.setX(p[1]);
		b.setY(p[2]);
		b.setData(Arrays.copyOfRange(p, 5, p.length));
		
		return b;
	}
	
	//Fixes "flattens" and "extracts" the 2D coordinates to 1D and vice versa
	public static class CoordFixer{
		int w;
		
		public CoordFixer(int width){
			w = width;
		}
		
		//2D coord to 1D index
		public int flatten(int x, int y){
			return (w*y)+x;
		}
		
		//1D index to 2D coord
		public Coord expand(int p){
			int x = p % w;
			int y = (p - x) / w;
			
			return new Coord(x,y);
		}
	}
}
