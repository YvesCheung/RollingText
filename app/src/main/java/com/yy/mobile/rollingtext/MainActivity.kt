package com.yy.mobile.rollingtext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateDecelerateInterpolator
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.strategy.AlignAnimationStrategy
import com.yy.mobile.rollingtextview.strategy.AlignAnimationStrategy.TextAlignment
import com.yy.mobile.rollingtextview.strategy.Direction
import com.yy.mobile.rollingtextview.strategy.NormalAnimationStrategy
import com.yy.mobile.rollingtextview.strategy.Strategy.CarryBitAnimation
import com.yy.mobile.rollingtextview.strategy.Strategy.NormalAnimation
import com.yy.mobile.rollingtextview.strategy.Strategy.SameDirectionAnimation
import com.yy.mobile.rollingtextview.strategy.Strategy.StickyAnimation
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private val list = listOf("1", "21339", "12", "123319", "24", "6", "247",
            "5226", "63", "378", "234389", "12395", "2", "1289", "32212", "400")
    private var idx = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val normal = rollingTextView.apply {
            addCharOrder(CharOrder.Number)
            animationDuration = 2000L
        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                normal.setText(list[idx % list.size])
                handler.postDelayed(this, 3000L)
            }
        }, 2000L)

        val sameDirection = rollingTextView2.apply {
            addCharOrder(CharOrder.Number)
            animationDuration = 2000L
            charStrategy = SameDirectionAnimation(Direction.SCROLL_DOWN)
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                sameDirection.setText(list[idx % list.size])
                handler.postDelayed(this, 3000L)
            }
        }, 2000L)

        val carryBit = rollingTextView3.apply {
            addCharOrder(CharOrder.Number)
            animationDuration = 2000L
            charStrategy = CarryBitAnimation(Direction.SCROLL_UP)
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                carryBit.setText(list[idx % list.size])
                handler.postDelayed(this, 3000L)
            }
        }, 2000L)

        val alignLeft = rollingTextView4.apply {
            addCharOrder(CharOrder.Number)
            animationDuration = 2000L
            charStrategy = AlignAnimationStrategy(TextAlignment.Left)
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                alignLeft.setText(list[idx++ % list.size])
                handler.postDelayed(this, 3000L)
            }
        }, 2000L)

        stickyText.apply {
            animationDuration = 3000L
            addCharOrder("0123456789abcdef")
            charStrategy = StickyAnimation(0.9)
        }
        handler.postDelayed({ stickyText.setText("eeee") }, 2000L)

        stickyText2.apply {
            animationDuration = 3000L
            addCharOrder("0123456789abcdef")
            charStrategy = StickyAnimation(0.2)
        }
        handler.postDelayed({ stickyText2.setText("eeee\naaaaa") }, 2000L)

        alphaBetView.apply {
            animationDuration = 2000L
            charStrategy = NormalAnimation()
            addCharOrder(CharOrder.Alphabet)
            addCharOrder(CharOrder.UpperAlphabet)
            addCharOrder(CharOrder.Number)
            addCharOrder(CharOrder.Hex)
            addCharOrder(CharOrder.Binary)
            animationInterpolator = AccelerateDecelerateInterpolator()
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    //finsih
                }
            })
            setText("i am a text")
        }

        timeView.apply {
            animationDuration = 300
            letterSpacingExtra = 10
        }

        @SuppressLint("SimpleDateFormat")
        val format: DateFormat = SimpleDateFormat("HH:mm:ss")
        handler.post(object : Runnable {
            override fun run() {
                timeView.setText(format.format(Date()))
                handler.postDelayed(this, 1000L)
            }
        })

        carryTextView.apply {
            animationDuration = 13000L
            addCharOrder(CharOrder.Number)
            charStrategy = CarryBitAnimation(Direction.SCROLL_DOWN)
            setText("0")
            setText("1290")
        }

        val charOrder1 = charOrderExample1.apply {
            animationDuration = 4000L
            addCharOrder("abcdefg")
            setText("a")
        }

        val charOrder2 = charOrderExample2.apply {
            animationDuration = 4000L
            addCharOrder("adg")
            setText("a")
        }

        handler.postDelayed({
            charOrder1.setText("g") //move from a to g
            charOrder2.setText("g") //just like charOrder1 but with different charOder
        }, 2000L)

        val diffDirection = directionExample.apply {
            animationDuration = 500L
            addCharOrder(CharOrder.UpperAlphabet)
            letterSpacingExtra = 10
            charStrategy = object : NormalAnimationStrategy() {
                override fun findCharOrder(
                        sourceChar: Char,
                        targetChar: Char,
                        index: Int,
                        order: Iterable<Char>?): Pair<List<Char>, Direction> {
                    val (first) = super.findCharOrder(sourceChar, targetChar, index, order)
                    return Pair(first, diffDirection(index))
                }

                private fun diffDirection(index: Int): Direction {
                    return when (index) {
                        0 -> Direction.SCROLL_LEFT
                        1 -> Direction.SCROLL_UP
                        2 -> Direction.SCROLL_DOWN
                        else -> Direction.SCROLL_RIGHT
                    }
                }
            }

        }
        object : Runnable {
            var idx = 0
            override fun run() {
                diffDirection.setText((1111 * (idx++ % 9 + 1)).toString())
                handler.postDelayed(this, 1000L)
            }
        }.run()
    }
}