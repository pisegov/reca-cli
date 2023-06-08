package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import java.io.File

class FileAudioDispatcherProvider(private val inputFile: File) :
    AudioDispatcherProvider {

    override fun addProcessorsAndStartDispatcher(processors: List<AudioProcessor>) {
        val dispatcher = AudioDispatcherFactory.fromFile(inputFile, 1024, 0)
        processors.forEach { processor ->
            dispatcher.addAudioProcessor(processor)
        }
        dispatcher.run()
    }
}
