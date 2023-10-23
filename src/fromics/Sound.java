package fromics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//a class used to manage sounds within a program
public class Sound {
	//the Map of sound names to sounds
	private static Map<String, File> sounds;
	
	//initializes the Sound class so it can be used
	public static void init() {
		sounds = new HashMap<>();
	}
	
	//adds a sound to the Sound class with the given sound file name and the name of the sound
	public static void add(String filename, String soundName) {
		File f = new File(filename);
		sounds.put(soundName, f);
	}
	
	//plays the sound with the given name
	public static void play(String soundName) {
		new Thread(() -> {
			File f = sounds.get(soundName);
			AudioInputStream audioIn;
			try {
				audioIn = AudioSystem.getAudioInputStream(f);
			} catch (UnsupportedAudioFileException e) {
				audioIn = null;
				e.printStackTrace();
			} catch (IOException e) {
				audioIn = null;
				e.printStackTrace();
			}
			Clip clip;
			try {
				clip = AudioSystem.getClip();
				clip.open(audioIn);
			} catch (LineUnavailableException e) {
				clip = null;
				e.printStackTrace();
			} catch (IOException e) {
				clip = null;
				e.printStackTrace();
			}
			clip.start();
		}).start();
	}
	
	//starts playing a lop of the sound with the given name
	public static void loop(String soundName) {
		new Thread(() -> {
			File f = sounds.get(soundName);
			AudioInputStream audioIn;
			try {
				audioIn = AudioSystem.getAudioInputStream(f);
			} catch (UnsupportedAudioFileException e) {
				audioIn = null;
				e.printStackTrace();
			} catch (IOException e) {
				audioIn = null;
				e.printStackTrace();
			}
			Clip clip;
			try {
				clip = AudioSystem.getClip();
				clip.open(audioIn);
			} catch (LineUnavailableException e) {
				clip = null;
				e.printStackTrace();
			} catch (IOException e) {
				clip = null;
				e.printStackTrace();
			}
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}).start();
	}
	
	//returns whether or not there is a sound with the given name
	public static boolean containsSound(String soundName) {
		return sounds.containsKey(soundName);
	}
}