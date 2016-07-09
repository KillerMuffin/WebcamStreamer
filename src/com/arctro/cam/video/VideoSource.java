package com.arctro.cam.video;

import java.awt.Dimension;

public interface VideoSource {
	public Dimension getSize();
	public int getWidth();
	public int getHeight();
	
	public byte[] getFrame();
}
