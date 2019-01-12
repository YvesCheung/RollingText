# RollingTextView

---

![preview][1]

[![build](https://jitpack.io/v/YvesCheung/RollingText.svg)](https://jitpack.io/#YvesCheung/RollingText)

[中文版README](README_CN.md)

Features
========

- easy to use, API is similar to TextView, and the setText method can be animated with up and down rolling

- support XML to set up common properties such as android:textSize/ android:textColor/ android:textStyle

- highly customizable to support animation effects of any single character

Animation
========

### Strategy

Different rolling effects can be achieved by setting different animation strategies

> The default animation is to roll down when small characters change to large characters, and vice versa.
>
> You can also specify that rolling to the same direction
>
> The carry animation can be worked from low digit to high digit, not only for decimal. But it can only be used for strings with a length less than 10 to prevent integer from overflow. It can only be used for a sequence of characters containing 0, otherwise the calculation of the carry will be meaningless.

![StrategyCompare][2]

### The order of characters

- The sequence of characters needs to be set up to tell ``RollingTextView`` how to change from the original character to the target character
- The common sequence of characters can be found in the ``CharOrder`` constant class
- When multiple orders are added and all are applicable to the target character and the original character, the precedence of the previous setting will be higher

```java
alphaBetView.addCharOrder(CharOrder.Alphabet);
alphaBetView.addCharOrder(CharOrder.UpperAlphabet);
alphaBetView.addCharOrder(CharOrder.Number);
alphaBetView.addCharOrder(CharOrder.Hex);
alphaBetView.addCharOrder(CharOrder.Binary);
```

![charOrderCompare][3]

### Rolling fluency

> The fluency of the animation can be adjusted by passing a ``factor`` parameter. The closer the ``factor`` value is to 0, the rolling will appear to be more hopping. And the closer the ``factor`` value is to 1, the more smooth the rolling is.

![stickyFactor][4]

### other

More ideas can be implemented on the ``CharOrderStrategy`` interface to customize your own animation effects

Download
========

1. Add in the root project build.gradle file：

    ```groovy
    allprojects {
	 	repositories {
	 		...
			maven { url 'https://jitpack.io' }
	 	}
	}
	```
	
2. Add dependency in the appropriate module

    ```groovy
    dependencies {  
	    compile 'com.github.YvesCheung:RollingText:[![build](https://jitpack.io/v/YvesCheung/RollingText.svg)](https://jitpack.io/#YvesCheung/RollingText)'
	}
    ```

Usage
=========

### XML settings

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
    android:shadowColor="#ff44ffdd"
    android:shadowDx="10"
    android:shadowDy="10"
    android:shadowRadius="10"/>
```

### write in java code

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

License
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
