package com.example.evelab;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MultimediaActivity extends AppCompatActivity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri;
    int position = 0;
    MediaPlayer mediaPlayer;
    VideoView videoView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multimedia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void audioOn(View view) {
        try {
            Uri audioUri = Uri.parse("android.resource://"
                    + getPackageName() + "/"
                    + R.raw.abba_fernando);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), audioUri);
            mediaPlayer.prepare(); // for buffering, may take long
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }
    public void audioPause(View view) {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying()) {
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void audioResume(View view) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }
    public void audioOff(View view) {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            position = 0;
        }
    }
    public void videoOn(View view) {
        videoView = (VideoView) findViewById(R.id.videoView);
// Create a progress bar for Video player and show it
        progressDialog = new ProgressDialog(this); // progress bar
        progressDialog.setMessage("Buffering...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {
// Start the MediaController
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoView);
            videoView.setMediaController(mediacontroller);
// set the path to video file
            videoView.setVideoPath(
                    "android.resource://" + getPackageName() + "/"
                            + R.raw.abba_fernando);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                videoView.start();
            }
        });
    }
    public void videoOff(View view) {
        if (videoView == null)
            return;
        if (videoView.isPlaying()){
            videoView.stopPlayback();
            videoView = null;
        }

        if (progressDialog.isShowing()){
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}