package com.demo.lizejun.libcommon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lib.common.util.SPUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SPUtils.getInstance().doRealWork("aa", new SPUtils.SpEditor.Builder().clear().build());
    }
}
