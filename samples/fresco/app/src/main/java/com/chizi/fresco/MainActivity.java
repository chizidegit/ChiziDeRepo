package com.chizi.fresco;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {

    private SimpleDraweeView mImageView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
    }

    private void initViews() {
        mImageView = findViewById(R.id.image);
        mButton = findViewById(R.id.button);
    }

    private void initListeners() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.baidu.com/img/bd_logo1.png?where=super");
                mImageView.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
                mImageView.setImageURI(uri);
            }
        });
    }

}
