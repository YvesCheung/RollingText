package com.yy.mobile.rollingtext

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
        val textView = ChangeTypeFaceView(this)
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        frameLayout.addView(textView, lp)
        setContentView(frameLayout)

        textView.setTextSize(25f)
        textView.animationDuration = 10 * 1000L
        textView.setText("aaaaaaaaaaa")
        textView.setText("zzzzzzzzzzz")
        textView.setOnClickListener {
            textView.setText("aaaaaaaaaaa", false)
            textView.setText("zzzzzzzzzzz")
        }
    }

    class ChangeTypeFaceView(context: Context) : RollingTextView(context) {

        init {
            this.typeface = ResourcesCompat.getFont(context, R.font.test_font)
        }
    }
}