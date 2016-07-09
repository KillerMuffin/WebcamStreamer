package com.arctro.cam.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.arctro.cam.supporting.exceptions.FrameException;
import com.arctro.cam.video.LocalVideoSource;

//Manages the local video feed
public class LocalCameraWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	
	CameraCanvas camera;
	LocalVideoSource manager;
	
	boolean running = true;
	
	public LocalCameraWindow(LocalVideoSource manager){
		this.manager = manager;
		manager.webcam.open(true);
		
		Dimension size = manager.webcam.getViewSize();
		
		camera = new CameraCanvas(manager);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		setSize(size);
		setTitle("Your Webcam");
		setLocationRelativeTo(null);
		
		add(camera);
		pack();
	}
	
	public void thing(){
		manager.update();
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
