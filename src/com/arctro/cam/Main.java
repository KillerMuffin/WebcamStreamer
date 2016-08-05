package com.arctro.cam;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import com.arctro.cam.audio.AudioBuffer;
import com.arctro.cam.processor.Block;
import com.arctro.cam.processor.DifferenceProcessor;
import com.arctro.cam.supporting.ImageHolder;
import com.arctro.cam.supporting.Packet;
import com.arctro.cam.supporting.Utils;
import com.arctro.cam.ui.ClientCameraWindow;
import com.arctro.cam.ui.LocalCameraWindow;
import com.arctro.cam.video.LocalVideoSource;
import com.github.sarxos.webcam.Webcam;

public class Main {

	static String address = "192.168.1.114";
	static InetAddress iAddress;
	static DatagramSocket socket;
	
	static ImageHolder currentFrame;
	static Webcam w;
	
	static boolean running = true;
	
	public static void main(String[] args) {
		//Get the client's ip address
		address = JOptionPane.showInputDialog("Enter a client IP");
		
		//Setup and open webcam
		w = Webcam.getDefault();
		w.open(true);
		
		//Setup the audio
		TargetDataLine mic = getMicrophone();
		AudioBuffer audioBuffer = new AudioBuffer(mic);
		if(mic != null){
			Thread at = new Thread(){
				public void run(){
					while(running){
						audioBuffer.capture();
					}
				}
			};
			at.start();
		}
		
		//Get the initial frame
		BufferedImage f = w.getImage();
		
		//Setup the client video source
		DifferenceProcessor erp = new DifferenceProcessor(new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_3BYTE_BGR));
		LocalVideoSource vm = new LocalVideoSource(w,erp);
		
		//Setup the local video source
		currentFrame = new ImageHolder(new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_3BYTE_BGR));
		
		//Setup the two windows
		ClientCameraWindow cWindow = new ClientCameraWindow(currentFrame, f.getWidth(), f.getHeight());
		LocalCameraWindow window = new LocalCameraWindow(vm);
		
		cWindow.start();
		
		window.start();
		//Place the local window next to the client window
		window.setLocation(cWindow.getX()+cWindow.getWidth(), cWindow.getY());
		
		//Connect to client
		try {
			iAddress = InetAddress.getByName(address);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			socket = new DatagramSocket(2001);
		} catch (UnknownHostException | SocketException e1) {
			e1.printStackTrace();
			//If connection failed, exit
			System.exit(0);
		}
		
		//Main send/recieve loop
		Thread t = new Thread(){
			public void run(){
				while(running){
					Block b = vm.next();
					
					try {
						send(b, audioBuffer);
						recieve();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		
		//When the user exits the window
		WindowListener exitListener = new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	//Stop the loop processes and webcam
		        running = false;
				w.open(false);
		        //Send the "stop" packet
		        sendFinal();
		        
		        //Exit application
		        System.exit(0);
		    }
		};
		//Bind to both windows
		cWindow.addWindowListener(exitListener);
		window.addWindowListener(exitListener);
	}
	
	//Prepare and send a packet
	public static void send(Block b, AudioBuffer ab) throws IOException{
		//Send no packet if the block is null (possible with a high minError value)
		if(b == null){
			return;
		}
		
		Packet p = new Packet(b, ab.getNext());
		byte[] buffer = p.prepare();
		
		//Send the packet to the client port 2001
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, iAddress, 2001);
		socket.send(packet);
	}
	
	//Send the "stop" packet
	public static void sendFinal(){
		//The stop packet is one byte, containing the value "0"
		byte[] buffer = new byte[1];
		buffer[0] = 0;
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, iAddress, 2001);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Recieve a packet
	public static void recieve() throws IOException{
		//65534 + PCO is the max size of a block and audio uncompressed
		byte[] buffer = new byte[65534 + Utils.PACKET_CONTENT_OFFSET];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(packet);
		
		//If the first byte == 0, then it is the "stop" packet
		if(buffer[0] != 1){
			System.exit(0);
		}
		
		//Convert back
		Packet p = new Packet(buffer);
		Block b = p.getBlock();
		
		short audioLength = p.getAudioLength();
		System.out.println(audioLength);
		
		//Not necessary, but just in case
		if(b == null){
			return;
		}
		
		//Update the local frame
		currentFrame.set(b);
	}

	public static TargetDataLine getMicrophone(){
		if(!AudioSystem.isLineSupported(Port.Info.MICROPHONE)){
			return null;
		}
		
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
	    try {
			return AudioSystem.getTargetDataLine(format);
			
		//This exception should not happen but whatever
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
}
