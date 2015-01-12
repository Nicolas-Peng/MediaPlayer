package com.LZP.mediaplayer;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.NetworkInfo.State;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

enum STATE{
	IDLE,STOP,PLAYING,PAUSE
}

public class SimpleMusicPlayerService extends Service {
	private IBinder binder=new SMPlayerBinder();
	public MediaPlayer mPlayer = new MediaPlayer();
	String currPath = "";
	
	STATE state = STATE.IDLE;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("IBinder","lzp");
		return binder;
	}
	
	public void sayHelloWorld(){  
        Toast.makeText(this.getApplicationContext(), "Hello World Local Service!", Toast.LENGTH_SHORT).show();  
    }    
	
	public class SMPlayerBinder extends Binder{
		SimpleMusicPlayerService getService(){
			return SimpleMusicPlayerService.this;
			
		}
	}
	@Override
	public void onCreate(){
		//initPlayer() ;
	}
	
	public void seekto(int i){
		mPlayer.seekTo(i);
	}
	/*
	public void initPlayer() {
		// 创建对象
		mPlayer = new MediaPlayer();
		// 初始化
		try {
			mPlayer.setDataSource("/storage/sdcard/01.mp3");
			mPlayer.prepare();
			/*
			 * //得到音乐时长 int size=mPlayer.getDuration(); String timelong =
			 * size/1000 +"s"; duration.setText(timelong);
			 */
			//duration = mPlayer.getDuration() / 1000;
/*
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	*/
	public int Duration(){
		if (mPlayer == null){
			Log.w("lzp", "mplayer");
			return 0;
		}
		else
			return mPlayer.getDuration();
	}
	
	public void PlayOrPause(String filePath){
		if(currPath!=filePath){
			if(currPath!=""){
				mPlayer.reset();//mPlayer.seekTo(0);
				state=STATE.IDLE;
			}
			currPath = filePath;
		}
		if(state==STATE.IDLE){
			try{
				mPlayer.setDataSource(filePath);
				mPlayer.prepare();
				mPlayer.start();
				state=STATE.PLAYING;
			}catch(IOException e){
			}
		}else if(state==STATE.PAUSE){
			mPlayer.start();
			state=STATE.PLAYING;
		}else{
			mPlayer.pause();
			state=STATE.PAUSE;
		}
	}
	
	
	// 点击播放按钮时的操作
		public void onPlayerPlay() {
			// 开始播放，暂停：
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				// btn1.setText("播放");
				//imgBtn1.setImageResource(R.drawable.playmusic);
				// imgBtn1.setBackgroundResource(R.drawable.playmusic);

			} else {
				mPlayer.start();
				// btn1.setText("暂停");
				//imgBtn1.setImageResource(R.drawable.stopmusic);
				// imgBtn1.setBackgroundResource(R.drawable.stopmusic);
			}
		}
		
		public void onPlayerStop() {
			// 停止
			mPlayer.stop();
			try {
				mPlayer.prepare();// 停止后必须重新就绪，否则无法再次开始
			} catch (IOException e) {
			}
		}

		public void onPlayerExit() {
			mPlayer.release();
		}
		
	    @Override
	    public void onDestroy(){
	    	mPlayer.release();
	    	super.onDestroy();
	    }
}
