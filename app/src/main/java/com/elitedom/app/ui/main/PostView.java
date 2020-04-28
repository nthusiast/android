package com.elitedom.app.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elitedom.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class PostView extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private TextView mPostTitle;
    private ImageView mPostImage;
    private FloatingActionButton mChatRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        relativeLayout = findViewById(R.id.single_card);
        mPostTitle = findViewById(R.id.title);
        mPostImage = findViewById(R.id.postImage);
        mChatRoom = findViewById(R.id.fab);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        Intent intent = getIntent();
        String message = intent.getStringExtra("title");
        mPostTitle.setText(message);
        mPostImage.setClipToOutline(true);
        mChatRoom.animate().alpha(1.0f);
    }
}
