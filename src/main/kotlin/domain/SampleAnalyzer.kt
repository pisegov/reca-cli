package domain

import App.tester
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.SpectralPeakProcessor
import domain.operating_specifiers.audio_dispatcher_providers.AudioDispatcherProvider
import data.model.Address
import data.model.Peak
import utils.Params
import domain.operating_specifiers.constellation_map_writers.ConstellationMapWriter
import domain.operating_specifiers.constellation_map_writers.NoConstellationMapWriter

class SampleAnalyzer(
    private val bufferSize: Int = 1024,
    private val sampleRate: Float = 11025F,
) {

    var params = Params()

    fun getHashesFromSample(
        audioDispatcherProvider: AudioDispatcherProvider,
        // if you want to plot a constellation map, the writer adds this extra functionality
        constellationMapWriter: ConstellationMapWriter = NoConstellationMapWriter(),
    ): List<Address> {
        val medianFilterLength = 10
        val noiseFloorFactor = 0F
        val numberOfPeaks = params.numberOfPeaks
        val minDistanceInCents = 0

        val audioProcessors = mutableListOf<AudioProcessor>()
        val spectralPeakFollower = SpectralPeakProcessor(bufferSize, 0, sampleRate.toInt())
        audioProcessors.add(spectralPeakFollower)
        val allPeaksFound = mutableListOf<Peak>()
        val addresses = mutableListOf<Address>()
        var timeStamp = 0
        var anchorPointIndex = 0

        // this audio processor invoke method process on each time chunk
        val audioProcessor = object : AudioProcessor {
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
                    minDistanceInCents
                )

                spectralPeaksList.forEach { peak ->
                    val frequency = peak.frequencyInHertz
                    allPeaksFound.add(Peak(timeStamp, frequency.toInt()))

                    // for constellation maps plotting
                    // nothing will be written if constellationMapWriter is NoConstellationMapWriter
                    constellationMapWriter.writePeaksData(timeStamp, frequency)
                }
                timeStamp++

                val newAddresses = makeAddresses(allPeaksFound, anchorPointIndex, params)
                addresses.addAll(newAddresses)

                anchorPointIndex = allPeaksFound.size - params.targetAreaSize - params.referencePeakDistance
                if (newAddresses.isNotEmpty()) anchorPointIndex++
                if (anchorPointIndex < 0) anchorPointIndex = 0

                return true
            }
        }

        audioProcessors.add(audioProcessor)
        audioDispatcherProvider.addProcessorsAndStartDispatcher(audioProcessors)

        tester.writeOutput("\nPeaks found: ${allPeaksFound.size} ")

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

    private fun makeAddresses(allPeaksFound: List<Peak>, referenceIndex: Int, params: Params): List<Address> {
        val addresses = mutableListOf<Address>()
        var reference = referenceIndex
        var left = reference + params.referencePeakDistance
        var right = left + params.targetAreaSize - 1
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
