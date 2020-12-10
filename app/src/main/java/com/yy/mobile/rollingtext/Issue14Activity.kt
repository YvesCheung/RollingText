package com.yy.mobile.rollingtext

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.strategy.AlignAnimationStrategy
import com.yy.mobile.rollingtextview.strategy.AlignAnimationStrategy.TextAlignment
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.SameDirectionStrategy
import kotlinx.android.synthetic.main.activity_issue_14.*

class Issue14Activity : FragmentActivity() {

    private val list = listOf("1", "21339", "12", "123319", "24", "6", "247",
        "5226", "63", "378", "234389", "12395", "2", "1289", "32212", "400")

    private var idx = 0

    private val handler = Handler(Looper.getMainLooper())

    private val loopAnimator = object : Runnable {

        override fun run() {
            tvFragmentTitle.setText(list[idx++ % list.size])
            handler.postDelayed(this, 2000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_14)

        tvFragmentTitle.animationDuration = 1500L
        tvFragmentTitle.addCharOrder(CharOrder.Number)
        tvFragmentTitle.charStrategy = SameDirectionStrategy(
            Direction.SCROLL_DOWN,
            AlignAnimationStrategy(TextAlignment.Left)
        )
        tvFragmentTitle.post(loopAnimator)
    }
}