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

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.ViewGroup
import android.support.v7.widget.AppCompatTextView

class LetterTile(context: Context?, letter: Char) : AppCompatTextView(context) {
    private var frozen = false
    fun moveToViewGroup(targetView: ViewGroup) {
        val parent = parent
        if (parent is StackedLayout) {
            parent.pop()
            targetView.addView(this)
            freeze()
            visibility = VISIBLE
        } else {
            val owner = parent as ViewGroup
            owner.removeView(this)
            (targetView as StackedLayout).push(this)
            unfreeze()
        }
    }

    fun freeze() {
        frozen = true
    }

    private fun unfreeze() {
        frozen = false
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        /**
         *
         * YOUR CODE GOES HERE
         *
         */
        return super.onTouchEvent(motionEvent)
    }

    companion object {
        const val TILE_SIZE = 150
    }

    init {
        text = letter.toString()
        textAlignment = TEXT_ALIGNMENT_CENTER
        height = TILE_SIZE
        width = TILE_SIZE
        textSize = 30f
        setBackgroundColor(Color.rgb(255, 255, 200))
    }
}