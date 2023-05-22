package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioDispatcher

interface AudioDispatcherProvider {
    fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher

    fun startDispatcher(dispatcher: AudioDispatcher)
}