package com.arctro.cam.audio;

import java.util.Arrays;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/*
 * This class holds a buffer of audio data to be streamed, and
 * automatically makes sure that the amount of audio sent does
 * not exceed the limit.
 */
public class AudioBuffer {
	byte[] buffer;
	TargetDataLine dl;
	
	boolean running = true;
	
	public AudioBuffer(TargetDataLine dl){
		buffer = new byte[Short.MAX_VALUE];
		this.dl = dl;
	}
	
	public void start() throws LineUnavailableException{
		dl.open();
		
		running = true;
		while(running){
			capture();
		}
		
		dl.close();
	}
	
	public void capture(){
		if(dl != null){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		
		dl.read(buffer, 0, buffer.length);
		increaseBuffer();
	}
	
	public void stop(){
		running = false;
	}
	
	public byte[] getNext(){
		if(bufferSize() < Short.MAX_VALUE){
			byte[] r = Arrays.copyOfRange(buffer, 0, bufferSize());
			buffer = new byte[Short.MAX_VALUE];
			
			return r;
		}else{
			byte[] r = Arrays.copyOfRange(buffer, 0, Short.MAX_VALUE);
			buffer = Arrays.copyOfRange(buffer, Short.MAX_VALUE, bufferSize());
			
			return r;
		}
	}
	
	private int bufferSize(){
		int c = 0;
		for(int i = 0; i < buffer.length; i++){
			if(buffer[i] != 0){
				c++;
			}else{
				return c;
			}
		}
		
		return c;
	}
	
	private void increaseBuffer(){
		byte[] tmp = new byte[bufferSize() + Short.MAX_VALUE];
		System.arraycopy(buffer,0,tmp,0,buffer.length);
		
		buffer = tmp;
	}
}
