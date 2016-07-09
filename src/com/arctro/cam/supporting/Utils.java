package com.arctro.cam.supporting;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

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
	public static final int MAX_SEND_RATE = 25; //Unused
	
	public static final int PACKET_CONTENT_OFFSET = 7;
	
	//Turns the byte array into a BufferedImage
	public static BufferedImage createImageFromBytes(byte[] imageData, int width, int height) {
	    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
	    img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(imageData, imageData.length), new Point()));
	    
	    return img;
	}
	
	public static byte[] shortToBytes(short s){
		return new byte[]{
				(byte)(s>>>8),
				(byte)(s&0xFF)}
		;
	}
	
	public static short bytesToShort(byte[] b){
		if(b.length > 2){
			throw new RuntimeException("Byte array too large! Must have a length of 2 (not " + b.length + ")");
		}
		
		return (short)(
				(
						(b[0]&0xFF)
						<<8)|
				(b[1]&0xFF)
				);
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
