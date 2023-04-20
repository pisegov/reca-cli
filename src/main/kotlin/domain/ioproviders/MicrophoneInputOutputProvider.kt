package domain.ioproviders

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class MicrophoneInputOutputProvider(peaksOutputFileName: String) : InputOutputProvider {
    private val path: Path = Paths.get(peaksOutputFileName)

    init {
        Files.write(path, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)
    }

    override fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher {
        return AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 0)
    }

//    override fun provideOutputPath(): Path {
//        return path
//    }

    override fun record(dispatcher: AudioDispatcher) {

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