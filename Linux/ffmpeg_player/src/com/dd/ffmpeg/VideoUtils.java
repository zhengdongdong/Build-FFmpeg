package com.dd.ffmpeg;

public class VideoUtils {

	public native static void decode(String input, String output);
	
	static{
		// 注意顺序
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
