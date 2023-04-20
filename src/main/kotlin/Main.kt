import data.fingerprints.FingerprintsDAO
import data.songs.SongsDAO
import domain.ioproviders.FileInputOutputProvider
import domain.ioproviders.MicrophoneInputOutputProvider
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import utils.*
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val properties = Properties()
    properties.load(File("local.properties").inputStream())

    Database.connect(
        url = "jdbc:mysql://localhost:3306/reca",
        driver = "com.mysql.cj.jdbc.Driver",
        user = properties.getProperty("mysql_user"),
        password = properties.getProperty("mysql_password")
    )

    transaction {
        SchemaUtils.create(FingerprintsDAO)
        SchemaUtils.create(SongsDAO)
    }

    val microSongId = 2
    val microIOProvider = MicrophoneInputOutputProvider(
        TEXT_RESOURCES_DIRECTORY + recordedSongsList[microSongId] + TEXT_EXTENSION
    )

    val fullSongsList = getAllFilesInResources()
    val recordedFileIOProvider = FileInputOutputProvider(
        AUDIO_RESOURCES_DIRECTORY + recordedSongsList[microSongId] + SONG_EXTENSION,
        TEXT_RESOURCES_DIRECTORY + recordedSongsList[microSongId] + TEXT_EXTENSION
    )
//    addSongsBase(fullSongsList)
//    testSong(recordedFileIOProvider)
    testSong(microIOProvider)
}