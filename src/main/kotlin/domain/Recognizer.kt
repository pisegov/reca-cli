package domain

import App.fingerprintsRepository
import App.sampleAnalyzer
import App.songsRepository
import data.model.SongWithTimeDelta
import domain.operating_specifiers.audio_dispatcher_providers.AudioDispatcherProvider
import domain.operating_specifiers.constellation_map_writers.ConstellationMapWriter
import domain.operating_specifiers.constellation_map_writers.NoConstellationMapWriter


class Recognizer {
    fun recognizeSong(
        dispatcherProvider: AudioDispatcherProvider,
        constellationMapWriter: ConstellationMapWriter = NoConstellationMapWriter(),
    ) {

        val recordedFingerprintsList =
            sampleAnalyzer
                .getHashesFromSample(dispatcherProvider, constellationMapWriter)
                .associate { it.hashCode() to it.anchorTimeStamp }

        println("Fingerprints from record: ${recordedFingerprintsList.size}")

        val matchingFingerprints = fingerprintsRepository.getFingerprints(recordedFingerprintsList.keys)
        println("Matching fingerprints from db: ${matchingFingerprints.size}")

        val songsWithDeltas = matchingFingerprints.map { dbFingerprint ->
            SongWithTimeDelta(
                songId = dbFingerprint.songId,
                delta = dbFingerprint.timeOffsetFromOriginal - recordedFingerprintsList[dbFingerprint.hash]!!
            )
        }

//     transform raw matching points from database
//     to list of possible songs with maximums
//     of quantities of time deltas between song point and record point
        val recognitionOptions = songsWithDeltas
            .groupingBy { it }
            .eachCount()
            .entries
            .groupBy { it.key.songId }

            // for now, we have map with this structure:
            // key: songId
            // value: list[(songId, timeDelta) -> quantity]
            // we take songId and maximum quantity of deltas for this song id
            .map { entry ->
                val songId = entry.key
                val listOfTimeDeltasAndQtys = entry.value

                songId to
                        listOfTimeDeltasAndQtys.maxBy {
                            it.value // take maximum pair (timeDelta with qty) by qty
                        }.value // and take this qty
            }
            .sortedByDescending { idToQtyPair -> idToQtyPair.second }
            .take(10)
        // now we have list of pairs (song id, max qty of fingerprints with the same time delta)


        val recOptionsWithTitles = getRecognitionOptionsWithTitles(recognitionOptions)
        val mostLikelyOption = recOptionsWithTitles[0].second
        val secondMostLikelyOption = recOptionsWithTitles[1].second
        val gapFromSecond = mostLikelyOption.toDouble() / secondMostLikelyOption.toDouble()
        if (gapFromSecond > 1.5) {
            println(recOptionsWithTitles[0].first)
            println("The gap from the second is $gapFromSecond")
        } else {
            println("I'm unsure about the result")
            println(recOptionsWithTitles)
        }

    }

    private fun getRecognitionOptionsWithTitles(recognitionOptions: List<Pair<Int, Int>>): List<Pair<String, Int>> {
        val songTitles = songsRepository.fetchSongsList(recognitionOptions.map { it.first })

        return recognitionOptions.map { idToQty ->
            val id = idToQty.first
            val qty = idToQty.second
            val title = songTitles[id]?.title ?: "Undefined Song"
            title to qty
        }
    }
}
