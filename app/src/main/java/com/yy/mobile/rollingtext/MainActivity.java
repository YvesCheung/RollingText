package com.yy.mobile.rollingtext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.Strategy;

import java.util.Arrays;
import java.util.List;

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
//        @SuppressLint("SimpleDateFormat") final DateFormat format = new SimpleDateFormat("HH:mm:ss");
//        Flowable.interval(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        timeView.setText(format.format(new Date()));
//                    }
//                });

        final RollingTextView carryView = findViewById(R.id.carryTextView);
        carryView.setAnimationDuration(13000L);
        carryView.addCharOrder(CharOrder.Number);
        carryView.setCharStrategy(Strategy.CarryBitAnimation);
        carryView.setText("0");
        //carryView.setText("1290");
//        carryView.addAnimatorListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                carryView.setText("ghn");
//            }
//        });
    }
}
