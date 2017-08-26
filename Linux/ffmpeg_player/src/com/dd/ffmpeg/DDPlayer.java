package com.dd.ffmpeg;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.Surface;

public class DDPlayer {
	
	public native void play(String input,Surface surface);
	
	public native void stop();
	
	
	public AudioTrack createAudioTrack(int sampleRateInHz, int nb_channels){
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		Log.i("dd", "nb_channels:"+nb_channels);
		int channelConfig;
		if(nb_channels == 1){
			channelConfig = android.media.AudioFormat.CHANNEL_OUT_MONO;
		}else if(nb_channels == 2){
			channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
		}else{
			channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
		}
		
		int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		
		AudioTrack audioTrack = new AudioTrack(
				AudioManager.STREAM_MUSIC, 
				sampleRateInHz, channelConfig, 
				audioFormat, 
				bufferSizeInBytes, AudioTrack.MODE_STREAM);
		
		//audioTrack.play();
		//write to PCM
		//audioTrack.write(audioData, offsetInBytes, sizeInBytes);
		return audioTrack;
	}
	
	static{
		System.loadLibrary("avutil-54");
		System.loadLibrary("swresample-1");
		System.loadLibrary("avcodec-56");
		System.loadLibrary("avformat-56");
		System.loadLibrary("swscale-3");
		System.loadLibrary("postproc-53");
		System.loadLibrary("avfilter-5");
		System.loadLibrary("avdevice-56");
		System.loadLibrary("myffmpeg");
	}
}
