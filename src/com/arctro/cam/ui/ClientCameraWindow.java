package com.arctro.cam.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
}
