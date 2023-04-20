package data.songs

import data.songs.model.SongDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object SongsDAO : Table("songs") {
    private val id = SongsDAO.integer("id").autoIncrement()
    private val title = SongsDAO.varchar("title", 100)
    override val primaryKey = PrimaryKey(id, name = "Songs_Id")

    fun insert(songDTO: SongDTO) {
        transaction {
            insert {
                it[title] = songDTO.title
            }
        }
    }

    fun fetchSong(id: Int): SongDTO {
        val songModel = SongsDAO.select { SongsDAO.id.eq(id) }.single()

        return SongDTO(
            id = songModel[SongsDAO.id],
            title = songModel[title]
        )
    }

    fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return transaction {
            val songModel = SongsDAO.select { SongsDAO.id.inList(idList) }.limit(10)

            songModel.toList().associate { row ->
                row[SongsDAO.id] to SongDTO(
                    id = row[SongsDAO.id],
                    title = row[title]
                )
            }
        }
    }

}