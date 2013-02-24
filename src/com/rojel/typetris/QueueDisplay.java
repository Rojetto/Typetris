package com.rojel.typetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class QueueDisplay extends Canvas implements MouseListener {
	private static final long serialVersionUID = -4664196160421993186L;

	private GameLogic logic;
	
	private Image bufImage;
	private Graphics bufG;
	
	public QueueDisplay() {
		this.addMouseListener(this);
	}
	
	public void setGameLogic(GameLogic logic) {
		this.logic = logic;
	}
	
	public void paint(Graphics g) {
		Font font = new Font("Helvetica", Font.BOLD, 30);
		g.setFont(font);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 120, 475);
		
		g.setColor(Color.BLACK);
		
		try {			
			for(int i = 0; i < this.logic.getQueue().length; i++) {
				String command = this.logic.getQueue()[i];
				
				if(!command.equals("")) {
					g.drawRoundRect(0, i * 50, 120, 50, 20, 20);
					g.drawString(logic.getIcon(command), 45, 35 + i * 50);
				}
			}
		} catch(NullPointerException e) {
			System.out.println("GameLogic noch nicht an QueueDisplay übergeben");
		}
		
		g.setColor(Color.GRAY);
		g.drawRect(0, 0, 119, 474);
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

	public void mouseClicked(MouseEvent arg0) {
		logic.removeCommand((int) (arg0.getY() / 50));
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
