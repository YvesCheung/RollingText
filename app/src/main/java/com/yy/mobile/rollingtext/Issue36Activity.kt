package com.yy.mobile.rollingtext

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView

/**
 * @author YvesCheung
 * 2023/9/5
 */
class Issue36Activity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frameLayout = FrameLayout(this)
        val textView = RollingTextView(this)
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        frameLayout.addView(textView, lp)
        setContentView(frameLayout)

        textView.setTextSize(50f)
        textView.typeface =
            ResourcesCompat.getFont(this, R.font.riviera_nights_light)
        textView.animationDuration = 10 * 1000L
        textView.addCharOrder(CharOrder.Alphabet)
        textView.setText("aaaaaaaaaaaa")
        textView.setText("zzzzzzzzzzzz")
        textView.setOnClickListener {
            textView.setText("hello world", false)
            textView.setText("animate text")
        }
    }
}