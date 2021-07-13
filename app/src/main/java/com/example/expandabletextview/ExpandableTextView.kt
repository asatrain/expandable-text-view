package com.example.expandabletextview

import android.content.Context
import android.graphics.Typeface
import android.text.Layout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView

class ExpandableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    companion object {
        private const val MAX_BORDER_LINE_LENGTH = 200
    }

    private val textView: TextView = TextView(context)
    private val buttonTextView: TextView = TextView(context)
    private val textPaint: TextPaint
        get() = textView.paint
    private val textLayout: Layout
        get() = textView.layout

    init {
        addView(textView)
        addView(buttonTextView)
    }

    private var expandable = false
    private var buttonTextFitsLastLineText = false
    private val borderLineTextWidths = FloatArray(MAX_BORDER_LINE_LENGTH)

    var foldedStateLineCount = 3
        set(value) {
            field = if (value >= 1) value else 1
            requestLayout()
        }

    var text =
        ""
        set(value) {
            field = value
            requestLayout()
        }

    var textColor
        get() = textView.currentTextColor
        set(value) {
            textView.setTextColor(value)
        }

    var textTypeface: Typeface
        get() = textView.typeface
        set(value) {
            textView.typeface = value
        }

    var expandButtonText = ""
        set(value) {
            field = value
            if (!expanded) {
                buttonTextView.text = field
            }
        }

    var foldButtonText = ""
        set(value) {
            field = value
            if (expanded) {
                buttonTextView.text = field
            }
        }

    var buttonTextTypeface: Typeface
        get() = buttonTextView.typeface
        set(value) {
            buttonTextView.typeface = value
        }

    var buttonTextColor
        get() = buttonTextView.currentTextColor
        set(value) {
            buttonTextView.setTextColor(value)
        }

    var textSize
        get() = textView.textSize
        set(value) {
            textView.textSize = value
            buttonTextView.textSize = value
        }

    var truncation: String = ""
        set(value) {
            field = value
            requestLayout()
        }

    var expanded = false
        set(value) {
            if (field != value) {
                field = value
                buttonTextView.text = if (field) foldButtonText else expandButtonText
            }
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        foldedStateLineCount =
            typedArray.getInt(R.styleable.ExpandableTextView_foldedStateLineCount, 3)
        text = typedArray.getString(R.styleable.ExpandableTextView_text) ?: "ExpandableTextView"
        expandButtonText =
            typedArray.getString(R.styleable.ExpandableTextView_expandButtonText) ?: "more"
        foldButtonText =
            typedArray.getString(R.styleable.ExpandableTextView_foldButtonText) ?: "less"
        buttonTextTypeface = Typeface.DEFAULT_BOLD
        textSize = typedArray.getDimension(R.styleable.ExpandableTextView_textSize, 14f)
        truncation = typedArray.getString(R.styleable.ExpandableTextView_truncation) ?: "..."
        expanded = typedArray.getBoolean(R.styleable.ExpandableTextView_expanded, false)
        typedArray.recycle()

        buttonTextView.text = if (expanded) foldButtonText else expandButtonText

        textView.setOnClickListener {
            if (expandable) {
                expanded = !expanded
            }
        }
        buttonTextView.setOnClickListener {
            if (expandable) {
                expanded = !expanded
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("abc", text.substring(0..2) + " mes")
        measureText(widthMeasureSpec, heightMeasureSpec)
        setFinalText()
    }

    private fun measureText(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        textView.text = text
        measureChild(textView, widthMeasureSpec, heightMeasureSpec)
        expandable = textView.lineCount > foldedStateLineCount
        if (!expandable) { // Имеем дело с обычной TextView
            setMeasuredDimension(width, textView.measuredHeight)
        } else {
            measureChild(buttonTextView, widthMeasureSpec, heightMeasureSpec)
            if (!expanded) { // Свернутое состояние
                // Нужно убедиться, что ширина текста конпки + текста обрезания вмещаются в строку
                val buttonAndTruncationFitBorderLine =
                    buttonTextView.measuredWidth + textPaint.measureText(truncation) <= width
                // Нужно убедиться, что количество символов в нужной нам строке допустоимо
                // для метода textPaint.getTextWidths() в onLayout
                val borderLineHasValidLength =
                    textLayout.getLineEnd(foldedStateLineCount - 1) - 1 -
                            textLayout.getLineStart(foldedStateLineCount - 1) <= MAX_BORDER_LINE_LENGTH
                if (!buttonAndTruncationFitBorderLine || !borderLineHasValidLength) {
                    // В случае ошибки превращаем в обычный TextView
                    expandable = false
                    setMeasuredDimension(width, textView.measuredHeight)
                } else {
                    setMeasuredDimension(
                        width,
                        textLayout.getLineBottom(foldedStateLineCount - 1)
                    )
                }
            } else { // Раскрытое состояние
                if (buttonTextView.measuredWidth <= width) {
                    val lastLineWidth = textLayout.getLineWidth(textView.lineCount - 1)
                    // Определяем, нужен ли дополнительный переход на новую строку в конце в случае,
                    // если текст кнопки налезает на основной текст
                    buttonTextFitsLastLineText =
                        lastLineWidth + buttonTextView.measuredWidth <= width
                    if (!buttonTextFitsLastLineText) {
                        setMeasuredDimension(width, textView.measuredHeight + textView.lineHeight)
                    } else {
                        setMeasuredDimension(width, textView.measuredHeight)
                    }
                } else {
                    // Если текст кнопки шире строки, превращаем в обычный TextView
                    expandable = false
                    setMeasuredDimension(width, textView.measuredHeight)
                }
            }
        }
    }

    private fun setFinalText() {
        if (!expandable) {
            buttonTextView.visibility = INVISIBLE
        } else {
            buttonTextView.visibility = VISIBLE
            if (expanded) {
                if (!buttonTextFitsLastLineText) {
                    textView.text = "${text}\n"
                }
            } else { // Свернутое состояние
                val borderLineStart = textLayout.getLineStart(foldedStateLineCount - 1)
                var borderLineEnd = textLayout.getLineEnd(foldedStateLineCount - 1)
                var borderLineWidth = textLayout.getLineWidth(foldedStateLineCount - 1)
                if (text[borderLineEnd - 1] == '\n') {
                    borderLineWidth -= textPaint.measureText("\n")
                    borderLineEnd -= 1
                }

                val freeSpaceWidth = measuredWidth - buttonTextView.measuredWidth
                if (borderLineWidth < freeSpaceWidth) {
                    // Если граничная строка не доходит левого края текста кнопки,
                    // то текст обрезания добавлять не нужно
                    textView.text = text.substring(0 until borderLineEnd)
                } else {
                    // А если доходит, обрезаем текст
                    val borderLine = text.substring(borderLineStart until borderLineEnd)
                    val truncationWidth = textPaint.measureText(truncation)
                    textPaint.getTextWidths(borderLine, borderLineTextWidths)
                    var i = borderLine.lastIndex
                    while (borderLineWidth >= freeSpaceWidth - truncationWidth) {
                        borderLineWidth -= borderLineTextWidths[i]
                        --i
                    }
                    textView.text =
                        "${text.substring(0 until borderLineEnd - (borderLine.lastIndex - i))}$truncation"
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("abc", text.substring(0..2) + " lay")
        textView.layout(0, 0, measuredWidth, measuredHeight)
        if (expandable) {
            buttonTextView.layout(
                measuredWidth - buttonTextView.measuredWidth,
                measuredHeight - buttonTextView.measuredHeight,
                measuredWidth,
                measuredHeight
            )
        }
    }
}