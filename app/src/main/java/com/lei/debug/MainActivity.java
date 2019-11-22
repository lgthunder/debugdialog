package com.lei.debug;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lei.core.DebugCore;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DebugCore.addHandler("test", () -> Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show());

    }
}
