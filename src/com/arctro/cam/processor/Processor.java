package com.arctro.cam.processor;

import com.arctro.cam.supporting.exceptions.FrameException;

public interface Processor {
	public void addFrame(byte[] b) throws FrameException;
	public Block getNext();
	public byte[] getFullClientFrame();
	public byte[] getFullFrame();
}
