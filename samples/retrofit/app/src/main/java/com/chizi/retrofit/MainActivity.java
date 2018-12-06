package com.chizi.retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mTestButton;
    private TextView mContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
    }

    private void initViews() {
        mTestButton = findViewById(R.id.btnTest);
        mContentTextView = findViewById(R.id.tvContent);
    }

    private void initListeners() {
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepos();
            }
        });
    }

    private void getRepos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<RepoBean> repoList = GithubService.newInstance().getRepos("chizidegit");
                    setResultData(repoList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setResultData(final List<RepoBean> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContentTextView.setText(result.toString());
            }
        });
    }
}
