package xyz.imxqd.twoer.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioPlayer {

    private static final String TAG = "AudioPlayer";

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static boolean playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }


        try {
            File file = new File(filePath);
            if (file.exists()) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                return true;
            } else {
                return false;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            release();
            return false;
        } catch (SecurityException e) {
            e.printStackTrace();
            release();
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            release();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            release();
            return false;
        }
    }

    public static boolean playSound(Uri fileUri, MediaPlayer.OnCompletionListener onCompletionListener) {
        Log.d(TAG, "playSound: " + fileUri);
        String path = fileUri.getPath();
        return playSound(path, onCompletionListener);
    }

    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
