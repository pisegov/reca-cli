package utils

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.WaveformWriter

class Recorder(
    sampleRate: Float,
    bufferSize: Int = 1024,
) {

    companion object {
        fun recordSong(recorder: Recorder, title: String = "record.wav") {
            println("Press enter to start recording $title")
            readln()
            recorder.recordSample(title)
        }
    }

    private val dispatcher: AudioDispatcher =
        AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 128)

    private val audioFormat = TarsosDSPAudioFormat(sampleRate, 16, 1, true, true)

    fun recordSample(title: String) {

        val writer = WaveformWriter(audioFormat, title)
        dispatcher.addAudioProcessor(writer)

        val thread = Thread(dispatcher)
        thread.start()
        println("Record is started")
        readln()
        dispatcher.stop()
        thread.join()
    }
}