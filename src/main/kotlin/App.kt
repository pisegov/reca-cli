import data.fingerprints.DatabaseDataSource
import data.fingerprints.FingerprintsRepository
import data.songs.SongsDBDatasource
import data.songs.SongsRepository
import domain.SampleAnalyzer

object App {
    val repo = FingerprintsRepository(DatabaseDataSource())
    val songsRepo = SongsRepository(SongsDBDatasource())
    val sampleAnalyzer = SampleAnalyzer(1024, 11025F)
}