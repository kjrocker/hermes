package com.kehvyn.hermes

import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kehvyn.hermes.databinding.FragmentTimerBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private var timerValue: Int = 23
    private var isRunning: Boolean = false
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var toneGenerator: ToneGenerator? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonIncrement.setOnClickListener {
            timerValue++
            updateTimerDisplay()
        }

        binding.buttonDecrement.setOnClickListener {
            if (timerValue > 0) {
                timerValue--
                updateTimerDisplay()
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        updateTimerDisplay()
        updatePlayPauseButton()
    }

    private fun updateTimerDisplay() {
        binding.textviewTimerDisplay.text = "${timerValue}s"
    }

    private fun updatePlayPauseButton() {
        binding.buttonPlayPause.text = if (isRunning) "⏸" else "▶"
        binding.buttonPlayPause.contentDescription = if (isRunning) "Pause timer" else "Play timer"
    }

    private fun startTimer() {
        if (timerValue <= 0) return
        
        isRunning = true
        updatePlayPauseButton()
        
        timerRunnable = object : Runnable {
            override fun run() {
                playNote()
                if (isRunning) {
                    handler.postDelayed(this, (timerValue * 1000).toLong())
                }
            }
        }
        handler.postDelayed(timerRunnable!!, (timerValue * 1000).toLong())
    }

    private fun stopTimer() {
        isRunning = false
        updatePlayPauseButton()
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun playNote() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 200)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        toneGenerator?.release()
        toneGenerator = null
        _binding = null
    }
}