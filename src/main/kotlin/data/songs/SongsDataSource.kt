package data.songs

import data.songs.model.SongDTO
import domain.Song

interface SongsDataSource {
    fun insert(song: Song): SongDTO
    fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO>
}