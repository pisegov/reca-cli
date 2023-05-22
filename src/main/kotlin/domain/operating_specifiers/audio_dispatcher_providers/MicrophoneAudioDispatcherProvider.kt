package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory

class MicrophoneAudioDispatcherProvider : AudioDispatcherProvider {

    override fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher {
        return AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 0)
    }

    override fun startDispatcher(dispatcher: AudioDispatcher) {
        println("Press Enter for microphone recording")
        readln()

        val thread = Thread(dispatcher)
        thread.start()

        // want to stop recording from microphone by keyboard input
        readln()
        dispatcher.stop()
        thread.join()
    }
}