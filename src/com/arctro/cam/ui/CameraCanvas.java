package com.arctro.cam.ui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.arctro.cam.supporting.Utils;
import com.arctro.cam.supporting.exceptions.FrameException;
import com.arctro.cam.video.VideoSource;

//Paints the videosource onto the canvas
public class CameraCanvas extends Canvas{
	private static final long serialVersionUID = 1L;

	VideoSource manager;
	
	int width, height;
	boolean repaintInProgress = false;
	BufferedImage[] icons;
	
	public CameraCanvas(VideoSource manager){
		this(manager, new BufferedImage[0]);
	}
	
	public CameraCanvas(VideoSource manager, BufferedImage[] icons){
		this.manager = manager;
		
		Dimension size = manager.getSize();
		width = size.width;
		height = size.height;
		
		setPreferredSize(size);
		setIgnoreRepaint(true);
		
		this.icons = icons;
	}
	
	public CameraCanvas(Dimension size){
		width = size.width;
		height = size.height;
		
		setPreferredSize(size);
		setIgnoreRepaint(true);
	}
	
	public void paint() throws FrameException{
		if(manager == null){
			throw new FrameException("No video source!");
		}
		
		paint(manager.getFrame());
	}
	
	public void paint(byte[] frame){
		if(repaintInProgress){
			return;
		}
		
		repaintInProgress = true;
		
		BufferedImage realFrame = Utils.createImageFromBytes(frame, width, height);
		
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(2);
			repaintInProgress = false;
			return;
		}
		
		do{
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			
			//Draw frame to screen
			g.drawImage(realFrame, 0, 0, width, height, null);
			
			//Draw icons to screen
			int lastPos = 0;
			for(int i = 0; i < icons.length; i++){
				BufferedImage bi = icons[i];
				g.drawImage(bi, lastPos+5, (height-5)-bi.getHeight(), bi.getWidth(), bi.getHeight(), null);
				lastPos+=5+bi.getWidth();
			}
			
			if(g != null){
				g.dispose();
			}
			
			bs.show();
			Toolkit.getDefaultToolkit().sync();
		}while(bs.contentsLost());
		
		repaintInProgress = false;
	}
	
	public void setIcons(BufferedImage[] icons){
		this.icons = icons;
	}
}
