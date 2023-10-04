package com.example.mapped;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class FullScreenVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        VideoView fullScreenVideoView = (VideoView) findViewById(R.id.vv_fullScreenVideo);
        Intent callingActivityIntent = getIntent();
        if(callingActivityIntent != null) {
            Uri videoUri = callingActivityIntent.getData();
            fullScreenVideoView.setVideoURI(videoUri);
            }

        fullScreenVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreenVideoView.start();
            }
        });

    }
}