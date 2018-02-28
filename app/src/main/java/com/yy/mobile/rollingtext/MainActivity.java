package com.yy.mobile.rollingtext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yy.mobile.rollingtextview.Direction;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.Strategy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        view = findViewById(R.id.rollingTextView);
        view.addCharOrder(RollingTextView.Number);
        view.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
        Observable.interval(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        view.setText(list.get(idx++ % list.size()));
                    }
                });
//        Observable.timer(2,TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        view.setText("1");
//                    }
//                });

//        List<Integer> integers = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
//        CircularList<Integer> list = new CircularList<>(integers, 56, 16);
//        int index = 0;
//        for (int i : list) {
//            Log.i("zyi", "index:" + index++ + " num:" + i);
//        }
//        Log.i("zyi", list.subList(10, 20).toString());
//        Log.i("zyi", "index of 3 is " + list.indexOf(3));
    }
}
