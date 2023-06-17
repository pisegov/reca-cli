package utils

import App.dbFiller
import App.fingerprintsRepository
import App.recognizer
import App.sampleAnalyzer
import App.songsRepository
import constants.AUDIO_RESOURCES_DIRECTORY
import constants.CONVERTED_SONG_EXTENSION
import constants.TEST_OUTPUTS_DIRECTORY
import data.DatabaseProvider
import data.fingerprints.model.FingerprintDTO
import data.model.Song
import domain.Preprocessor
import domain.operating_specifiers.audio_dispatcher_providers.FileAudioDispatcherProvider
import domain.operating_specifiers.constellation_map_writers.NoConstellationMapWriter
import kotlinx.coroutines.*
import java.io.File
import java.time.Duration
import java.time.LocalDateTime

data class TestSong(
    val title: String,
    val path: String,
)

class Tester {

    private var params = Params()

    val outputBuilder = StringBuilder()
    fun writeOutput(output: Any = "") {
        println(output.toString())
        outputBuilder.appendLine(output.toString())
    }

    fun doOneTest(
        preprocessedSongsPaths: List<TestSong>,
        songsToRecognize: List<String>,
        testPaths: List<String>,
        newParams: Params,
    ) {
        params = newParams
        sampleAnalyzer.params = params
        refillDb(preprocessedSongsPaths)
        outputBuilder.clear()
        testSamples(
            songsToRecognize,
            testPaths
        )
    }

    fun startBigTest() {
//        val originalSongsPaths = dbFiller.getAllMusicFilesInDirectory(ORIGINALS_DIRECTORY)
//        val preprocessedSongsPaths = preprocessAllFiles(originalSongsPaths)
        val preprocessedSongsPaths =
            dbFiller.getAllMusicFilesInDirectory("/home/pmikhail/reca-resources/audio/tmp", ".wav")
                .mapIndexed { index, path ->
                    val preprocessor = Preprocessor(path)
                    val title = preprocessor.getFileTitleFromPath()
                    println(title)
                    TestSong(title, path)
                }

        val testPaths = listOf(
            AUDIO_RESOURCES_DIRECTORY + "tests/20/",
            AUDIO_RESOURCES_DIRECTORY + "tests/10/",
            AUDIO_RESOURCES_DIRECTORY + "tests/5/"
        )
        val songsToRecognize = dbFiller.getAllMusicFilesInDirectory(testPaths[0], ".wav")
            .map {
                val prep = Preprocessor(it)
                prep.getFileTitleFromPath()
            }

//        for (n in 2..2) {
//            for (d in 1..15) {
//                for (ta in 5..5) {
//                    doOneTest(
//                        preprocessedSongsPaths, songsToRecognize, testPaths,
//                        Params(numberOfPeaks = n, referencePeakDistance = d, targetAreaSize = ta)
//                    )
//                }
//            }
//        }
//
//        for (n in 3..3) {
//            for (d in 13..13) {
//                for (ta in 1..20) {
//                    doOneTest(
//                        preprocessedSongsPaths, songsToRecognize, testPaths,
//                        Params(numberOfPeaks = n, referencePeakDistance = d, targetAreaSize = ta)
//                    )
//                }
//            }
//        }

        val configsList = listOf<Params>(
            Params(1, 10, 20),
            Params(2, 13, 20),
            Params(3, 13, 11),
            Params(4, 13, 6),
            Params(5, 7, 6),
            Params(6, 7, 4),
            Params(7, 10, 4),
            Params(8, 1, 2),
            Params(9, 1, 2),
            Params(10, 1, 2),
        )

        configsList.forEach { config ->
            doOneTest(
                preprocessedSongsPaths, songsToRecognize, testPaths,
                config
            )
        }
    }

    private fun preprocessAllFiles(songsPaths: List<String>): List<TestSong> {
        return songsPaths.map { songFilePath ->
            val preprocessor = Preprocessor(songFilePath)
            val title = preprocessor.getTitleWithArtist()
            val path = preprocessor.downSampleAndGetPath()
            println(title)
            TestSong(title, path)
        }
    }

    private fun refillDb(testSongs: List<TestSong>) {
        DatabaseProvider.recreateTables()

        runBlocking {
            addTestSongs(testSongs)
        }
    }

    private suspend fun addTestSongs(testSongs: List<TestSong>) = withContext(Dispatchers.IO) {
        val threadsQty = 8
        val dividedSongsList: MutableList<List<TestSong>> = mutableListOf()
        val fullSongsListSize = testSongs.size
        val onePeaceCapacity = testSongs.size / threadsQty + 1
        for (i in 0 until threadsQty) {
            val fromIndex = i * onePeaceCapacity
            val toIndex =
                if (fullSongsListSize < fromIndex + onePeaceCapacity) fullSongsListSize else fromIndex + onePeaceCapacity

            dividedSongsList.add(testSongs.subList(fromIndex, toIndex))
            launch {
                dividedSongsList[i].forEach { song ->
                    addSingleTestSong(song)
                }
            }
        }
    }

    private fun addSingleTestSong(testSong: TestSong) {
        val songInDB = songsRepository.insertSong(Song(title = testSong.title))
        val songFingerprints = sampleAnalyzer.getHashesFromSample(
            FileAudioDispatcherProvider(File(testSong.path)),
            NoConstellationMapWriter(),
        ).map {
            FingerprintDTO(
                hash = it.hashCode(),
                timeOffsetFromOriginal = it.anchorTimeStamp,
                songId = songInDB.id
            )
        }

        println("\n${songInDB.id}: ${testSong.title}")
        fingerprintsRepository.addFingerprintsList(songFingerprints)
    }

    fun testSamples(songsToRecognize: List<String>, testPaths: List<String>) {

        writeOutput('\n' + params.testOutputFileName + '\n')
        val divider = "======================================================================================\n"
        songsToRecognize.forEach { song ->
            writeOutput(divider)
            testPaths.forEach { path ->
                val date1 = LocalDateTime.now()

                val recordedFileDispatcherProvider =
                    FileAudioDispatcherProvider(File(path + song + CONVERTED_SONG_EXTENSION))
                recognizer.recognizeSong(recordedFileDispatcherProvider, NoConstellationMapWriter())
                val date2 = LocalDateTime.now()

                val duration = Duration.between(date1, date2).toSeconds()
                writeOutput("Seconds have passed: $duration")
                writeOutput()
            }
        }

        File(TEST_OUTPUTS_DIRECTORY + params.testOutputFileName).writeText(outputBuilder.toString())
    }


    fun independentTest(newParams: Params) {
        val preprocessedSongsPaths =
            dbFiller.getAllMusicFilesInDirectory("/home/pmikhail/reca-resources/audio/tmp", ".wav")
                .mapIndexed { index, path ->
                    val preprocessor = Preprocessor(path)
                    val title = preprocessor.getFileTitleFromPath()
                    println(title)
                    TestSong(title, path)
                }

        val testPaths = listOf(
            AUDIO_RESOURCES_DIRECTORY + "tests/20/",
            AUDIO_RESOURCES_DIRECTORY + "tests/10/",
            AUDIO_RESOURCES_DIRECTORY + "tests/5/"
        )
        val songsToRecognize = dbFiller.getAllMusicFilesInDirectory(testPaths[0], ".wav")
            .map {
                val prep = Preprocessor(it)
                prep.getFileTitleFromPath()
            }

        params = newParams
        sampleAnalyzer.params = params
        outputBuilder.clear()
        refillDb(preprocessedSongsPaths)
        testSamples(
            songsToRecognize,
            testPaths
        )
    }
}