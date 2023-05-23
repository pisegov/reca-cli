package data.songs

import data.songs.model.SongDTO
import data.model.Song
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object SongsTable : Table("songs") {
    private val id = SongsTable.integer("id").autoIncrement()
    private val title = SongsTable.varchar("title", 100)
    override val primaryKey = PrimaryKey(id, name = "Songs_Id")

    fun insert(song: Song): SongDTO {
        return transaction {
            val model = insert {
                it[title] = song.title
            }
            SongDTO(
                id = model[SongsTable.id],
                title = model[title]
            )
        }
    }

    fun fetchSong(id: Int): SongDTO {
        val songModel = SongsTable.select { SongsTable.id.eq(id) }.single()

        return SongDTO(
            id = songModel[SongsTable.id],
            title = songModel[title]
        )
    }

    fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return transaction {
            val songModel = SongsTable.select { SongsTable.id.inList(idList) }.limit(10)

            songModel.toList().associate { row ->
                row[SongsTable.id] to SongDTO(
                    id = row[SongsTable.id],
                    title = row[title]
                )
            }
        }
    }

}