# RollingTextView

---

![preview][1]

特性
======

- 使用简单，API与TextView类似，setText方法可带有上下滚动的动画
- 支持xml设置android:textSize/android:textColor/android:textStyle等常用属性
- 可高度定制，支持任何单个字符的上下滚动变化效果

动画效果
=======

### 策略

可以通过设置不同的动画策略来实现不同的滚动效果

> 默认的动画是小字符向大字符变化时向下滚动，反之向上滚动
>
> 也可以指定让滚动向同一个方向
>
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

### 滚动流畅度

> 可以通过传递一个 ``factor`` 参数来调整动画的流畅度。 ``factor`` 值越接近0.0，滚动会显得比较跳跃。而 ``factor`` 值越接近1.0，滚动越平滑

![stickyFactor][4]

### 其他

更多的想法可自行实现 ``CharOrderStrategy`` 接口，定制自己的动画效果

配置
=====

1. 在App根目录的project build.gradle文件中添加：

    ```groovy
    allprojects {
	 	repositories {
	 		...
			maven { url 'https://jitpack.io' }
	 	}
	}
	```
	
2. 在对应的module 中添加依赖：

    ```groovy
    dependencies {  
	    compile 'com.github.YvesCheung:RollingText:1.2.0'
	}
    ```

使用
=====

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

开源协议
========

   		Copyright 2018 Yves Cheung

   	Licensed under the Apache License, Version 2.0 (the "License");
   	you may not use this file except in compliance with the License.
   	You may obtain a copy of the License at

       	http://www.apache.org/licenses/LICENSE-2.0

   	Unless required by applicable law or agreed to in writing, software
   	distributed under the License is distributed on an "AS IS" BASIS,
   	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   	See the License for the specific language governing permissions and
   	limitations under the License.
    
    
  [1]: https://raw.githubusercontent.com/YvesCheung/RollingText/master/ezgif.com-optimize.gif
  [2]: https://raw.githubusercontent.com/YvesCheung/RollingText/master/StrategyCompare.gif
  [3]: https://raw.githubusercontent.com/YvesCheung/RollingText/master/charOrderCompare.gif
  [4]: https://raw.githubusercontent.com/YvesCheung/RollingText/master/stickyFactor.gif
