package utils

import App.fingerprintsRepository
import App.sampleAnalyzer
import App.songsRepository
import data.fingerprints.model.FingerprintDTO
import domain.Song
import domain.operating_specifiers.audio_dispatcher_providers.FileAudioDispatcherProvider
import domain.operating_specifiers.constellation_map_writers.NoConstellationMapWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class DatabaseFiller {
    suspend fun addSongsFromDirectory(directoryPath: String = ORIGINALS_DIRECTORY) = withContext(Dispatchers.IO) {
        val fullSongsList = getAllMp3FilesInDirectory(directoryPath)
        val threadsQty = 6
        val dividedSongsList: MutableList<List<String>> = mutableListOf()
        val fullSongsListSize = fullSongsList.size
        val onePeaceCapacity = fullSongsList.size / threadsQty + 1
        for (i in 0 until threadsQty) {
            val fromIndex = i * onePeaceCapacity
            val toIndex =
                if (fullSongsListSize < fromIndex + onePeaceCapacity) fullSongsListSize else fromIndex + onePeaceCapacity

            dividedSongsList.add(fullSongsList.subList(fromIndex, toIndex))
            launch {
                dividedSongsList[i].forEach { song ->
                    addSingleSong(song)
                }
            }
        }
    }

    // keep an old consistent function in case if it works better
    private fun addSongsBaseConsistently(fullSongsList: List<String>) {
        // analyze provided songs list
        fullSongsList.forEach { song ->
            addSingleSong(song)
        }
    }

    fun addSingleSong(songFilePath: String) {
        val preprocessor = Preprocessor(songFilePath)
        val songTitleWithArtist = preprocessor.getTitleWithArtist()
        val preprocessedFile = File(preprocessor.downSampleAndGetPath())

        val songInDB = songsRepository.insertSong(Song(title = songTitleWithArtist))

        val songFingerprints = sampleAnalyzer.getHashesFromSample(
            FileAudioDispatcherProvider(preprocessedFile),
//            ConstellationFileWriter(TEXT_RESOURCES_DIRECTORY + song + TEXT_EXTENSION)
            NoConstellationMapWriter()
        ).map {
            FingerprintDTO(
                hash = it.hashCode(),
                timeOffsetFromOriginal = it.anchorTimeStamp,
                songId = songInDB.id
            )
        }

        preprocessedFile.delete()

        println("\n${songInDB.id}: $songTitleWithArtist")
        fingerprintsRepository.addFingerprintsList(songFingerprints)
    }

    //https://stackoverflow.com/questions/49419971/kotlin-get-list-of-all-files-in-resource-folder
    private fun getAllMp3FilesInDirectory(directoryPath: String): List<String> {
        val resourcesPath = Paths.get(directoryPath)
        return Files.walk(resourcesPath)
            .filter { item -> Files.isRegularFile(item) }
            .filter { item ->
                val title = item.toString()
                title.endsWith(".mp3")
            }
            .map { item -> item.toString() }
            .toList()
    }

}