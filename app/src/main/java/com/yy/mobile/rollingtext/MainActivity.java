package com.yy.mobile.rollingtext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.strategy.Direction;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    private List<String> list = Arrays.asList("1", "9", "12", "19", "24", "36", "47",
            "56", "63", "78", "89", "95", "132", "289", "312", "400");
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RollingTextView normal = findViewById(R.id.rollingTextView);
        normal.addCharOrder(CharOrder.Number);
        normal.setAnimationDuration(2000L);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                normal.setText(list.get(idx % list.size()));
                handler.postDelayed(this, 3000L);
            }
        }, 2000L);

        final RollingTextView sameDirection = findViewById(R.id.rollingTextView2);
        sameDirection.addCharOrder(CharOrder.Number);
        sameDirection.setAnimationDuration(2000L);
        sameDirection.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sameDirection.setText(list.get(idx % list.size()));
                handler.postDelayed(this, 3000L);
            }
        }, 2000L);

        final RollingTextView carryBit = findViewById(R.id.rollingTextView3);
        carryBit.addCharOrder(CharOrder.Number);
        carryBit.setAnimationDuration(2000L);
        carryBit.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_UP));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                carryBit.setText(list.get(idx++ % list.size()));
                handler.postDelayed(this, 3000L);
            }
        }, 2000L);

        final RollingTextView countDown = findViewById(R.id.rollingTextView4);
        countDown.addCharOrder(CharOrder.Number);
        countDown.addCharOrder(":");
        countDown.setAnimationDuration(500L);
        countDown.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
        countDown.setCharStrategy(Strategy.TimeCountDownAnimation(Direction.SCROLL_DOWN));
        handler.post(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                date.setTime(System.currentTimeMillis());
                countDown.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date));
                handler.postDelayed(this, 1000L);
            }
        });

        final RollingTextView stickyText = findViewById(R.id.stickyText);
        stickyText.setAnimationDuration(3000L);
        stickyText.addCharOrder("0123456789abcdef");
        stickyText.setCharStrategy(Strategy.StickyAnimation(0.9f));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stickyText.setText("eeee");
            }
        }, 2000L);

        final RollingTextView stickyText2 = findViewById(R.id.stickyText2);
        stickyText2.setAnimationDuration(3000L);
        stickyText2.addCharOrder("0123456789abcdef");
        stickyText2.setCharStrategy(Strategy.StickyAnimation(0.2f));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stickyText2.setText("eeee\naaaaa");
            }
        }, 2000L);


        final RollingTextView alphaBetView = findViewById(R.id.alphaBetView);
        alphaBetView.setAnimationDuration(2000L);
        alphaBetView.setCharStrategy(Strategy.NormalAnimation());
        alphaBetView.addCharOrder(CharOrder.Alphabet);
        alphaBetView.addCharOrder(CharOrder.UpperAlphabet);
        alphaBetView.addCharOrder(CharOrder.Number);
        alphaBetView.addCharOrder(CharOrder.Hex);
        alphaBetView.addCharOrder(CharOrder.Binary);
        alphaBetView.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
        alphaBetView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //finsih
            }
        });
        alphaBetView.setText("i am a text");


        final RollingTextView timeView = findViewById(R.id.timeView);
        timeView.setAnimationDuration(300);
        @SuppressLint("SimpleDateFormat") final DateFormat format = new SimpleDateFormat("HH:mm:ss");
        handler.post(new Runnable() {
            @Override
            public void run() {
                timeView.setText(format.format(new Date()));
                handler.postDelayed(this, 1000L);
            }
        });

        final RollingTextView carryView = findViewById(R.id.carryTextView);
        carryView.setAnimationDuration(13000L);
        carryView.addCharOrder(CharOrder.Number);
        carryView.setCharStrategy(Strategy.CarryBitAnimation(Direction.SCROLL_DOWN));
        carryView.setText("0");
        carryView.setText("1290");

        final RollingTextView charOrder1 = findViewById(R.id.charOrderExample1);
        charOrder1.setAnimationDuration(4000L);
        charOrder1.addCharOrder("abcdefg");
        charOrder1.setText("a");

        final RollingTextView charOrder2 = findViewById(R.id.charOrderExample2);
        charOrder2.setAnimationDuration(4000L);
        charOrder2.addCharOrder("adg");
        charOrder2.setText("a");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                charOrder1.setText("g"); //move from a to g

                charOrder2.setText("g"); //just like charOrder1 but with different charOder
            }
        }, 2000L);
    }
}
