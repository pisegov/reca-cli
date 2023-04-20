package data.songs

import data.songs.model.SongDTO

class SongsRepository(private val dataSource: SongsDataSource) {

    fun insertSong(song: SongDTO) {
        return dataSource.insert(song)
    }

    fun fetchSongsList(idList: Collection<Int>, limit: Int = 10): Map<Int, SongDTO> {
        return dataSource.fetchSongsList(idList)
    }
}