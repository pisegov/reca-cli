package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import java.io.File

class FileAudioDispatcherProvider(private val inputFile: File) :
    AudioDispatcherProvider {

    override fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher {
        return AudioDispatcherFactory.fromFile(inputFile, bufferSize, 0)
    }

    override fun startDispatcher(dispatcher: AudioDispatcher) {
        dispatcher.run()
    }
}
