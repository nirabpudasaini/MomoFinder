package com.nirab.momofinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class Splash extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(5000);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				finally{
					Intent openMain = new Intent(Splash.this, Map.class);
					startActivity(openMain);
				}
			}
			
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_splash, menu);
		return true;
	}

}
