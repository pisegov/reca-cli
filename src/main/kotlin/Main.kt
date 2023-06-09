import App.dbFiller
import App.recognizer
import App.sampleAnalyzer
import data.DatabaseProvider
import domain.operating_specifiers.audio_dispatcher_providers.MicrophoneAudioDispatcherProvider
import kotlinx.coroutines.runBlocking
import constants.ORIGINALS_DIRECTORY
import utils.Params
import java.util.logging.Level
import java.util.logging.Logger


fun addNewSongsBase() {
    DatabaseProvider.recreateTables()
    runBlocking {
        dbFiller.addSongsFromDirectory(ORIGINALS_DIRECTORY)
    }
}


// https://stackoverflow.com/questions/50778442/how-to-disable-jaudiotagger-logger-completely
fun disableLoggers() {
    val pin = listOf<Logger>(Logger.getLogger("org.jaudiotagger"))
    for (l: Logger in pin) l.level = Level.OFF
}

fun updateSampleAnalyzerParams(params: Params = Params()) {
    sampleAnalyzer.params = params
}

fun main(args: Array<String>) {
    DatabaseProvider.provide()
    disableLoggers()
    updateSampleAnalyzerParams(
        Params(numberOfPeaks = 3, referencePeakDistance = 13, targetAreaSize = 11)
    )

    recognizer.recognizeSong(MicrophoneAudioDispatcherProvider())
}