package data.fingerprints

import data.fingerprints.model.FingerprintDTO
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object FingerprintsDAO : Table(name = "fingerprints") {
    private val hash = FingerprintsDAO.integer("hash")
    private val songId = FingerprintsDAO.integer("song_id")

    private val offset = FingerprintsDAO.integer("time_offset")

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
            val fingerprintsModel = FingerprintsDAO.select { hash.inList(hashesList) }
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