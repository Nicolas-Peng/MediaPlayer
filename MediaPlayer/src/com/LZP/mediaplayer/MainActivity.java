package com.LZP.mediaplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.R.integer;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	// private MediaPlayer mPlayer = null;
	// private Button btn1;
	private ImageButton imgBtn1,next,past;
	private Button exit;
	private TextView mTime;
	private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	ListView listView;
	// private TextView duration;
	private int duration=0;
	private Handler mHandler;
	private SimpleMusicPlayerService smpService;
	// 进度条
	private SeekBar seekBar;
	private static final int MAX_PROGRESS = 100;
	private int current_progress = 10;
	private static final int PRO = 1;
	private int currPosition = 0;
	 private String currPath = "/storage/sdcard/01.mp3";
	// 进度条end
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			// TODO Auto-generated method stubs
			// 通过Binder获取Service对象 保持通讯
			Log.w("lzh", "caca");
			smpService = ((SimpleMusicPlayerService.SMPlayerBinder) iBinder)
					.getService();
			//if(smpService.mPlayer!=null)
			//duration = smpService.Duration() / 1000;
			Log.w("lzh", Integer.valueOf(duration).toString());
			Log.w("seekbar_Progress",Integer.valueOf(seekBar.getProgress()).toString());
			if(smpService.mPlayer.isPlaying())
				imgBtn1.setImageResource(R.drawable.stopmusic);
			else
				imgBtn1.setImageResource(R.drawable.playmusic);
			smpService.sayHelloWorld();
			// updateByStatus();
		}

		private void updateByStatus() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			// smpService = null;
			Log.d("Serviceeeeeeee", "ssssssss");
		}
	};

	@Override
	protected void onDestroy() {
		// onPlayerExit();
		unbindService(sc);
		super.onDestroy();
	}

	// 播放按钮的点击处理
	OnClickListener mListenPlay = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			smpService.PlayOrPause(currPath);
			seekBar.setMax(smpService.Duration() / 1000);

			//smpService.onPlayerPlay();
			if (smpService.mPlayer.isPlaying())
				imgBtn1.setImageResource(R.drawable.stopmusic);
			else
				imgBtn1.setImageResource(R.drawable.playmusic);

		}
	};
	// 停止按钮的点击处理
	OnClickListener mListenStop = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			smpService.onPlayerStop();
		}
	};
	// 退出按钮的点击处理
	OnClickListener mListenExit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,SimpleMusicPlayerService.class);
			stopService(intent);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 关闭Activity时不终止Service
		Intent indent = new Intent(this, SimpleMusicPlayerService.class);
		startService(indent);

		// 调用bindService绑定服务
		Intent bindent = new Intent(this, SimpleMusicPlayerService.class);
		bindService(bindent, sc, BIND_AUTO_CREATE);
		Log.d("1111", "111111111");
		/*
		 * if (smpService == null){ Log.w("lzp", "service"); } else { duration =
		 * smpService.Duration()/1000; }
		 */
		Log.d("tttt", "ttttt");
		imgBtn1 = (ImageButton) findViewById(R.id.imageButton1);
		next=(ImageButton)findViewById(R.id.next);
		past=(ImageButton)findViewById(R.id.past);
		exit=(Button)findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(MainActivity.this,SimpleMusicPlayerService.class);
				stopService(i);
				finish();
			}
		});
		// btn1=(Button)findViewById(R.id.button1);
		// btn1.setOnClickListener(mListenPlay);
		imgBtn1.setOnClickListener(mListenPlay);
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currPosition==mDataList.size()-1)
					currPosition=0;
				else 
					currPosition++;
				currPath = mDataList.get(currPosition).get("path").toString();
				smpService.PlayOrPause(currPath);
				seekBar.setMax(smpService.Duration()/1000);
				seekBar.setProgress(0);
			}
		});
		
		past.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(currPosition==0)
					currPosition=mDataList.size()-1;
				else 
					currPosition--;
				currPath=mDataList.get(currPosition).get("path").toString();
				smpService.PlayOrPause(currPath);
				seekBar.setMax(smpService.Duration()/1000);
				seekBar.setProgress(0);
			}
		});
		
		mTime = (TextView) findViewById(R.id.time);
		// duration=(TextView)findViewById(R.id.duration);
		// duration = mPlayer.getDuration() / 1000;
		listView = (ListView) findViewById(R.id.listView1);
		/*
		setData();
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, mDataList,// 数据源
				R.layout.music_list,// listview中item的布局文件
				new String[] { "name", "image" },// list中数据的key
				new int[] { R.id.name, R.id.image });// item.xml中各控件的id

		listView.setAdapter(listItemAdapter);
		*/		
		// 进度条
		seekBar = (SeekBar) this.findViewById(R.id.seekBar1);
		Log.d("Duration", Integer.valueOf(duration).toString());
		// seekBar.setMax(duration); // duration是秒
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				/*
				 * smpService.mPlayer.seekTo(1000 * seekBar.getProgress());
				 * Log.d("SeekBAR", "AAAAAAA");
				 */
				smpService.seekto(seekBar.getProgress() * 1000);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				/*
				 * int process = seekBar.getProgress();
				 * 
				 * if (mPlayer != null) { mPlayer.seekTo(1000 * process);
				 * System.out.println(process);
				 * 
				 * }
				 */

				System.out.println(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
		Log.d("22222", "222222222");
		
		generateListView();
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 123:
					Integer s=0;
					if(smpService.state==STATE.PLAYING ){
					s = smpService.mPlayer.getCurrentPosition();
					s = s / 1000;
					//Log.w("ssssssss", Integer.valueOf(s).toString());
					mTime.setText(s.toString() + "秒");
					}
					
					break;
				case PRO:
					if (current_progress >= smpService.Duration()/1000) {
					} else {
						// seekBar.setProgress(current_progress);
						current_progress += 1;
						seekBar.incrementProgressBy(1);
						Log.d("lzp", Integer.valueOf(seekBar.getProgress())
								.toString());
						Log.d("lzp", "seekBar+++++");
						// mHandler.sendEmptyMessageDelayed(PRO, 10000);
						break;
					}
				}
			}
		};
		// current_progress = current_progress > 0 ? current_progress : 0;
		// seekBar.setProgress(current_progress);
		// mHandler2.sendEmptyMessage(PRO);
		Thread mThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.obtainMessage(123).sendToTarget();
					//if (smpService.mPlayer.isPlaying())
					mHandler.obtainMessage(PRO).sendToTarget();
					Log.d("跳动", "Seekbar");
				}
			}
		};
		mThread.start();
		Log.d("enddd", "enddd");
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void findAll(String path,List<File> myList){
		File myFile = new File(path);
		File[] subFiles = myFile.listFiles();
		if(subFiles!=null)
			for(File subFile:subFiles)
				if(subFile.isFile()&&subFile.getName().endsWith(".mp3"))
					myList.add(subFile);
	}
	
	private void generateListView(){
		List<File> myList = new ArrayList<File>();
		int count = 1;
		findAll("/storage/sdcard", myList);
		//按字符排序
		Collections.sort(myList);
		for(File file:myList){
			Map<String, Object> map=new HashMap<String,Object>();
			map.put("name", count+"_"+file.getName());
			map.put("path", file.getAbsolutePath());
			mDataList.add(map);
			count++;
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this,mDataList,
				R.layout.music_list,
				new String[]{"name"},new int[]{R.id.name});
		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				currPosition = position;
				String path = mDataList.get(currPosition).get("path").toString();
				currPath=path;
				smpService.PlayOrPause(currPath);
				if (smpService.mPlayer.isPlaying())
					imgBtn1.setImageResource(R.drawable.stopmusic);
				else
					imgBtn1.setImageResource(R.drawable.playmusic);
				seekBar.setMax(smpService.Duration()/1000);
				seekBar.setProgress(0);
			}
		});
	}
	/*
	private void setData() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "1、My Heart Will Go On.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "2、Ailee - 손대지마.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "3、Lenka-The Show.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "4、后会无期.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "5、喜欢你.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "6、小苹果.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "7、尹钟信 Swings - 회색도시.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);

		map = new HashMap<String, Object>();
		map.put("name", "8、昭宥 权顺日 朴容仁 - 틈.mp3");
		map.put("image", R.drawable.music_icon);
		mDataList.add(map);
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
