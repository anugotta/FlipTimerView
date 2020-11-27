package com.asp.fliptimer

import android.app.Activity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.widget.Toast
import com.asp.fliptimerviewlibrary.CountDownClock
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

    private var isRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val typeface = ResourcesCompat.getFont(this, R.font.roboto_bold)
        timerProgramCountdown.setCustomTypeface(typeface!!)
        timerProgramCountdown.startCountDown(99999999)
        timerProgramCountdown.setCountdownListener(object : CountDownClock.CountdownCallBack {
            override fun countdownAboutToFinish() {
                //TODO Add your code here
            }

            override fun countdownFinished() {
                Toast.makeText(this@MainActivity, "Finished", Toast.LENGTH_SHORT).show()
                timerProgramCountdown.resetCountdownTimer()
                isRunning = false
                btnPause.isEnabled = false
            }
        })


        btnPause.setOnClickListener {

            if(isRunning){
                isRunning = false
                timerProgramCountdown.pauseCountDownTimer()
                btnPause.text = getString(R.string.resume_timer)
            }else{
                isRunning = true
                timerProgramCountdown.resumeCountDownTimer()
                btnPause.text = getString(R.string.pause_timer)
            }


        }

    }
}
