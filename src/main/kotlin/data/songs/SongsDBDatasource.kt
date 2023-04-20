package data.songs

import data.songs.model.SongDTO

class SongsDBDatasource : SongsDataSource {
    override fun insert(song: SongDTO) {
        SongsDAO.insert(song)
    }

    override fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return SongsDAO.fetchSongsList(idList)
    }
}