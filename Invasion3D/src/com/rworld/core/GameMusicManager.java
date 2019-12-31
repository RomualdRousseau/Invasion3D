package com.rworld.core;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class GameMusicManager implements IDisposable {
	
	public GameMusicManager(GameActivity activity) {
		_soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		_audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		_mediaPlayer = new MediaPlayer();
	}
	
	public void dispose() {
		_mediaPlayer.stop();
		_mediaPlayer.release();
		_mediaPlayer = null;
		_soundPool.release();
		_soundPool = null;
	}

	public void loadMusic(AssetFileDescriptor fd) throws IllegalArgumentException, IllegalStateException, IOException {
		_mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
		_mediaPlayer.prepare();
		_mediaPlayer.setLooping(true);
		_mediaPlayer.start();
	}
	
	public void playMusic() {
		_mediaPlayer.start();
	}
	
	public void stopMusic() {
		_mediaPlayer.stop();
	}
	
	public void pauseMusic() {
		_mediaPlayer.pause();
	}
	
	public int loadSound(AssetFileDescriptor fd) {
		return _soundPool.load(fd, 1);
	}
	
	public void playSound(int soundId) {
		int volume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		_soundPool.play(soundId, volume, volume, 1, 0, 1);
	}
	
	private SoundPool _soundPool;
	private AudioManager _audioManager;
	private MediaPlayer _mediaPlayer;
}
