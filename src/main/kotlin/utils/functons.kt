package utils

import App.repo
import App.sampleAnalyzer
import App.songsRepo
import data.fingerprints.model.FingerprintDTO
import data.model.SongWithTimeDelta
import data.songs.model.SongDTO
import domain.ioproviders.FileInputOutputProvider
import domain.ioproviders.InputOutputProvider
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

//https://stackoverflow.com/questions/49419971/kotlin-get-list-of-all-files-in-resource-folder
fun getAllFilesInResources(): List<String> {
    val resourcesPath = Paths.get(FULL_SONGS_DIRECTORY)
    return Files.walk(resourcesPath)
        .filter { item -> Files.isRegularFile(item) }
        .filter { item -> item.toString().endsWith(".wav") }
        .map { item ->
            val str = item.toString()
            val lastDashIndex = str.lastIndexOf('/')
            str.substring(lastDashIndex + 1, str.length - 4)
        }.toList()
}

fun addSongsBase(fullSongsList: List<String>) {
    // analyze a little songs base
    fullSongsList.forEachIndexed { index, song ->

        val songFingerprints = sampleAnalyzer.getHashesFromSample(
            FileInputOutputProvider(
                FULL_SONGS_DIRECTORY + song + SONG_EXTENSION,
                TEXT_RESOURCES_DIRECTORY + song + TEXT_EXTENSION
            )
        ).map {
            FingerprintDTO(
                hash = it.hashCode(),
                timeOffsetFromOriginal = it.anchorTimeStamp,
                songId = index + 1
            )
        }
        println("\n$index: $song")
        repo.addFingerprintsList(songFingerprints)
        songsRepo.insertSong(
            SongDTO(
                id = index + 1,
                title = song
            )
        )
    }
}

fun testSong(ioProvider: InputOutputProvider) {

    // can analyze from file or from microphone
    val microAddressesList = sampleAnalyzer.getHashesFromSample(ioProvider)
        .map { it.hashCode() to it.anchorTimeStamp }
    println("addresses found: ${microAddressesList.size}")

    val matchingFingerprints = repo.getFingerprints(microAddressesList.map { it.first })

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

fun getRecognitionOptionsWithTitles(recognitionOptionsIDs: Map<Int, Int>): Map<String, Int> {
    val list = recognitionOptionsIDs.entries.sortedByDescending { it.value }.subList(0, 10)
    val songTitles = songsRepo.fetchSongsList(list.map { it.key })

    return list.associate { entry ->
        val songId = entry.key
        val songTitle = songTitles[songId]?.title
        val maxDeltasQty = entry.value
        (songTitle ?: "Undefined Song") to (maxDeltasQty)
    }
}