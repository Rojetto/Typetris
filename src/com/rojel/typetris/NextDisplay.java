package com.rojel.typetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class NextDisplay extends Canvas {
	private static final long serialVersionUID = -4417810579331602535L;

	private GameLogic logic;
	
	private Image bufImage;
	private Graphics bufG;
	
	public void setGameLogic(GameLogic logic) {
		this.logic = logic;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 120, 120);
		
		try {			
			for(int y = 0; y < 4; y++) {
				for(int x = 0; x < 4; x++) {
					if(logic.getNext()[x][y]) {
						g.setColor(logic.getNextColor());
						g.fillRect(x * 30, y * 30, 30, 30);
						g.setColor(Color.BLACK);
						g.drawRect(x * 30, y * 30, 30, 30);
					}
				}
			}
		} catch(NullPointerException e) {
			System.out.println("GameLogic noch nicht an NextDisplay übergeben.");
		}
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, 119, 119);
	}
	
	public void update(Graphics g) { //Double-Buffering
		int w = this.getSize().width;
		int h = this.getSize().height;
		
		if(bufImage == null) {
			bufImage = this.createImage(w, h);
			bufG = bufImage.getGraphics();
		}
		
		bufG.setColor(this.getBackground());
		bufG.fillRect(0, 0, w, h);
		
		bufG.setColor(this.getForeground());
		
		paint(bufG);
		
		g.drawImage(bufImage, 0, 0, this);
	}
}
