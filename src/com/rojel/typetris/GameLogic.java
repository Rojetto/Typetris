package com.rojel.typetris;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.JLabel;

public class GameLogic {
	public boolean running = true;
	public boolean paused = false;
	public String[] commands = {"rechts", "links", "runter", "dreh rechts", "dreh links" };
	public String[] icons = {"\u2192", "\u2190", "\u2193", "\u21bb", "\u21ba" };
	public boolean[][][] blocks = { { {false, false, false, false }, {false, false, false, false }, {true, true, true, true }, {false, false, false, false } }, {
	
	{false, false, false, false }, {false, false, true, false }, {true, true, true, false }, {false, false, false, false } }, {
	
	{false, false, false, false }, {true, true, true, false }, {false, false, true, false }, {false, false, false, false } }, {
	
	{false, false, false, false }, {false, true, true, false }, {false, true, true, false }, {false, false, false, false } }, {
	
	{false, true, false, false }, {false, true, true, false }, {false, false, true, false }, {false, false, false, false } }, {
	
	{false, false, true, false }, {false, true, true, false }, {false, false, true, false }, {false, false, false, false } }, {
	
	{false, false, true, false }, {false, true, true, false }, {false, true, false, false }, {false, false, false, false } } };
	
	private boolean[][] map;
	private Color[][] colorMap;
	private boolean[][] block;
	private Color blockColor;
	private int blockX;
	private int blockY;
	private boolean[][] next;
	private Color nextColor;
	private String[] queue;
	private boolean boost;
	private int score;
	
	private long lastTick;
	private long lastCommand;
	private long startTick;
	private long tick;
	
	private GameDisplay gameDisplay;
	private NextDisplay nextDisplay;
	private QueueDisplay queueDisplay;
	private JLabel scoreDisplay;
	
	private SoundPlayer soundPlayer;
	
	public GameLogic(GameDisplay gameDisplay, NextDisplay nextDisplay, QueueDisplay queueDisplay, JLabel scoreDisplay) {
		map = new boolean[10][20];
		colorMap = new Color[10][20];
		block = new boolean[5][5];
		next = new boolean[5][5];
		queue = new String[9];
		Arrays.fill(queue, "");
		boost = false;
		score = 0;
		
		this.gameDisplay = gameDisplay;
		this.nextDisplay = nextDisplay;
		this.queueDisplay = queueDisplay;
		this.scoreDisplay = scoreDisplay;
		
		this.gameDisplay.setGameLogic(this);
		this.nextDisplay.setGameLogic(this);
		this.queueDisplay.setGameLogic(this);
		
		this.lastTick = System.currentTimeMillis();
		this.lastCommand = System.currentTimeMillis();
		this.startTick = 2000;
		this.tick = startTick;
		
		this.soundPlayer = new SoundPlayer();
	}
	
	public void run() {
		this.nextBlock();
		this.nextBlock();
		
		soundPlayer.startMusic();
		
		while(this.running) {
			while(this.paused) {
				
			}
			
			if(System.currentTimeMillis() - this.lastTick > this.tick || this.boost) {
				this.lastTick = System.currentTimeMillis();
				
				if(this.isFree(0, 1)) {
					this.blockY++;
				} else {
					if(this.blockY < 0) {
						soundPlayer.stopMusic();
						soundPlayer.playSound(SoundPlayer.GAMEOVER);
						this.running = false;
					} else {
						soundPlayer.playSound(SoundPlayer.DROP);
						this.addBlockToMap();
						this.nextBlock();
						this.tick = (int) (this.tick * 0.92);
						this.score = this.score + 10;
						this.boost = false;
					}
				}
			}
			
			if(System.currentTimeMillis() - this.lastCommand > this.tick / 2) { // Commands werden doppelt so oft ausgeführt wie Ticks
				this.lastCommand = System.currentTimeMillis();
				
				this.runCommand(this.queue[0]);
				this.removeCommand(0);
			}
			
			scoreDisplay.setText("Score:" + score); // Displays updaten
			gameDisplay.repaint();
			nextDisplay.repaint();
			queueDisplay.repaint();
			
			this.checkRows();
		}
	}
	
	private void checkRows() { // Überprüft alle Reihen von unten nach oben und entfernt sie, wenn sie voll sind
		for(int y = 0; y < 20; y++) {
			if(isRowFull(y)) {
				removeRow(y);
				this.score = this.score + 100;
				soundPlayer.playSound(SoundPlayer.ROW);
			}
		}
	}
	
	private boolean isRowFull(int index) { // Überprüft die Reihe "index"
		boolean result = true;
		
		for(int x = 0; x < 10; x++) {
			if(!map[x][index])
				result = false;
		}
		
		return result;
	}
	
	private void removeRow(int index) { // Entfernt die Reihe und lässt die anderen nachrutschen
		for(int y = index; y > 0; y--) {
			for(int x = 0; x < 10; x++) {
				this.map[x][y] = this.map[x][y - 1];
				this.colorMap[x][y] = this.colorMap[x][y - 1];
			}
		}
	}
	
	private void addBlockToMap() {
		for(int y = 0; y < block.length; y++) {
			for(int x = 0; x < block.length; x++) {
				if(block[x][y]) {
					this.map[x + blockX][y + blockY] = true;
					this.colorMap[x + blockX][y + blockY] = this.blockColor;
				}
			}
		}
	}
	
	private void runCommand(String command) {
		if(command.equalsIgnoreCase("rechts")) {
			if(this.isFree(1, 0))
				this.blockX++;
		} else if(command.equalsIgnoreCase("links")) {
			if(this.isFree(-1, 0))
				this.blockX--;
		} else if(command.equalsIgnoreCase("dreh rechts")) {
			this.rotateRight();
			if(!isFree(0, 0)) { // Wenn Drehung nicht möglich ist, wird versucht, den Block nach links oder nach rechts zu verschieben bis es geht
				int freeX = 0;
				
				for(int i = -2; i <= 2; i++) {
					if(isFree(i, 0)) {
						freeX = i;
					}
				}
				
				this.blockX = this.blockX + freeX;
				
				if(freeX == 0) {
					this.rotateLeft();
				}
			}
		} else if(command.equalsIgnoreCase("dreh links")) {
			this.rotateLeft();
			if(!isFree(0, 0)) {
				int freeX = 0;
				
				for(int i = -2; i <= 2; i++) {
					if(isFree(i, 0)) {
						freeX = i;
					}
				}
				
				this.blockX = this.blockX + freeX;
				
				if(freeX == 0) {
					this.rotateRight();
				}
			}
		} else if(command.equalsIgnoreCase("runter")) {
			this.boost = true;
		}
	}
	
	public void addCommand(String command) {
		int freeIndex = 0;
		while(!this.queue[freeIndex].equals("")) {
			freeIndex++;
		}
		
		this.queue[freeIndex] = command;
	}
	
	public void removeCommand(int index) {
		for(int i = index; i < this.queue.length - 1; i++) {
			this.queue[i] = this.queue[i + 1];
		}
		
		this.queue[this.queue.length - 1] = "";
	}
	
	public boolean isValid(String command) {
		for(int i = 0; i < commands.length; i++) {
			if(commands[i].equalsIgnoreCase(command))
				return true;
		}
		
		return false;
	}
	
	public String getIcon(String command) {
		if(this.isValid(command)) {
			for(int i = 0; i < this.commands.length; i++) {
				if(this.commands[i].equalsIgnoreCase(command))
					return this.icons[i];
			}
		}
		
		return "\u2613";
	}
	
	public void rotateRight() {
		boolean[][] rotatedBlock = new boolean[block.length][block.length];
		
		for(int y = 0; y < block.length; y++) {
			for(int x = 0; x < block.length; x++) {
				rotatedBlock[block.length - 1 - y][x] = block[x][y];
			}
		}
		
		this.block = rotatedBlock;
	}
	
	public void rotateLeft() {
		boolean[][] rotatedBlock = new boolean[block.length][block.length];
		
		for(int y = 0; y < block.length; y++) {
			for(int x = 0; x < block.length; x++) {
				rotatedBlock[y][block.length - 1 - x] = block[x][y];
			}
		}
		
		this.block = rotatedBlock;
	}
	
	public void nextBlock() {
		this.block = this.next;
		this.blockColor = this.nextColor;
		this.next = this.blocks[(int) (Math.random() * this.blocks.length)];
		
		int r = (int) (Math.random() * 256);
		int g = (int) (Math.random() * 256);
		int b = (int) (Math.random() * 256);
		this.nextColor = new Color(r, g, b);
		
		blockX = 4;
		blockY = -3;
	}
	
	public boolean isFree(int xOffset, int yOffset) {
		for(int y = 0; y < block.length; y++) {
			for(int x = 0; x < block.length; x++) {
				if(block[x][y]) {
					boolean collides = false;
					
					try {
						collides = map[blockX + x + xOffset][blockY + y + yOffset];
					} catch(ArrayIndexOutOfBoundsException e) {
						if(blockY + y + yOffset >= 0) { // Nur kollidieren, wenn
														// Block an die linke,
														// rechte oder untere
														// Kante drückt
							collides = true;
						} else {
							collides = false;
						}
					}
					
					if(collides)
						return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean[][] getMap() {
		return this.map;
	}
	
	public Color[][] getColorMap() {
		return this.colorMap;
	}
	
	public boolean[][] getBlock() {
		return this.block;
	}
	
	public Color getBlockColor() {
		return this.blockColor;
	}
	
	public int getBlockX() {
		return this.blockX;
	}
	
	public int getBlockY() {
		return this.blockY;
	}
	
	public boolean[][] getNext() {
		return this.next;
	}
	
	public Color getNextColor() {
		return this.nextColor;
	}
	
	public String[] getQueue() {
		return this.queue;
	}
	
	public int getScore() {
		return this.score;
	}
}
