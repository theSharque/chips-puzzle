package com.saturn7.chipspuzzle.audio;

import android.content.Context;
import android.media.MediaPlayer;

import com.saturn7.chipspuzzle.R;

public class ScratchPlayer {
    private MediaPlayer player;
    private boolean playing;

    public ScratchPlayer(Context context) {
        player = MediaPlayer.create(context, R.raw.scratch);
        if (player != null) {
            player.setLooping(true);
            player.setVolume(0.25f, 0.25f);
        }
    }

    public void setSliding(boolean sliding) {
        if (player == null) {
            return;
        }
        if (sliding && !playing) {
            player.start();
            playing = true;
        } else if (!sliding && playing) {
            player.pause();
            player.seekTo(0);
            playing = false;
        }
    }

    public void stop() {
        setSliding(false);
    }

    public void release() {
        stop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
