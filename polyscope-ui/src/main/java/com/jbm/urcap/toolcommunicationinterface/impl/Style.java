package com.jbm.urcap.toolcommunicationinterface.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Style {
	private BufferedImage image;
	
	public Image getImage(String name,int width, int heigth) {					
		
		try {
			if (name == "nok") {
				image = ImageIO.read(this.getClass().getResource("/nok.PNG"));
			} else if (name == "ok") {
				image = ImageIO.read(this.getClass().getResource("/ok.PNG"));
			} 				
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("FAILED TO LOAD IMAGE");
		}
		Image imagescaled = image.getScaledInstance(width, heigth, Image.SCALE_SMOOTH);
		return imagescaled;
	}
}
