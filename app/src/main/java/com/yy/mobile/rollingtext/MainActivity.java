package com.yy.mobile.rollingtext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.Direction;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.Strategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private List<String> list = Arrays.asList("1", "9", "12", "19", "24", "36", "47",
            "56", "63", "78", "89", "95", "132", "289", "312", "400");
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RollingTextView sameDiretionView = findViewById(R.id.rollingTextView);
        sameDiretionView.addCharOrder(CharOrder.Number);
        sameDiretionView.setAnimationDuration(700L);
        sameDiretionView.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sameDiretionView.setText(list.get(idx++ % list.size()));
            }
        }, 2000L, 3000L);


        final RollingTextView alphaBetView = findViewById(R.id.alphaBetView);
        alphaBetView.setAnimationDuration(2000L);
        alphaBetView.setCharStrategy(Strategy.NormalAnimation);
        alphaBetView.addCharOrder(CharOrder.Alphabet);
        alphaBetView.setText("bdz");


        final RollingTextView timeView = findViewById(R.id.timeView);
        timeView.setAnimationDuration(300);
        @SuppressLint("SimpleDateFormat") final DateFormat format = new SimpleDateFormat("HH:mm:ss");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timeView.setText(format.format(new Date()));
            }
        }, 0, 1000L);


        final RollingTextView carryView = findViewById(R.id.carryTextView);
        carryView.setAnimationDuration(13000L);
        carryView.addCharOrder(CharOrder.Number);
        carryView.setCharStrategy(Strategy.CarryBitAnimation);
        carryView.setText("0");
        carryView.setText("1290");
    }
}
