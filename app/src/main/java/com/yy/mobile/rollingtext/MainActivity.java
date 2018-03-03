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
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    RollingTextView view;

    private List<String> list = Arrays.asList("1", "9", "12", "19", "24", "36", "47", "56", "63", "78", "89", "95", "132");
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        view = findViewById(R.id.rollingTextView);
//        view.addCharOrder(CharOrder.Number);
//        view.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
//        Observable.interval(3, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        view.setText(list.get(idx++ % list.size()));
//                    }
//                });
//
//        final RollingTextView timeView = findViewById(R.id.timeView);
//        timeView.setTextSize(20);
//        timeView.setAnimationDuration(300);
//
//        @SuppressLint("SimpleDateFormat") final DateFormat format = new SimpleDateFormat("hh:mm:ss");
//        Flowable.interval(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        timeView.setText(format.format(new Date()));
//                    }
//                });

        RollingTextView carryView = findViewById(R.id.carryTextView);
        carryView.setAnimationDuration(5000L);
        carryView.addCharOrder(CharOrder.Number);
        carryView.setCharStrategy(Strategy.CarryBitAnimation);
        carryView.setText("1");
        carryView.setText("353425346");
    }
}
