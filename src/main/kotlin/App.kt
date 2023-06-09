import data.DatabaseProvider
import data.fingerprints.FingerprintsDBDataSource
import data.fingerprints.FingerprintsRepository
import data.songs.SongsDBDatasource
import data.songs.SongsRepository
import domain.SampleAnalyzer
import domain.DatabaseFiller
import domain.Recognizer

object App {
    val fingerprintsRepository = FingerprintsRepository(FingerprintsDBDataSource(DatabaseProvider.fingerprintsTable))
    val songsRepository = SongsRepository(SongsDBDatasource(DatabaseProvider.songsTable))
    val sampleAnalyzer = SampleAnalyzer(1024, 11025F)
    val dbFiller = DatabaseFiller()
    val recognizer = Recognizer()
}