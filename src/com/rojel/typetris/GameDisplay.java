package com.rojel.typetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GameDisplay extends Canvas {
	private static final long serialVersionUID = 7526058709315001052L;

	private GameLogic logic;
	
	private Image bufImage;
	private Graphics bufG;
	
	public void setGameLogic(GameLogic logic) {
		this.logic = logic;
	}
	
	public void paint(Graphics g) {
		try {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 300, 600);
			
			for(int y = 0; y < 20; y++) {
				for(int x = 0 ; x < 10; x++) {
					if(logic.getMap()[x][y]) {
						g.setColor(logic.getColorMap()[x][y]);
						g.fillRect(x * 30, y * 30, 30, 30);
						g.setColor(Color.BLACK);
						g.drawRect(x * 30, y * 30, 30, 30);
					}
				}
			}
			
			for(int y = 0; y < 4; y++) {
				for(int x = 0; x < 4; x++) {
					if(logic.getBlock()[x][y]) {
						g.setColor(logic.getBlockColor());
						g.fillRect(x * 30 + logic.getBlockX() * 30, y * 30 + logic.getBlockY() * 30, 30, 30);
						g.setColor(Color.BLACK);
						g.drawRect(x * 30 + logic.getBlockX() * 30, y * 30 + logic.getBlockY() * 30, 30, 30);
					}
				}
			}
		} catch(NullPointerException e) {
			System.out.println("GameLogic noch nicht an GameDisplay übergeben");
		}
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, 299, 599);
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
