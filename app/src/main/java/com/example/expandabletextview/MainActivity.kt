package com.example.expandabletextview

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var expandableTextView1: ExpandableTextView
    private lateinit var expandableTextView2: ExpandableTextView
    private lateinit var expandableTextView3: ExpandableTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Когда количество строк меньше foldedStateLineCount
        expandableTextView1 = findViewById(R.id.expandableTextView1)
        expandableTextView1.text =
            "akjsdfhkjdashfkjhasdkjfkjdsagfhjhjadsbhjcafjhjdkhfkjahsdfkjhasdgfhjgasdjhfghjasdgfjhasdgfhjsdabfhjbacvjhdsgfhgasjdfhgahsdbjhcbj"

        // Много параметров из кода
        expandableTextView2 = findViewById(R.id.expandableTextView2)
        expandableTextView2.text =
            "akjsdfhkjdashfkjhasdkjfkjdsagfhjhjadsbhjcafjhjdkhfkjahsdfkjhasdgfhjgasdjhfghjasdgfjhasdgfhjsdabfhjbacvjhdsgfhgasjdfhjkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkhgahsdbjhcbjhjcafjhjdkhfkjahsdfkjhasdgfhjgasdjhfghjasdgfjhasdgfhjsdabfh"
        expandableTextView2.foldedStateLineCount = 2
        expandableTextView2.textTypeface =
            Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        expandableTextView2.buttonTextTypeface =
            Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)
        expandableTextView2.textSize = 20f

        // Пример, где не нужно вставлять строку обрезания, сделанный через xml атрибуты
        expandableTextView3 = findViewById(R.id.expandableTextView3)
    }
}