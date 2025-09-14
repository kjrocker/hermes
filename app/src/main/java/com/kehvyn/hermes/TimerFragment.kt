package com.kehvyn.hermes

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlin.random.Random
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
    private val minTimerValue: Int = 3
    private val defaultTimerValue: Int = 23
    
    companion object {
        private const val PREFS_NAME = "hermes_prefs"
        private const val KEY_TIMER_VALUE = "timer_value"
    }
    private var isRunning: Boolean = false
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private val audioTracks = listOf(
        R.raw.ship_bell_chimes,
        R.raw.ship_bell_chimes_plus_2,
        R.raw.ship_bell_chimes_plus_4,
        R.raw.ship_bell_chimes_minus_2,
        R.raw.ship_bell_chimes_minus_4
    )

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
            saveTimerValue()
        }

        binding.buttonDecrement.setOnClickListener {
            if (timerValue > minTimerValue) {
                timerValue--
                updateTimerDisplay()
                saveTimerValue()
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        // MediaPlayer will be created per playback in playNote()
        loadTimerValue()
        updateTimerDisplay()
        updatePlayPauseButton()
    }

    private fun updateTimerDisplay() {
        binding.textviewTimerDisplay.text = "${timerValue}s"
    }

    private fun updatePlayPauseButton() {
        binding.buttonPlayPause.text = if (isRunning) "||" else "▶"
        binding.buttonPlayPause.contentDescription = if (isRunning) "Pause timer" else "Play timer"
    }

    private fun startTimer() {
        if (timerValue < minTimerValue) return
        
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
        // Randomly select an audio track for this playback
        val randomTrack = audioTracks[Random.nextInt(audioTracks.size)]
        
        // Create MediaPlayer for this specific playback
        val player = MediaPlayer.create(requireContext(), randomTrack)
        player?.let {
            it.setOnCompletionListener { mp ->
                mp.release()
            }
            it.start()
        }
    }
    
    private fun saveTimerValue() {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt(KEY_TIMER_VALUE, timerValue).apply()
    }
    
    private fun loadTimerValue() {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        timerValue = sharedPrefs.getInt(KEY_TIMER_VALUE, defaultTimerValue)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        // MediaPlayer instances are now created and released per playback
        _binding = null
    }
}