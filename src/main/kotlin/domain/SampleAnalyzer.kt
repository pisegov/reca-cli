package domain

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.SpectralPeakProcessor
import domain.ioproviders.InputOutputProvider
import model.Address
import model.Peak
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class SampleAnalyzer(
    private val bufferSize: Int = 1024,
    private val sampleRate: Float = 11025F,
) {

    companion object {
        const val TARGET_AREA_SIZE = 5
        const val REFERENCE_PEAK_DISTANCE = 3
    }

    fun getHashesFromSample(
        inputOutputProvider: InputOutputProvider,
    ): List<Address> {
        val medianFilterLength = 10
        val noiseFloorFactor = 0F
        val numberOfPeaks = 2

        val audioDispatcher = inputOutputProvider.provideAudioDispatcher(sampleRate, bufferSize)
//        audioDispatcher.addAudioProcessor(AudioPlayer(AudioFormat(sampleRate, 16, 1, true, false)))

        val spectralPeakFollower = SpectralPeakProcessor(bufferSize, 0, sampleRate.toInt())
        audioDispatcher.addAudioProcessor(spectralPeakFollower)
        val allPeaksFound = mutableListOf<Peak>()
        val addresses = mutableListOf<Address>()
        var timeStamp = 0
        var anchorPointIndex = 0

        // this audio processor invoke method process on each time chunk
        audioDispatcher.addAudioProcessor(object : AudioProcessor {
            override fun processingFinished() {}
            override fun process(audioEvent: AudioEvent): Boolean {
                val noiseFloor = SpectralPeakProcessor.calculateNoiseFloor(
                    spectralPeakFollower.magnitudes,
                    medianFilterLength,
                    noiseFloorFactor
                )
                val magnitudes = spectralPeakFollower.magnitudes
                val frequencyEstimates = spectralPeakFollower.frequencyEstimates
                val localMaximaIndexes: MutableList<Int> =
                    SpectralPeakProcessor.findLocalMaxima(magnitudes, noiseFloor)

                //remove frequency estimates not in range 500..3000
                filterFrequencyEstimates(localMaximaIndexes, frequencyEstimates, 500f..3000f)

                val spectralPeaksList: List<SpectralPeakProcessor.SpectralPeak> = SpectralPeakProcessor.findPeaks(
                    magnitudes,
                    frequencyEstimates,
                    localMaximaIndexes.toList(),
                    numberOfPeaks,
                    1000
                )

                spectralPeaksList.forEach { peak ->
                    val str = "$timeStamp ${peak.frequencyInHertz}\n"
                    println(str.trim())
                    allPeaksFound.add(Peak(timeStamp, peak.frequencyInHertz.toInt()))

                    // for constellation maps plotting
                    Files.write(inputOutputProvider.provideOutputPath(), str.toByteArray(), StandardOpenOption.APPEND)
                }
                timeStamp++

                val newAddresses = makeAddresses(allPeaksFound, anchorPointIndex)
                addresses.addAll(newAddresses)

                anchorPointIndex = allPeaksFound.size - TARGET_AREA_SIZE - REFERENCE_PEAK_DISTANCE
                if (newAddresses.isNotEmpty()) anchorPointIndex++
                if (anchorPointIndex < 0) anchorPointIndex = 0

                return true
            }
        })

        inputOutputProvider.record(audioDispatcher)

        println("${allPeaksFound.size} peaks found")

        return addresses
    }

    private fun filterFrequencyEstimates(
        localMaximaIndexes: MutableList<Int>,
        frequencyEstimates: FloatArray,
        range: ClosedFloatingPointRange<Float>,
    ) {
        var i = 0
        while (i in localMaximaIndexes.indices) {
            val peak = localMaximaIndexes[i]
            when (frequencyEstimates[peak]) {
                !in range -> {
                    localMaximaIndexes.removeAt(i)
                    --i
                    if (i < 0) i = 0
                }

                else -> {
                    ++i
                }
            }
        }
    }

    private fun makeAddresses(allPeaksFound: List<Peak>, referenceIndex: Int): List<Address> {
        val addresses = mutableListOf<Address>()
        var reference = referenceIndex
        var left = reference + REFERENCE_PEAK_DISTANCE
        var right = left + TARGET_AREA_SIZE - 1
        while (right < allPeaksFound.size) {
            for (i in left..right) {
                val peak = allPeaksFound[i]
                val referencePeak = allPeaksFound[reference]
                addresses.add(peak.getAddress(referencePeak))
            }

            reference++
            left++
            right++
        }
        return addresses
    }
}
