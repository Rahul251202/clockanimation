package com.example.clockanimation

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var clockHand: ImageView
    private lateinit var clockBackground: ImageView
    private val handler = Handler()
    private val updateInterval: Long = 1000 // 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTextView = findViewById(R.id.timeTextView)
        clockHand = findViewById(R.id.clockHand)
        clockBackground = findViewById(R.id.clockBackground)

        startClock()
        startBackgroundAnimation()
    }

    private fun startClock() {
        handler.post(object : Runnable {
            override fun run() {
                updateTime()
                animateClockHand()
                handler.postDelayed(this, updateInterval)
            }
        })
    }

    private fun updateTime() {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(currentTime)
        timeTextView.text = formattedTime
    }

    private fun animateClockHand() {
        val currentTime = Calendar.getInstance()
        val seconds = currentTime.get(Calendar.SECOND)
        val currentRotation = seconds * 6f // 360 degrees / 60 seconds = 6 degrees per second

        val nextRotation = currentRotation + 6f
        val animator = ObjectAnimator.ofFloat(clockHand, "rotation", currentRotation, nextRotation)
        animator.duration = updateInterval
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun startBackgroundAnimation() {
        val startColor = ContextCompat.getColor(this, R.color.start_color) // Define these colors in colors.xml
        val endColor = ContextCompat.getColor(this, R.color.end_color)

        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        colorAnimator.duration = 5000 // 5 seconds
        colorAnimator.repeatCount = ValueAnimator.INFINITE
        colorAnimator.repeatMode = ValueAnimator.REVERSE

        colorAnimator.addUpdateListener { animator ->
            val gradientDrawable = clockBackground.background as GradientDrawable
            gradientDrawable.setColor(animator.animatedValue as Int)
        }

        colorAnimator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

