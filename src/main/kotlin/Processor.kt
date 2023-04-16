import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.SpectralPeakProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.AudioPlayer
import model.Address
import model.Peak
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.sound.sampled.AudioFormat

class Processor(
    private val bufferSize: Int = 1024,
    private val sampleRate: Float = 11025F,
) {

    companion object {
        const val TARGET_AREA_SIZE = 3
        const val REFERENCE_PEAK_DISTANCE = 5
    }

    fun processFromFile(inputFileName: String, peaksOutputFileName: String): List<Address> {
        val path = Paths.get(peaksOutputFileName)
        val hashesPath = Paths.get("fileHashes.txt")
        Files.write(path, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)
        Files.write(hashesPath, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)

        val audioDispatcher = AudioDispatcherFactory.fromFile(File(inputFileName), bufferSize, 0)
        return processSample(audioDispatcher, path, hashesPath)
    }

    fun processFromMicrophone(peaksOutputFileName: String): List<Address> {
        val path = Paths.get(peaksOutputFileName)
        val hashesPath = Paths.get("microHashes.txt")
        Files.write(path, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)
        Files.write(hashesPath, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)

        val audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate.toInt(), bufferSize, 0)
        return processSample(audioDispatcher, path, hashesPath)
    }

    private fun processSample(
        audioDispatcher: AudioDispatcher,
        peaksOutputPath: Path,
        hashesOutputPath: Path,
    ): List<Address> {
        val medianFilterLength = 10000
        val noiseFloorFactor = 0.5F
        val numberOfPeaks = 10

//        audioDispatcher.addAudioProcessor(AudioPlayer(AudioFormat(sampleRate, 16, 1, true, false)))

        val spectralPeakFollower = SpectralPeakProcessor(bufferSize, 0, sampleRate.toInt())
        audioDispatcher.addAudioProcessor(spectralPeakFollower)
        var timeCounter = 0
        var referenceIndex = 0
        val peaksFound = mutableListOf<Peak>()
        val addresses = mutableListOf<Address>()
        audioDispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {}
            override fun process(audioEvent: AudioEvent): Boolean {
                val noiseFloor = SpectralPeakProcessor.calculateNoiseFloor(
                    spectralPeakFollower.magnitudes,
                    medianFilterLength,
                    noiseFloorFactor
                )
                val frequencyEstimates = spectralPeakFollower.frequencyEstimates
                val localMaximaIndexes: MutableList<Int> =
                    SpectralPeakProcessor.findLocalMaxima(spectralPeakFollower.magnitudes, noiseFloor)

                //remove frequency estimates not in range 500..3000
                val maximums = mutableListOf<Float>()
                var i = 0
                while (i in localMaximaIndexes.indices) {
                    val peak = localMaximaIndexes[i]
                    when (frequencyEstimates[peak]) {
                        !in 500f..3000f -> {
//                            localMaximaIndexes.removeAt(index)
                            localMaximaIndexes.removeAt(i)
                            --i
                            if (i < 0) i = 0
                        }

                        else -> {
                            maximums.add(frequencyEstimates[peak])
                            ++i
                        }
                    }
                }

                val list: List<SpectralPeakProcessor.SpectralPeak> = SpectralPeakProcessor.findPeaks(
                    spectralPeakFollower.magnitudes,
                    spectralPeakFollower.frequencyEstimates,
                    localMaximaIndexes.toList(),
                    numberOfPeaks,
                    1000
                )

                // do something with the list...
                list.forEach { peak ->
                    val str = "$timeCounter ${peak.frequencyInHertz}\n"
                    println(str.trim())
                    peaksFound.add(Peak(timeCounter, peak.frequencyInHertz.toInt()))
                    Files.write(peaksOutputPath, str.toByteArray(), StandardOpenOption.APPEND)
                }
                timeCounter++

                if (peaksFound.size > referenceIndex + REFERENCE_PEAK_DISTANCE + TARGET_AREA_SIZE - 1) {
                    val left = referenceIndex + REFERENCE_PEAK_DISTANCE
                    val right = left + TARGET_AREA_SIZE - 1

                    val sublist = peaksFound.subList(left, right)
                    sublist.forEach { peak ->
                        addresses.add(peak.getAddress(peaksFound[referenceIndex]))
                    }
                    referenceIndex += TARGET_AREA_SIZE
                }
                return true
            }

        })

        val thread = Thread(audioDispatcher)

        thread.start()
        readln()
        audioDispatcher.stop()
        thread.join()

        return addresses
    }
}
