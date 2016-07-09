package com.arctro.cam.processor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import com.arctro.cam.supporting.Coord;
import com.arctro.cam.supporting.ImageHolder;
import com.arctro.cam.supporting.Utils;
import com.arctro.cam.supporting.exceptions.FrameException;

public class DifferenceProcessor implements Processor{
	//Holds the image that the client currently possesses
	public ImageHolder currentModel;
	//Holds the last frame of video from the webcam
	public ImageHolder lastFrame;
	
	//Width, Height, Width * Height, Block Count, Block Count Width, Block Count Height, Minimum Error For new packet 
	public int width, height, wh, nb, nbw, nbh;
	
	public DifferenceProcessor(BufferedImage initial){
		//Store these values to save computing time
		this.width = initial.getWidth();
		this.height = initial.getHeight();
		wh = width * height;
		
		nbw = ((int) width/Utils.BLOCK_SIZE)+1;
		nbh = ((int) height/Utils.BLOCK_SIZE);
		nb = nbw*nbh;
		
		//Set the initial frame
		currentModel = new ImageHolder(((DataBufferByte) initial.getRaster().getDataBuffer()).getData(), width, height);
		lastFrame = new ImageHolder(((DataBufferByte) initial.getRaster().getDataBuffer()).getData(), width, height);
		
		System.out.println("Width: " + width + " Height: " + height + " wh: " + wh + " nb: " + nb + " nbw: " + nbw + " nbh: " + nbh);
		System.out.println(currentModel.getImage().length);
	}

	//Updates the last frame
	@Override
	public void addFrame(byte[] b) throws FrameException {
		if(b.length != lastFrame.getImage().length){
			throw new FrameException("The new frame is not the same size as the old one!");
		}
		
		lastFrame.setImage(b);
	}

	//Calculates and returns the next block to be sent
	@Override
	public Block getNext() {
		//If there are no changes, no packet should be sent
		if(currentModel.equals(lastFrame)){
			return null;
		}
		
		Block b = new Block();
		//Find the block with the largest difference
		for(int x = 0; x < nbw; x++){
			for(int y = 0; y < nbh; y++){
				Coord pos = new Coord(x,y,true);
				
				double cd = blockDifference(pos);
				if(b.getDifference() < cd){
					b = new Block(pos.getBlockX(), pos.getBlockY(), cd, getBlock(pos, lastFrame).getImage());
				}
			}
		}
		
		//Update the model
		currentModel.set(b);
		
		return b;
	}
	
	//Calculates the difference between the lastFrame and currentModel at a position using mean square root
	private double blockDifference(Coord pos){
		double totall = 0;
		double totalm = 0;
		
		for(int x = 0; x < Utils.BLOCK_SIZE; x++){
			for(int y = 0; y < Utils.BLOCK_SIZE; y++){
				int pixell = 0;
				int pixelm = 0;
				
				byte[] pl = lastFrame.get(pos.getBlockX(), pos.getBlockY(), x, y);
				byte[] pm = currentModel.get(pos.getBlockX(), pos.getBlockY(), x, y);
				for(int i = 0; i < Utils.PIXEL_LENGTH; i++){
					pixell += pl[i];
					pixelm += pm[i];
				}
				
				pixell/=3;
				pixelm/=3;
				
				totall+=Math.pow(pixell, 2);
				totalm+=Math.pow(pixelm, 2);
			}
		}
		
		totall = Math.sqrt((1.0/(double)Utils.S_BLOCK_SIZE)*totall);
		totalm = Math.sqrt((1.0/(double)Utils.S_BLOCK_SIZE)*totalm);
		
		double diff = Math.abs(totall - totalm);
		
		return diff;
	}

	//Returns the block at a position
	private ImageHolder getBlock(Coord pos, ImageHolder frame){
		ImageHolder ih = new ImageHolder(new byte[Utils.S_BLOCK_BYTE_SIZE], Utils.BLOCK_SIZE, Utils.BLOCK_SIZE);
		
		for(int x = 0; x < Utils.BLOCK_SIZE; x++){
			for(int y = 0; y < Utils.BLOCK_SIZE; y++){
				ih.set(x, y, frame.get(pos.getBlockX(), pos.getBlockY(), x, y));
			}
		}
		
		return ih;
	}

	@Override
	public byte[] getFullClientFrame() {
		return currentModel.getImage();
	}

	@Override
	public byte[] getFullFrame() {
		return lastFrame.getImage();
	}
}
