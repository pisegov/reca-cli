package data.songs

import data.songs.model.SongDTO

interface SongsDataSource {
    fun insert(song: SongDTO)
    fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO>
}