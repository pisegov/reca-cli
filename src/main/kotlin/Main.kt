import App.dbFiller
import App.sampleAnalyzer
import constants.ORIGINALS_DIRECTORY
import data.DatabaseProvider
import kotlinx.coroutines.runBlocking
import utils.ArgumentsHandler
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
    ArgumentsHandler.handle(args)

    DatabaseProvider.initDatabaseTables(
        fingerprintsTableName = ArgumentsHandler.tableTitles.first,
        songsTableName = ArgumentsHandler.tableTitles.second,
    )

    disableLoggers()

    updateSampleAnalyzerParams(
        Params(numberOfPeaks = 3, referencePeakDistance = 13, targetAreaSize = 11)
    )

    ArgumentsHandler.invokeAction()
}