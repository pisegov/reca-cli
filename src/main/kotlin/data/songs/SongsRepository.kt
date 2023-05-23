package data.songs

import data.songs.model.SongDTO
import domain.Song

class SongsRepository(private val dataSource: SongsDataSource) {

    fun insertSong(song: Song): SongDTO {
        return dataSource.insert(song)
    }

    fun fetchSongsList(idList: Collection<Int>, limit: Int = 10): Map<Int, SongDTO> {
        return dataSource.fetchSongsList(idList)
    }
}