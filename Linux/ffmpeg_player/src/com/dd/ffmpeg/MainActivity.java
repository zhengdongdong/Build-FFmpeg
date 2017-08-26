package com.dd.ffmpeg;

import java.io.File;
import java.util.Random;

import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivity extends Activity {

	private Spinner sss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sss = (Spinner) findViewById(R.id.sss);
		String[] names = { "girls.mp4", "Titanic.mkv", "m.flv" };
		sss.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));
		
	}

	public void mPlay(View btn) {
		final String input = new File(Environment.getExternalStorageDirectory(), sss.getSelectedItem().toString())
				.getAbsolutePath();
		Intent intent = new Intent(this, PlayerActivity.class);
		intent.putExtra("input", input);
		startActivity(intent);

	}
}
