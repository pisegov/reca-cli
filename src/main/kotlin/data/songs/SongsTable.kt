package data.songs

import data.songs.model.SongDTO
import data.model.Song
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class SongsTable(name: String = "songs") : Table(name) {
    val id = integer("id").autoIncrement()
    private val title = varchar("title", 100)
    override val primaryKey = PrimaryKey(id, name = "Songs_Id")

    private val table = this

    init {
        index(true, id)
    }

    fun insert(song: Song): SongDTO {
        return transaction {
            val model = insert {
                it[title] = song.title
            }
            SongDTO(
                id = model[table.id],
                title = model[title]
            )
        }
    }

    fun fetchSong(id: Int): SongDTO {
        val songModel = table.select { table.id.eq(id) }.single()

        return SongDTO(
            id = songModel[table.id],
            title = songModel[title]
        )
    }

    fun fetchSongsList(idList: Collection<Int>): Map<Int, SongDTO> {
        return transaction {
            val songModel = table.select { table.id.inList(idList) }.limit(10)

            songModel.toList().associate { row ->
                row[table.id] to SongDTO(
                    id = row[table.id],
                    title = row[title]
                )
            }
        }
    }

}