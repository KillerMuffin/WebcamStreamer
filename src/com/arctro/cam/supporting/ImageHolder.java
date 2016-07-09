package com.arctro.cam.supporting;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.Serializable;

import com.arctro.cam.processor.Block;
import com.arctro.cam.video.VideoSource;

//Holds the image data and provides easy methods to access it
public class ImageHolder implements Serializable, VideoSource{
	private static final long serialVersionUID = 1L;
	
	//Holds the image data
	byte[] image;
	int w,h;
	
	//Fixes "flattens" and "extracts" the 2D coordinates to 1D and vice versa
	Utils.CoordFixer cf;
	
	public ImageHolder(byte[] image, int width, int height){
		this.image = image;
		w = width;
		h = height;
		
		//The CoordFixer records the width as 3*width because an image pixel takes up three pixels
		cf = new Utils.CoordFixer(width*3);
	}
	
	public ImageHolder(BufferedImage image){
		this(((DataBufferByte) image.getRaster().getDataBuffer()).getData(), image.getWidth(), image.getHeight());
	}
	
	/**
	 * Returns the pixel at a position
	 * @param bx The block x
	 * @param by The block y
	 * @param x The x
	 * @param y The y
	 * @return The pixel at a position
	 */
	public byte[] get(int bx, int by, int x, int y){
		x = ((bx * Utils.BLOCK_SIZE) + x)*3;
		y = (by * Utils.BLOCK_SIZE) + y;
		
		int p = cf.flatten(x, y);
		
		byte[] r = new byte[3];
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			r[i] = image[p+i];
		}
		
		return r;
	}
	
	/**
	 * Returns the pixel at a position
	 * @param x The x
	 * @param y The y
	 * @return The pixel at a position
	 */
	public byte[] get(int x, int y){
		int p = cf.flatten(x*3, y);
		
		byte[] r = new byte[3];
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			r[i] = image[p+i];
		}
		
		return r;
	}
	
	/**
	 * Returns the pixel at a position
	 * @param c The coordinate of the pixel
	 * @return The pixel at a position
	 */
	public byte[] get(Coord c){
		int p = cf.flatten(c.getX()*3, c.getY());
		
		byte[] r = new byte[3];
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			r[i] = image[p+i];
		}
		
		return r;
	}
	
	/**
	 * Sets the pixel at a position
	 * @param bx The block x
	 * @param by The block y
	 * @param x The x
	 * @param y The y
	 * @param v The pixel value (length should be three, as BGR)
	 */
	public void set(int bx, int by, int x, int y, byte[] v){
		x = ((bx * Utils.BLOCK_SIZE) + x)*3;
		y = (by * Utils.BLOCK_SIZE) + y;
		
		int p = cf.flatten(x, y);
		
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			image[p+i] = v[i];
		}
	}
	
	/**
	 * Sets the pixel at a position
	 * @param x The x
	 * @param y The y
	 * @param v The pixel value (length should be three, as BGR)
	 */
	public void set(int x, int y, byte[] v){
		int p = cf.flatten(x*3, y);
		
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			image[p+i] = v[i];
		}
	}
	
	/**
	 * Sets the pixel at a position
	 * @param c The coordinate of the pixel
	 * @param v The pixel value (length should be three, as BGR)
	 */
	public void set(Coord c, byte[] v){
		int p = cf.flatten(c.getX()*3, c.getY());
		
		for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
			image[p+i] = v[i];
		}
	}
	
	/**
	 * Sets a block
	 * @param b The block data to insert
	 */
	public void set(Block b){
		for(int x = 0; x < Utils.BLOCK_SIZE; x++){
			for(int y = 0; y < Utils.BLOCK_SIZE; y++){
				set(b.getX(), b.getY(), x, y, b.getData().get(x, y));
			}
		}
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getWidth() {
		return w/3;
	}

	public void setWidth(int w) {
		this.w = w*3;
	}

	public int getHeight() {
		return h;
	}

	public void setHeight(int h) {
		this.h = h;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(w,h);
	}

	@Override
	public byte[] getFrame() {
		return getImage();
	}
}
