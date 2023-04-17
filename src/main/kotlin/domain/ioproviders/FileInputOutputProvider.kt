package domain.ioproviders

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class FileInputOutputProvider(private val inputFileName: String, peaksOutputFileName: String) :
    InputOutputProvider {
    private val path: Path = Paths.get(peaksOutputFileName)

    init {
        Files.write(path, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)
    }

    override fun provideAudioDispatcher(sampleRate: Float, bufferSize: Int): AudioDispatcher {
        return AudioDispatcherFactory.fromFile(File(inputFileName), bufferSize, 0)
    }

    override fun provideOutputPath(): Path {
        return path
    }
}
