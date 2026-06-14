package com.manish.wordhaven.domain.service

interface SoundService {
    fun playStartSound()
    fun playSuccessSound()
    fun playFailureSound()
    fun release()
}
