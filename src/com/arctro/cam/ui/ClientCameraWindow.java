package com.arctro.cam.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.arctro.cam.supporting.ImageHolder;
import com.arctro.cam.supporting.exceptions.FrameException;

//Manages the client video feed
public class ClientCameraWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	
	CameraCanvas camera;
	
	public ClientCameraWindow(ImageHolder image, int width, int height){
		camera = new CameraCanvas(image);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setSize(new Dimension(width, height));
		setTitle("Client Webcam");
		setLocationRelativeTo(null);
		
		add(camera);
		pack();
	}
	
	public void thing(){
		try {
			camera.paint();
		} catch (FrameException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		setVisible(true);
		
		new Timer(10, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				thing();
			}
		}).start();
	}
	
	public void setAudioAvailable(boolean available){
		if(available){
			BufferedImage[] icons = new BufferedImage[1];
			try {
				icons[0] = ImageIO.read(Files.newInputStream(Paths.get("res/ic_audiotrack_white_24dp_1x.png")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			camera.setIcons(icons);
		}else{
			camera.setIcons(new BufferedImage[0]);
		}
	}
}
