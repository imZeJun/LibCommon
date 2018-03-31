package com.demo.lizejun.libcommon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lib.common.worker.SimpleWorker;
import com.lib.common.worker.SpWorker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpWorker.getInstance().applyBatch("aa",
                new SpWorker.SpEditor.Builder()
                        .putBoolean("aaa", false)
                        .clear().build());
    }
}
