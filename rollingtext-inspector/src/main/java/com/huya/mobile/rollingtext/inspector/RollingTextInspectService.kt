package com.huya.mobile.rollingtext.inspector

import android.view.View
import com.huya.mobile.uinspector.impl.properties.view.ViewPropertiesParser
import com.huya.mobile.uinspector.impl.properties.view.ViewPropertiesParserService
import com.huya.mobile.uinspector.util.colorToString
import com.huya.mobile.uinspector.util.quote
import com.huya.mobile.uinspector.util.spStr
import com.yy.mobile.rollingtextview.CharOrder
import com.yy.mobile.rollingtextview.RollingTextView
import com.yy.mobile.whisper.Output

/**
 * @author YvesCheung
 * 2021/1/14
 */
class RollingTextInspectService : ViewPropertiesParserService {

    override fun tryCreate(v: View): ViewPropertiesParser<out View>? {
        if (v is RollingTextView) {
            return RollingTextViewParser(v)
        }
        return null
    }

    private class RollingTextViewParser(view: RollingTextView) : ViewPropertiesParser<RollingTextView>(view) {

        override fun parse(@Output props: MutableMap<String, Any?>) {
            super.parse(props)

            props["text"] = view.getText().quote()

            props["textSize"] = view.getTextSize().spStr

            props["textColor"] = colorToString(view.textColor)

            val tf = view.typeface
            if (tf != null) {
                if (tf.isBold) {
                    props["isBold"] = "true"
                }

                if (tf.isItalic) {
                    props["isItalic"] = "true"
                }
            }

            if (view.letterSpacingExtra != 0) {
                props["letterSpacingExtra"] = view.letterSpacingExtra
            }

            props["strategy"] = view.charStrategy::class.java.simpleName

            val charOrder = getCharOrder(view)
            if (charOrder != null) {
                props["char order"] =
                    if (charOrder.isEmpty()) {
                        "[]"
                    } else {
                        charOrder.joinToString { charSet ->
                            when (val s = charSet.asSequence().filter { it != EMPTY }.joinToString("")) {
                                CharOrder.Number -> "[Number]"
                                CharOrder.Hex -> "[Hex]"
                                CharOrder.Binary -> "[Binary]"
                                CharOrder.Alphabet -> "[Alphabet]"
                                CharOrder.UpperAlphabet -> "[UpperAlphabet]"
                                else -> s
                            }
                        }
                    }

            }

            props["duration"] = view.animationDuration
        }

        companion object {

            private const val EMPTY = 0.toChar()

            private var blackList = false

            private val charOrderManagerField by lazy(LazyThreadSafetyMode.NONE) {
                val f = RollingTextView::class.java.getDeclaredField("charOrderManager")
                f.isAccessible = true
                f
            }

            private val charOrderListField by lazy(LazyThreadSafetyMode.NONE) {
                val cls = Class.forName("com.yy.mobile.rollingtextview.CharOrderManager")
                val f = cls.getDeclaredField("charOrderList")
                f.isAccessible = true
                f
            }

            private fun getCharOrder(view: RollingTextView): List<Set<Char>>? {
                if (!blackList) {
                    try {
                        val charOrderManager = charOrderManagerField.get(view)
                        @Suppress("UNCHECKED_CAST")
                        return charOrderListField.get(charOrderManager) as List<Set<Char>>
                    } catch (e: Throwable) {
                        blackList = true
                    }
                }
                return null
            }
        }
    }
}