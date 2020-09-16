/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.engedu.wordstack

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val words = ArrayList<String>()
    private var stackedLayout: StackedLayout? = null
    private var word1: String? = null
    private var word2: String? = null
    private val placedTiles = mutableListOf<LetterTile>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val assetManager = assets
        try {
            val inputStream = assetManager.open("words.txt")
            val `in` = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (`in`.readLine().also { line = it } != null) {
                val word = line!!.trim { it <= ' ' }
                if (word.length == WORD_LENGTH) words.add(word)
            }
        } catch (e: IOException) {
            val toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG)
            toast.show()
        }
        val verticalLayout = findViewById<View>(R.id.vertical_layout) as LinearLayout
        stackedLayout = StackedLayout(this)
        verticalLayout.addView(stackedLayout, 3)
        val word1LinearLayout = findViewById<View>(R.id.word1)
//        word1LinearLayout.setOnTouchListener(TouchListener())
        word1LinearLayout.setOnDragListener(dragListener);
        val word2LinearLayout = findViewById<View>(R.id.word2)
//        word2LinearLayout.setOnTouchListener(TouchListener())
        word2LinearLayout.setOnDragListener(dragListener);
    }

    private inner class TouchListener : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN && !stackedLayout!!.empty()) {
                val tile = stackedLayout!!.peek() as LetterTile
                tile.moveToViewGroup(v as ViewGroup)
                placedTiles.add(tile)
                if (stackedLayout!!.empty()) {
                    val messageBox = findViewById<View>(R.id.message_box) as TextView
                    messageBox.text = "$word1 $word2"
                }
                return true
            }
            return false
        }
    }

    private val dragListener = OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                v.setBackgroundColor(LIGHT_BLUE)
                v.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                v.setBackgroundColor(LIGHT_GREEN)
                v.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                v.setBackgroundColor(LIGHT_BLUE)
                v.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                v.setBackgroundColor(Color.WHITE)
                v.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                // Dropped, reassign Tile to the target Layout
                val tile = event.localState as LetterTile
                tile.moveToViewGroup(v as ViewGroup)
                if (stackedLayout!!.empty()) {
                    val messageBox = findViewById<View>(R.id.message_box) as TextView
                    messageBox.text = "$word1 $word2"
                }
                placedTiles.add(tile)

                true
            }
            else -> false
        }
    }

    fun onStartGame(view: View?): Boolean {
        stackedLayout?.clear()
        (findViewById<View>(R.id.word1) as LinearLayout).removeAllViews()
        (findViewById<View>(R.id.word2) as LinearLayout).removeAllViews()
        val messageBox = findViewById<View>(R.id.message_box) as TextView
        messageBox.text = "Game started"
        val size = words.size
        val pos1 = Random.nextInt(size)
        word1 = words[pos1]
        while (pos1 == Random.nextInt(size).also { word2 = words[it] }) {
        }
        var p1 = 0
        var p2 = 0
        val list = mutableListOf<Char>()
        while (p1 < WORD_LENGTH && p2 < WORD_LENGTH) {
            if (Random.nextInt(2) == 0) {
                list.add(word1!![p1])
                p1++
            } else {
                list.add(word2!![p2])
                p2++
            }
        }
        val scrambledWord = String(list.toCharArray()) + word1!!.substring(p1, WORD_LENGTH) + word2!!.substring(p2, WORD_LENGTH)
//        Log.d("sb", "onStartGame: $scrambledWord $word1 $word2 ${list.toString()}")
        for (c in scrambledWord.reversed()) {
            val letterTile = LetterTile(this, c)
            stackedLayout?.push(letterTile)
        }
//        messageBox.text = scrambledWord
        return true
    }

    fun onUndo(view: View?): Boolean {
        if (placedTiles.size > 0) {
            placedTiles.removeLast().moveToViewGroup(stackedLayout as ViewGroup)
        }
        return true
    }

    companion object {
        private const val WORD_LENGTH = 5
        val LIGHT_BLUE = Color.rgb(176, 200, 255)
        val LIGHT_GREEN = Color.rgb(200, 255, 200)
    }
}