package com.awesomeproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;

public class VideoLiveWallpaper extends WallpaperService {


    class GLWallpaperEngine extends Engine {
        private final Context context;
        MediaPlayer mediaPlayer = new MediaPlayer();
        String videoPath ;

        GLWallpaperEngine(@NonNull final Context context) {
            this.context = context;
            setTouchEventsEnabled(false);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            SharedPreferences sh = getSharedPreferences("pref", Context.MODE_PRIVATE);
            videoPath = sh.getString("path", "");

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            mediaPlayer.setSurface(surfaceHolder.getSurface());
            try {
                mediaPlayer.setDataSource(videoPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.setLooping(true);
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mediaPlayer.start();

            }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mediaPlayer.start();
            } else {
                mediaPlayer.pause();
            }
        }


        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            mediaPlayer.release();
            mediaPlayer = null;

        }

    }

    @Override
    public Engine onCreateEngine() {
        return new GLWallpaperEngine(this);
    }



}


