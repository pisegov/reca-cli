package utils

import App.dbFiller
import App.recognizer
import constants.ORIGINAL_SONG_EXTENSION
import domain.operating_specifiers.audio_dispatcher_providers.FileAudioDispatcherProvider
import domain.operating_specifiers.audio_dispatcher_providers.MicrophoneAudioDispatcherProvider
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.io.path.*

typealias CompoundArgumentCallback = (String) -> Unit
typealias SingleArgumentCallback = () -> Unit
typealias ActionCallback = () -> Unit

object ArgumentsHandler {
    var tableTitles: Pair<String, String> = Pair("fingerprints_test", "songs_test")
        private set
    private var action: ActionCallback =
        { recognizer.recognizeSong(MicrophoneAudioDispatcherProvider()) }
    private var actionIsSet = false

    private val compoundArgsStateMachine = mapOf<String, CompoundArgumentCallback>(
        "-a" to { str ->
            updateAction {
                val path = Path(str)
                if (path.isDirectory()) {
                    runBlocking {
                        dbFiller.addSongsFromDirectory(directoryPath = path.pathString)
                    }
                } else if (path.isRegularFile()) {
                    val extension = ".${path.toFile().extension}"
                    if (extension == ORIGINAL_SONG_EXTENSION) {
                        dbFiller.addSingleSong(path.absolutePathString())
                    } else {
                        println("Use .mp3 file")
                    }
                } else {
                    println("Wrong argument")
                }
            }
        },
        "-f" to { str ->
            updateAction { recognizer.recognizeSong(FileAudioDispatcherProvider(File(str))) }
        },
    )

    private val singleArgsStateMachine = mapOf<String, SingleArgumentCallback>(
        "-b" to {
            tableTitles = Pair("fingerprints", "songs")
        },
        "-bt" to {
            updateAction {
                App.tester.startBigTest()
            }
        },
        "-m" to {
            updateAction { recognizer.recognizeSong(MicrophoneAudioDispatcherProvider()) }
        },
        "-h" to {
            updateAction {
                println(
                    "Reca help message\n" +
                            "Parameters: \n" +
                            "-h                       Show help message\n" +
                            "-f file.mp3              Recognize from file\n" +
                            "-m                       Recognize from microphone\n" +
                            "-b                       Use big database with 1000 songs\n" +
                            "-a file.mp3              Add song from file\n" +
                            "-a /path/to/directory    Add all songs from directory"
                )
            }
        },
    )

    private fun updateAction(callback: ActionCallback) {
        if (actionIsSet) return

        action = callback
        actionIsSet = true
    }

    fun handle(args: Array<String>) {
        args.forEachIndexed { index, arg ->
            when (arg) {
                in singleArgsStateMachine.keys -> {
                    singleArgsStateMachine[arg]?.invoke()
                }

                in compoundArgsStateMachine.keys -> {
                    try {
                        compoundArgsStateMachine[arg]?.let { it(args[index + 1]) }
                    } catch (e: IndexOutOfBoundsException) {
                        println("You didn't pass the file or directory")
                    }
                }
            }
        }
    }

    fun invokeAction() {
        action.invoke()
    }
}