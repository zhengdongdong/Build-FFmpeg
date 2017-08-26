package com.dd.ffmpeg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class PlayerActivity extends Activity {

	private DDPlayer player;
	private VideoView videoView;
	private Thread thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_palyer);
		
		Intent intent = getIntent();
		final String input = intent.getStringExtra("input");
		if(input == null || input.length()<=0){
			Toast.makeText(this, "播放路径为空", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		videoView = (VideoView) findViewById(R.id.video_view);
		player = new DDPlayer();

		videoView.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// Surface传入到Native函数中，用于绘制
						player.play(input, videoView.getHolder().getSurface());
					}
				});
				thread.start();
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		player.stop();
	}
}
