package data.songs

import data.songs.model.SongDTO
import data.model.Song

class SongsDBDatasource : SongsDataSource {
    override fun insert(song: Song): SongDTO {
        return SongsTable.insert(song)
    }

    override fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return SongsTable.fetchSongsList(idList)
    }
}