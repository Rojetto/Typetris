package com.rojel.typetris;

import java.io.File;
import java.util.ArrayList;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer implements MetaEventListener {
	public static final String SOUND_DIRECTORY = "typetris_resources/sound/";
	public static final String MUSIC_DIRECTORY = "typetris_resources/music/";
	
	public static final int DROP = 1;
	public static final int DROP_COUNT = 7;
	public static final int ROW = 2;
	public static final int GAMEOVER = 3;
	public static final int MUSIC_COUNT = new File(MUSIC_DIRECTORY).listFiles().length;
	
	public long tempoFactor = 1;
	
	private Sequencer sequencer;
	private int[] playlist;
	private int track;
	
	public void playSound(String path) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch(Exception e) {
			System.out.println("Fehler beim Sound.");
			e.printStackTrace();
		}
	}
	
	public void playSound(int sound) {
		if(sound == SoundPlayer.DROP) {
			this.playSound(SOUND_DIRECTORY + "drop" + (int) (Math.random() * SoundPlayer.DROP_COUNT + 1) + ".wav");
		} else if(sound == SoundPlayer.ROW) {
			this.playSound(SOUND_DIRECTORY + "row.wav");
		} else if(sound == SoundPlayer.GAMEOVER) {
			this.playSound(SOUND_DIRECTORY + "gameover.wav");
		}
	}
	
	public void startMusic() {
		try {
			if(playlist == null || track >= playlist.length) {
				System.out.println(SoundPlayer.MUSIC_COUNT + " Musikdateien gefunden.");
				this.track = 0;
				
				playlist = new int[SoundPlayer.MUSIC_COUNT];
				ArrayList<Integer> music = new ArrayList<Integer>();
				for(int i = 1; i <= SoundPlayer.MUSIC_COUNT; i++) {
					music.add(i);
				}
				
				for(int i = 0; i < SoundPlayer.MUSIC_COUNT; i++) {
					int randomTrack = (int) (Math.random() * music.size());
					playlist[i] = music.get(randomTrack);
					music.remove(randomTrack);
				}
			}
			
			File file = new File(MUSIC_DIRECTORY + "music" + (playlist[track]) + ".mid");
			System.out.println("Spiele Track " + this.track + ": " + file.getName());
			Sequence sequence = MidiSystem.getSequence(file);
			System.out.println("Länge: " + (int) (sequence.getMicrosecondLength() / 1000000 / 60) + "min " + ((int) (sequence.getMicrosecondLength() / 1000000) - ((int) (sequence.getMicrosecondLength() / 1000000 / 60)) * 60) + "s");
			
			sequencer = MidiSystem.getSequencer();
			sequencer.setTempoFactor(this.tempoFactor);
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.addMetaEventListener(this);
			
			sequencer.start();
		} catch(Exception e) {
			System.out.println("Fehler bei der Midi");
			e.printStackTrace();
		}
	}
	
	public void stopMusic() {
		sequencer.stop();
		sequencer.close();
	}
	
	public void meta(MetaMessage event) {
		if(event.getType() == 47) {
			this.track++;
			this.startMusic();
		}
	}
}
