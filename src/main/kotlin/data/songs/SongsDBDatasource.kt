package data.songs

import data.songs.model.SongDTO
import data.model.Song

class SongsDBDatasource(private val table: SongsTable) : SongsDataSource {
    override fun insert(song: Song): SongDTO {
        return table.insert(song)
    }

    override fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return table.fetchSongsList(idList)
    }
}