package com.example.narceous;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import android.app.Service;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MP_service extends Service {
	  private static final String LOG_TAG = "Media Player Service";
	  private final IBinder mBinder = new MyBinder();
	  MediaPlayer mp = new MediaPlayer();
	  
	  private ArrayList<String> mplaylist;
	  private int playlist_index = 0;
	  
	  String mTitle = "";
	  String mArtist = "";
	  
	  public enum State {
		 Playing, 
		 Paused,
		 Stopped,
		 Preparing
	  }
	  
	  private State mState = State.Stopped;
	  
	  public static final String file_url = "file_url";
	  @Override 
	  public void onCreate() {
	  
	
	  }
  
	  @Override
	  public IBinder onBind(Intent arg0) {
		  return mBinder;
	  }
  
	  public class MyBinder extends Binder {
		  MP_service getService() {
			  return MP_service.this;
		  }
	  }	  

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
		  return START_NOT_STICKY;
		  
	  } 
	  
	  public int Load_Playlist (ArrayList<String> playlist_urls) {
		  
		  mplaylist  = playlist_urls;
		  reset_song();
	      return 0;
	  }
	  
	  public int Play() {
		  	
		  if(mState != State.Stopped) {
	        try {
	        	mp.start();
	        	mState = State.Playing;
	        }	catch (IllegalStateException e) {
	            Log.e(LOG_TAG, "Play() failed");
	            return 1;
	        }
		  }
		  return 0;
	  }
	  
	  public int Pause() {
		  	
		  if(mState == State.Playing) {
	        try {
	        	if (mp.isPlaying()){
	        		mp.pause();
	        		mState = State.Paused;
	        	} else {
	        		return 1;
	        	}
	        }	catch (IllegalStateException e) {
	            Log.e(LOG_TAG, "Pause() failed");
	            return 1;
	        }
		  }
		  
		  return 0;
	  }
	  
	  public int Next(){
		  playlist_index++;
		  if (playlist_index >= mplaylist.size()){
			  playlist_index = 0;
		  }
		  reset_song();
		  return 0;
	  }
	  
	  public int Previous(){
		  playlist_index--;
		  if (playlist_index < 0){
			  playlist_index = mplaylist.size() -1;
		  }
		  reset_song();
		  return 0;
	  }
	  
	  private int reset_song(){
		  State prev_state = mState;
		  if (mState != State.Stopped) {
			  mp.stop();
			  mp.reset();
		  }
		  

		  
	      try {
	    	  String mURL = mplaylist.get(playlist_index);
	          mp.setDataSource(mURL);
	          
			  MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
			  metaRetriever.setDataSource(mURL,new HashMap<String, String>());
			  mArtist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			  mTitle = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			  
	      } catch (IOException e) {
	          Log.e(LOG_TAG, "Set url failed");
	          return 1;
	      }
	      
	      try {
	           mp.prepare();
	           mState = State.Preparing;
	      } catch (IOException e) {
	           Log.e(LOG_TAG, "prepare() failed");
	           return 1;
	      }
		  
	      if (prev_state == State.Playing) {
	    	  mState = State.Playing;
	    	  Play();
	      }
	      
		  return 0;
	  }
	  
	  public State get_state() {
		  return mState;
	  }
	  
	  public String get_song_title() {
		  if (mTitle == null) {
			  mTitle = mplaylist.get(playlist_index);
			  String filename_pattern = ".*/(.*)\\.mp3";
			  mTitle = mTitle.replaceAll(filename_pattern, "$1");
		  }
		  
		  return mTitle;
	  }
	  
	  public String get_artist() {
		  return mArtist;
	  }
	  
}


