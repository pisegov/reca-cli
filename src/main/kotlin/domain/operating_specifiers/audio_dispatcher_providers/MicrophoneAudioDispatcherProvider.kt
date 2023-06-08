package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory

class MicrophoneAudioDispatcherProvider : AudioDispatcherProvider {
    override fun addProcessorsAndStartDispatcher(processors: List<AudioProcessor>) {
        println("Press Enter for microphone recording")
        readln()

        val dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(11025, 1024, 0)
        processors.forEach { processor ->
            dispatcher.addAudioProcessor(processor)
        }

        val thread = Thread(dispatcher)
        thread.start()

        println("Recording is started")
        // want to stop recording from microphone by keyboard input
        readln()
        dispatcher.stop()
        thread.join()
    }
}