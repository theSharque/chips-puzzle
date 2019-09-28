package com.saturn7.game.puzzle.free;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.game.puzzle.IActivityRequestHandler;
import com.game.puzzle.MainGame;

public class PuzzleFREEActivity extends AndroidApplication implements IActivityRequestHandler {

    protected AdView adView;

    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;
    private static final String AD_UNIT_ID = "ca-app-pub-8928685503148444/2125102534";

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_ADS:
                    adView.setVisibility(View.VISIBLE);
                    break;

                case HIDE_ADS:
                    adView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the layout
        RelativeLayout layout = new RelativeLayout(this);

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // Create the libgdx View
        View gameView = initializeForView(new MainGame(this), true);

        // Create and setup the AdMob view
        adView = new AdView(this); // Put in your secret key here
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        AdRequest newAdReq = new AdRequest.Builder()
                .build();
        adView.loadAd(newAdReq);

        // Add the libgdx view
        layout.addView(gameView);

        // Add the AdMob view
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layout.addView(adView, adParams);

        // Hook it all up
        setContentView(layout);
    }

    @Override
    public void showAds(boolean show) {
        handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }
}