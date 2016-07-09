package com.arctro.cam.supporting;

public class Coord {
	int x, y;
	boolean block;
	
	public Coord(){
		x = y = -1;
		block=false;
	}

	public Coord(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		block = false;
	}

	public Coord(int x, int y, boolean block) {
		super();
		this.x = (block) ? x * Utils.BLOCK_SIZE : x;
		this.y = (block) ? y * Utils.BLOCK_SIZE : y;
		this.block = block;
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
	
	public int getBlockX(){
		return Math.floorDiv(x, Utils.BLOCK_SIZE);
	}
	
	public void setBlockX(int x){
		this.x = x * Utils.BLOCK_SIZE;
	}
	
	public int getBlockY(){
		return Math.floorDiv(y, Utils.BLOCK_SIZE);
	}
	
	public void setBlockY(int y){
		this.y = y * Utils.BLOCK_SIZE;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}
}
