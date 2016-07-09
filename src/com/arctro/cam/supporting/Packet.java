package com.arctro.cam.supporting;

import java.util.Arrays;

import com.arctro.cam.processor.Block;

public class Packet {
	byte[] data;
	
	public Packet(){
		data = new byte[0];
	}
	
	public Packet(byte[] data){
		this.data = data;
	}
	
	public Packet(Block b){
		byte[] image = b.getData().getImage();
		byte[] packet = new byte[Utils.PACKET_CONTENT_OFFSET+image.length];
		
		packet[0] = 1;
		
		//X and Y at the first position
		packet[1] = (byte) b.getX();
		packet[2] = (byte) b.getY();
		
		//Reserved for compression quality
		packet[3] = 0;
		
		//Image size (short stored across two bytes)
		byte[] imageLength = Utils.shortToBytes((short)image.length);
		packet[4] = imageLength[0];
		packet[5] = imageLength[1];
		
		//Audio size (short stored across two bytes)
		//Will be used when the audio component is introduced
		packet[6] = 0;
		packet[7] = 0;
		
		//Transfer the image to the end of the packet
		for(int i = 0; i < image.length; i++){
			packet[i+Utils.PACKET_CONTENT_OFFSET] = image[i];
		}
		
		data = packet;
	}
	
	public byte[] getVideo(){
		return Arrays.copyOfRange(data, Utils.PACKET_CONTENT_OFFSET, Utils.PACKET_CONTENT_OFFSET+Utils.bytesToShort(Arrays.copyOfRange(data, 4,5)));
	}
	
	public byte[] getAudio(){
		int start = Utils.PACKET_CONTENT_OFFSET+Utils.bytesToShort(Arrays.copyOfRange(data, 4,5));
		return Arrays.copyOfRange(data, start, start+Utils.bytesToShort(Arrays.copyOfRange(data, 6,7)));
	}
	
	public Block getBlock(){
		if(data[0] != 1){
			return null;
		}
		
		Block b = new Block();
		
		b.setX(data[1]);
		b.setY(data[2]);
		b.setData(Arrays.copyOfRange(data, Utils.PACKET_CONTENT_OFFSET, data.length));
		
		return b;
	}
	
	public byte[] prepare(){
		return data;
	}
}