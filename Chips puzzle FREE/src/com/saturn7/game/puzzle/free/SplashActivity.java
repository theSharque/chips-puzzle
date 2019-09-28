package com.saturn7.game.puzzle.free;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.splash );
		
		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					sleep( 5000 );
				} catch( InterruptedException e ) {
					// do nothing
				} finally {
					finish();
					Intent i = new Intent();
					i.setClassName( "com.saturn7.game.puzzle.free",
							"com.saturn7.game.puzzle.free.PuzzleFREEActivity" );
					startActivity( i );
					stop();
				}
			}
		};
		splashTread.start();
	}
}
