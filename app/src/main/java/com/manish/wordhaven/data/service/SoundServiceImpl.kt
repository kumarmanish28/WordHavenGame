package com.manish.wordhaven.data.service

import android.content.Context
import android.media.MediaPlayer
import com.manish.wordhaven.R
import com.manish.wordhaven.domain.service.SoundService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundServiceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SoundService {

    private var mediaPlayer: MediaPlayer? = null

    override fun playStartSound() {
        playSound(R.raw.start)
    }

    override fun playSuccessSound() {
        playSound(R.raw.completed)
    }

    override fun playFailureSound() {
        playSound(R.raw.failed)
    }

    private fun playSound(resId: Int) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener {
                it.release()
                if (mediaPlayer == it) mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
