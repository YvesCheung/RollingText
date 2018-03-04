# RollingTextView

---

![preview][1]

特性
========

- 使用简单，API与TextView类似，setText方法可带有上下滚动的动画
- 支持xml设置android:textSize/android:textColor/android:textStyle等常用属性
- 可高度定制，支持任何单个字符的上下滚动变化效果

使用方法
=======

### xml设置

```xml
<com.yy.mobile.rollingtextview.RollingTextView
    android:id="@+id/alphaBetView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="i am text"
    android:textSize="25sp" 
    android:textColor="#1d1d1d"
    android:textStyle="bold"
    android:gravity="center"
    android:shadowColor="#ffdd00"
    android:shadowDx="4dp"
    android:shadowDy="4dp"/>
```

### 代码设置

```java
final RollingTextView rollingTextView = findViewById(R.id.alphaBetView);
rollingTextView.setAnimationDuration(2000L);
rollingTextView.setCharStrategy(Strategy.NormalAnimation);
rollingTextView.addCharOrder(CharOrder.Alphabet);
rollingTextView.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
rollingTextView.addAnimatorListener(new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
        //finsih
    }
});
rollingTextView.setText("i am a text");
```

### 字符的顺序

- 字符的顺序需要自行设置，告诉RollingTextView怎么从原字符滚动变化到目标字符
- 常用的字符顺序可以在 **CharOrder** 常量中找到
- 当添加多个顺序时且都适用于目标字符和原字符，前面设置的优先级会更高

```java
alphaBetView.addCharOrder(CharOrder.Alphabet);
alphaBetView.addCharOrder(CharOrder.UpperAlphabet);
alphaBetView.addCharOrder(CharOrder.Number);
alphaBetView.addCharOrder(CharOrder.Hex);
alphaBetView.addCharOrder(CharOrder.Binary);
```

![charOrderCompare][2]



  [1]: https://github.com/YvesCheung/RollingText/blob/master/ezgif.com-optimize.gif
  [2]: https://github.com/YvesCheung/RollingText/blob/master/charOrderCompare.gif
