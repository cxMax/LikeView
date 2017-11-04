package com.cxmax.likeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cxmax.widget.NumberView;

public class MainActivity extends AppCompatActivity {

    private NumberView numberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberView = findViewById(R.id.number);

        numberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberView.decrease();
            }
        });
    }
}
