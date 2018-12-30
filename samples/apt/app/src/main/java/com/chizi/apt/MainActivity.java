package com.chizi.apt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.chizi.annotation.DIActivity;
import com.chizi.annotation.DIView;

@DIActivity
public class MainActivity extends AppCompatActivity {

    @DIView(R.id.text)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DIMainActivity.bindView(this);
        mTextView.setText("Hello apt");
    }
}
