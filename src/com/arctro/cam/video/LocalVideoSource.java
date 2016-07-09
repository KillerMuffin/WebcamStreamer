package com.arctro.cam.video;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.arctro.cam.processor.Block;
import com.arctro.cam.processor.DifferenceProcessor;
import com.arctro.cam.processor.Processor;
import com.arctro.cam.supporting.Utils;
import com.arctro.cam.supporting.exceptions.FrameException;
import com.github.sarxos.webcam.Webcam;

//Recieves the local video, and processes it
public class LocalVideoSource implements VideoSource{
	public Webcam webcam;
	public Processor processor;
	
	boolean running = true;
	
	public LocalVideoSource(Webcam webcam, Processor processor){
		this.webcam = webcam;
		this.processor = processor;
	}
	
	public void start(){
		webcam.open(true);
		
		Thread t = new Thread(){
			public void run(){
				while(true){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					update();
				}
			}
		};
		t.start();
		
		webcam.open(false);
	}
	
	//Updates the frames and deals with the processor
	public void update(){
		try {
			BufferedImage f = webcam.getImage();
			if(f == null){
				return;
			}
			
			processor.addFrame(((DataBufferByte) f.getRaster().getDataBuffer()).getData());
		} catch (FrameException e) {
			e.printStackTrace();
		}
	}
	
	public void stop(){
		running = false;
	}
	
	public Block next(){
		return processor.getNext();
	}

	@Override
	public Dimension getSize() {
		return webcam.getViewSize();
	}

	@Override
	public int getWidth() {
		return webcam.getViewSize().width;
	}

	@Override
	public int getHeight() {
		return webcam.getViewSize().height;
	}

	@Override
	public byte[] getFrame() {
		return processor.getFullFrame();
	}
	
	@SuppressWarnings("unused")
	private void DEBUG_saveModel(){
		DifferenceProcessor p = (DifferenceProcessor) processor;
		BufferedImage img = Utils.createImageFromBytes(p.currentModel.getImage(), p.width, p.height);
		
		File outputfile = new File("di/"+System.currentTimeMillis() + ".jpg");
	    try {
			ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void DEBUG_saveDBlock(Block b){
		if(b==null){
			return;
		}
			
		BufferedImage img = Utils.createImageFromBytes(b.getData().getImage(), 64,1);
		
		File outputfile = new File("di/"+System.currentTimeMillis() + ".jpg");
	    try {
			ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
