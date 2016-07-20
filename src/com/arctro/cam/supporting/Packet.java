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
	
	public Packet(Block b, byte[] audio){
		byte[] image = b.getData().getImage();
		byte[] packet = new byte[Utils.PACKET_CONTENT_OFFSET+image.length+audio.length];
		
		packet[0] = 1;
		
		//X and Y at the first position
		packet[1] = (byte) b.getX();
		packet[2] = (byte) b.getY();
		
		//Reserved for compression quality
		packet[3] = 0;
		
		//Image size (short stored across two bytes)
		byte[] imageLength = Utils.shortToBytes((short) image.length);
		packet[4] = imageLength[0];
		packet[5] = imageLength[1];
		
		//Audio size (short stored across two bytes)
		//Will be used when the audio component is introduced
		byte[] audioLength = Utils.shortToBytes((short) audio.length);
		packet[6] = audioLength[0];
		packet[7] = audioLength[1];
		
		//Transfer the image to the end of the packet
		for(int i = 0; i < image.length; i++){
			packet[i+Utils.PACKET_CONTENT_OFFSET] = image[i];
		}
		
		//Transfer the audio to the end of the packet
		for(int i = 0; i < audio.length; i++){
			packet[i+image.length+Utils.PACKET_CONTENT_OFFSET] = audio[i];
		}
		
		data = packet;
	}
	
	public short getVideoLength(){
		return Utils.bytesToShort(Arrays.copyOfRange(data, 4,5));
	}
	
	public byte[] getVideo(){
		return Arrays.copyOfRange(data, Utils.PACKET_CONTENT_OFFSET, Utils.PACKET_CONTENT_OFFSET+getVideoLength());
	}
	
	public short getAudioLength(){
		return Utils.bytesToShort(Arrays.copyOfRange(data, 6,7));
	}
	
	public byte[] getAudio(){
		int start = Utils.PACKET_CONTENT_OFFSET+getVideoLength();
		return Arrays.copyOfRange(data, start, start+getAudioLength());
	}
	
	public Block getBlock(){
		if(data[0] != 1){
			return null;
		}
		
		Block b = new Block();
		
		b.setX(data[1]);
		b.setY(data[2]);
		b.setData(getVideo());
		
		return b;
	}
	
	public byte[] prepare(){
		return data;
	}
}
