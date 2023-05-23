package domain

import App.fingerprintsRepository
import App.sampleAnalyzer
import App.songsRepository
import data.model.SongWithTimeDelta
import domain.operating_specifiers.audio_dispatcher_providers.AudioDispatcherProvider
import domain.operating_specifiers.constellation_map_writers.ConstellationMapWriter
import domain.operating_specifiers.constellation_map_writers.NoConstellationMapWriter


class Recognizer {
    fun testSong(
        dispatcherProvider: AudioDispatcherProvider,
        constellationMapWriter: ConstellationMapWriter = NoConstellationMapWriter(),
    ) {

        // can analyze from file or from microphone
        val microAddressesList =
            sampleAnalyzer
                .getHashesFromSample(dispatcherProvider, constellationMapWriter)
                .map { it.hashCode() to it.anchorTimeStamp }

        println("addresses found: ${microAddressesList.size}")

        val matchingFingerprints = fingerprintsRepository.getFingerprints(microAddressesList.map { it.first })

        println("fingerprints found = ${matchingFingerprints.size}")

        val songsWithDeltas = microAddressesList
            .associateWith { recordedFingerprint ->
                matchingFingerprints.filter { matchingFingerprint ->
                    matchingFingerprint.hash == recordedFingerprint.first
                }
            }
            .flatMap { entry ->
                val remoteFingerprint = entry.key
                val dbFingerprintsList = entry.value
                dbFingerprintsList.map { dbFingerprint ->
                    SongWithTimeDelta(
                        dbFingerprint.songId,
                        (dbFingerprint.timeOffsetFromOriginal - remoteFingerprint.second)
                    )
                }
            }

//     transform raw matching points from database
//     to list of possible songs with maximums
//     of quantities of time deltas between song point and record point
        val recognitionOptions = songsWithDeltas
            .groupingBy { songWithDelta -> Pair(songWithDelta.songId, songWithDelta.delta) }
            .eachCount()
            .entries
            .groupBy { it.key.first }

            // for now, we have map with this structure:
            // key: songId
            // value: (songId, timeDelta) -> quantity
            // we take songId and maximum quantity of deltas for this song id
            .map { entry -> Pair(entry.key, entry.value.maxBy { it.value }.value) }
            .sortedByDescending { idToQtyPair -> idToQtyPair.second }

        val recOptionsWithTitles = getRecognitionOptionsWithTitles(recognitionOptions.toMap())
        println(recOptionsWithTitles)
        println(recOptionsWithTitles.maxBy { it.value }.key)
    }

    private fun getRecognitionOptionsWithTitles(recognitionOptionsIDs: Map<Int, Int>): Map<String, Int> {
        val list = recognitionOptionsIDs.entries.sortedByDescending { it.value }.subList(0, 10)
        val songTitles = songsRepository.fetchSongsList(list.map { it.key })

        return list.associate { entry ->
            val songId = entry.key
            val songTitle = songTitles[songId]?.title
            val maxDeltasQty = entry.value
            (songTitle ?: "Undefined Song") to (maxDeltasQty)
        }
    }
}