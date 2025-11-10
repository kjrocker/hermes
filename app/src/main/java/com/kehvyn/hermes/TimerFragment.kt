package com.kehvyn.hermes

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
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
    private var repetitionValue: Int = 10
    private val defaultRepetitionValue: Int = 10
    private var currentRepetitionCount: Int = 0
    private var previousNonZeroRepetitions: Int = 10
    private val isInfiniteMode: Boolean get() = repetitionValue == 0
    
    companion object {
        private const val PREFS_NAME = "hermes_prefs"
        private const val KEY_TIMER_VALUE = "timer_value"
        private const val KEY_REPETITION_VALUE = "repetition_value"
    }
    private var isRunning: Boolean = false
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var autoRepeatHandler: Handler = Handler(Looper.getMainLooper())
    private var autoRepeatRunnable: Runnable? = null
    private val audioTracks = listOf(
        R.raw.ship_bell_chimes,
        R.raw.ship_bell_chimes_plus_2,
        R.raw.ship_bell_chimes_plus_4,
        R.raw.ship_bell_chimes_minus_2,
        R.raw.ship_bell_chimes_minus_4
    )
    private val completionAudioTrack = R.raw.church_bell_37120

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
        
        binding.buttonIncrement.setOnLongClickListener {
            startAutoRepeat { 
                timerValue++
                updateTimerDisplay()
                saveTimerValue()
            }
            true
        }
        
        binding.buttonIncrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                stopAutoRepeat()
            }
            false
        }

        binding.buttonDecrement.setOnClickListener {
            if (timerValue > minTimerValue) {
                timerValue--
                updateTimerDisplay()
                saveTimerValue()
            }
        }
        
        binding.buttonDecrement.setOnLongClickListener {
            startAutoRepeat { 
                if (timerValue > minTimerValue) {
                    timerValue--
                    updateTimerDisplay()
                    saveTimerValue()
                }
            }
            true
        }
        
        binding.buttonDecrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                stopAutoRepeat()
            }
            false
        }

        binding.buttonRepetitionIncrement.setOnClickListener {
            if (isInfiniteMode) {
                repetitionValue = 1
            } else {
                repetitionValue++
            }
            updateRepetitionDisplay()
            saveRepetitionValue()
        }
        
        binding.buttonRepetitionIncrement.setOnLongClickListener {
            startAutoRepeat { 
                if (isInfiniteMode) {
                    repetitionValue = 1
                } else {
                    repetitionValue++
                }
                updateRepetitionDisplay()
                saveRepetitionValue()
            }
            true
        }
        
        binding.buttonRepetitionIncrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                stopAutoRepeat()
            }
            false
        }

        binding.buttonRepetitionDecrement.setOnClickListener {
            if (repetitionValue > 0) {
                if (repetitionValue == 1) {
                    previousNonZeroRepetitions = repetitionValue
                    repetitionValue = 0
                } else {
                    repetitionValue--
                }
                updateRepetitionDisplay()
                saveRepetitionValue()
            }
        }
        
        binding.buttonRepetitionDecrement.setOnLongClickListener {
            startAutoRepeat { 
                if (repetitionValue > 0) {
                    if (repetitionValue == 1) {
                        previousNonZeroRepetitions = repetitionValue
                        repetitionValue = 0
                    } else {
                        repetitionValue--
                    }
                    updateRepetitionDisplay()
                    saveRepetitionValue()
                }
            }
            true
        }
        
        binding.buttonRepetitionDecrement.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                stopAutoRepeat()
            }
            false
        }

        binding.buttonInfiniteToggle.setOnClickListener {
            if (isInfiniteMode) {
                repetitionValue = previousNonZeroRepetitions
            } else {
                previousNonZeroRepetitions = repetitionValue
                repetitionValue = 0
            }
            updateRepetitionDisplay()
            saveRepetitionValue()
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
        loadRepetitionValue()
        updateTimerDisplay()
        updateRepetitionDisplay()
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
        currentRepetitionCount = 0
        updatePlayPauseButton()
        
        timerRunnable = object : Runnable {
            override fun run() {
                if (!isInfiniteMode) {
                    currentRepetitionCount++
                    if (currentRepetitionCount >= repetitionValue) {
                        playCompletionSound()
                        stopTimer()
                        return
                    }
                }
                
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
    
    private fun playCompletionSound() {
        val player = MediaPlayer.create(requireContext(), completionAudioTrack)
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
    
    private fun saveRepetitionValue() {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt(KEY_REPETITION_VALUE, repetitionValue).apply()
    }
    
    private fun loadRepetitionValue() {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        repetitionValue = sharedPrefs.getInt(KEY_REPETITION_VALUE, defaultRepetitionValue)
    }
    
    private fun updateRepetitionDisplay() {
        binding.textviewRepetitionDisplay.text = if (isInfiniteMode) "Inf." else "x${repetitionValue.toString()}"
    }
    
    private fun startAutoRepeat(action: () -> Unit) {
        stopAutoRepeat()
        
        autoRepeatRunnable = object : Runnable {
            override fun run() {
                action()
                autoRepeatHandler.postDelayed(this, 100) // Repeat every 100ms
            }
        }
        
        // Start after initial delay of 500ms
        autoRepeatHandler.postDelayed(autoRepeatRunnable!!, 500)
    }
    
    private fun stopAutoRepeat() {
        autoRepeatRunnable?.let { autoRepeatHandler.removeCallbacks(it) }
        autoRepeatRunnable = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        stopAutoRepeat()
        // MediaPlayer instances are now created and released per playback
        _binding = null
    }
}