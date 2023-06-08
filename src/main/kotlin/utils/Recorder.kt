package utils

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.WaveformWriter

class Recorder(
    private val sampleRate: Float,
    private val bufferSize: Int = 1024,
) {
    fun recordSong(title: String = "record.wav") {
        println("Press enter to start recording $title")
        readln()
        recordSample(title)
    }

    private val audioFormat = TarsosDSPAudioFormat(sampleRate, 16, 1, true, true)

    private fun recordSample(title: String) {
        val writer = WaveformWriter(audioFormat, title)
        val dispatcher: AudioDispatcher =
            AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 128)
        dispatcher.addAudioProcessor(writer)
        val thread = Thread(dispatcher)
        thread.start()
        println("Record is started")
        readln()
        dispatcher.stop()
        thread.join()
    }
}