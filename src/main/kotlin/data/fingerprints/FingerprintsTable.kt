package data.fingerprints

import data.fingerprints.model.FingerprintDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class FingerprintsTable(name: String = "fingerprints") : Table(name) {
    private val hash = integer("hash")
    private val songId = integer("song_id")
    //    private val songId =
    //        integer("song_id").references(DatabaseProvider.songsTable.id, onDelete = ReferenceOption.CASCADE)

    private val offset = integer("time_offset")

    private val table = this

    init {
        index(false, hash)
        index(true, hash, songId, offset)
    }

    fun batchInsert(list: List<FingerprintDTO>) {
        transaction {
            batchInsert(list) {
                this[hash] = it.hash
                this[offset] = it.timeOffsetFromOriginal
                this[songId] = it.songId
            }
        }
    }

    fun fetchFingerprints(hashesList: Collection<Int>): List<FingerprintDTO> {
        return transaction {
            val fingerprintsModel = table.select { hash.inList(hashesList) }
            val fingerprints = fingerprintsModel.map { fingerprintModel ->
                FingerprintDTO(
                    hash = fingerprintModel[hash],
                    timeOffsetFromOriginal = fingerprintModel[offset],
                    songId = fingerprintModel[songId]
                )
            }
            fingerprints
        }
    }

}