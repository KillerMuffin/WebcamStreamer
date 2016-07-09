package com.arctro.cam.processor;

import com.arctro.cam.supporting.ImageHolder;
import com.arctro.cam.supporting.Utils;

//A block is 64 pixels
public class Block {
	//x,y refers to the block position
	int x,y;
	double difference;
	ImageHolder data;
	
	public Block(){
		difference = x = y = -1;
		data = null;
	}
	
	public Block(int x, int y, double difference, byte[] data) {
		super();
		this.x = x;
		this.y = y;
		this.difference = difference;
		this.data = new ImageHolder(data, Utils.BLOCK_SIZE, Utils.BLOCK_SIZE);
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDifference() {
		return difference;
	}

	public void setDifference(double difference) {
		this.difference = difference;
	}

	public ImageHolder getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = new ImageHolder(data, Utils.BLOCK_SIZE, Utils.BLOCK_SIZE);
	}
}
