import data.fingerprints.DatabaseDataSource
import data.fingerprints.FingerprintsRepository
import data.songs.SongsDBDatasource
import data.songs.SongsRepository
import domain.SampleAnalyzer
import utils.DatabaseFiller
import utils.Recognizer

object App {
    val fingerprintsRepository = FingerprintsRepository(DatabaseDataSource())
    val songsRepository = SongsRepository(SongsDBDatasource())
    val sampleAnalyzer = SampleAnalyzer(1024, 11025F)
    val dbFiller = DatabaseFiller()
    val recognizer = Recognizer()
}