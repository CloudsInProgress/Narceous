package com.example.narceous;

import java.util.ArrayList;
import java.util.List;

import com.example.narceous.MP_service;
import com.example.narceous.R;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.view.Menu;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	private MP_service mp_service_obj;
	private MP_service.State MP_state ;
	ArrayList<String> Voice_in_matches;
	static List<ResolveInfo> systemActivities=null;
	private ListView mList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button play_button = (Button) findViewById(R.id.play_button);
		Button prev_button = (Button) findViewById(R.id.prev_button);
		Button next_button = (Button) findViewById(R.id.next_button);
		Button load_button = (Button) findViewById(R.id.load_button);
		
		Intent command = new Intent(MainActivity.this, MP_service.class);	
		startService(command);
		
		prev_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) throws  NumberFormatException {
				mp_service_obj.Previous();
				Toast.makeText(MainActivity.this, "Previous", Toast.LENGTH_SHORT).show();
				Update_info();
			}
		});	
		next_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) throws  NumberFormatException {
				mp_service_obj.Next();
				Toast.makeText(MainActivity.this, "Next", Toast.LENGTH_SHORT).show();
				Update_info();
			}	
		});
		play_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) throws  NumberFormatException {
				MP_state = mp_service_obj.get_state();
				if(MP_state != MP_service.State.Playing) {
					mp_service_obj.Play();
				    play_button.setText(R.string.pause_button);
				    Toast.makeText(MainActivity.this, "Playing", Toast.LENGTH_SHORT).show();
				} else {
					mp_service_obj.Pause();
					play_button.setText(R.string.play_button);
					Toast.makeText(MainActivity.this, "Pausing", Toast.LENGTH_SHORT).show();
				}					
			}	
		});
		
		load_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) throws  NumberFormatException {
				startVoiceRecognitionActivity();
				Toast.makeText(MainActivity.this, "Loading songs", Toast.LENGTH_SHORT).show();
				
				ArrayList<String> mplaylist = new ArrayList<String>();
				
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/Bach-Air-on-a-G-String.mp3");
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/Beethoven-Moonlight-Sonata-Mvt.-1.mp3");
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/CLAUDE-DEBUSSY-CLAIRE-DE-LUNE.mp3");
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/Erik-Satie-Gymnopédie-No.1.mp3");
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/Saint-Saëns_-Aquarium.mp3");
				mplaylist.add("http://private.xin-jin.net/wp-content/uploads/2013/07/Sissel-O-Mio-Babbino-Caro.mp3");
				
				mp_service_obj.Load_Playlist(mplaylist);
				Update_info();
			}	
		});
		
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, IBinder binder) {
	    mp_service_obj = ((MP_service.MyBinder) binder).getService();
	    Toast.makeText(MainActivity.this, "Connected to MP service", Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	mp_service_obj = null;
	    }
	};
	
	private void Update_info(){
		
		TextView titleLabel = (TextView)findViewById( R.id.Song_title);
		titleLabel.setText(mp_service_obj.get_song_title());
		titleLabel.setSelected(true); 
		
	}
	
	@Override
	protected void onResume() {
	   super.onResume();
	   bindService(new Intent(this, MP_service.class), mConnection,
	       Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	void startVoiceRecognitionActivity(){
	    if(systemActivities==null){
	        PackageManager pm = getPackageManager();
	        systemActivities = pm.queryIntentActivities(
	                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	    }
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Missive(c) Speech Input");
	    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	        // Fill the list view with the strings the recognizer thought it could have heard
	    	Voice_in_matches = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);

	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}

}
