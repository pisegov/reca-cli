import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.filters.BandPass
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.WaveformWriter
import be.tarsos.dsp.writer.WriterProcessor
import java.io.File
import java.io.RandomAccessFile
import java.util.*

class Recorder(
    private val sampleRate: Float,
    private val bufferSize: Int = 1024,
) {

    private val dispatcher: AudioDispatcher =
        AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 128)

    private val audioFormat = TarsosDSPAudioFormat(sampleRate, 16, 1, true, true)

    fun recordSample(title: String) {

//        val outputFile = RandomAccessFile(title, "rw")
//        val outputFile = RandomAccessFile(File(title), "rw")
        val writer = WaveformWriter(audioFormat, title)
//        val writer = WriterProcessor(audioFormat, outputFile)
        dispatcher.addAudioProcessor(writer)

        val thread = Thread(dispatcher)
        thread.start()
        println("Record is started")
        readln()
        dispatcher.stop()
        thread.join()
    }
}