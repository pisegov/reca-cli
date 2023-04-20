package domain.ioproviders

import be.tarsos.dsp.AudioDispatcher
import java.nio.file.Path

interface InputOutputProvider {
    fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher

    //    fun provideOutputPath(): Path
    fun record(dispatcher: AudioDispatcher)
}