# RollingTextView

---

![preview][1]

[![](https://jitpack.io/#YvesCheung/RollingText/)](https://jitpack.io/#YvesCheung/RollingText/)

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

### 动画策略

可以通过设置不同的动画策略来实现不同的滚动效果

> 默认的动画是小字符向大字符变化时向下滚动，反之向上滚动

> 也可以指定让滚动向同一个方向

> 进位动画可以从低位数字进位到高位数字，不止是适用于十进制。但只能用于长度小于10的字符串防止溢出整型数。只能用于包含0的字符序列，否则进位的计算将没有意义。 

![StrategyCompare][2]

### 字符的顺序

- 字符的顺序需要自行设置，告诉RollingTextView怎么从原字符滚动变化到目标字符
- 常用的字符顺序可以在 ``CharOrder`` 常量中找到
- 当添加多个顺序时且都适用于目标字符和原字符，前面设置的优先级会更高

```java
alphaBetView.addCharOrder(CharOrder.Alphabet);
alphaBetView.addCharOrder(CharOrder.UpperAlphabet);
alphaBetView.addCharOrder(CharOrder.Number);
alphaBetView.addCharOrder(CharOrder.Hex);
alphaBetView.addCharOrder(CharOrder.Binary);
```

![charOrderCompare][3]


  [1]: https://github.com/YvesCheung/RollingText/blob/master/ezgif.com-optimize.gif
  [2]: https://github.com/YvesCheung/RollingText/blob/master/StrategyCompare.gif
  [3]: https://github.com/YvesCheung/RollingText/blob/master/charOrderCompare.gif
