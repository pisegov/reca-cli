package data.fingerprints

import data.fingerprints.model.FingerprintDTO

class FingerprintsDBDataSource(private val table: FingerprintsTable) : FingerprintsDataSource {

    override fun addFingerprintsList(list: List<FingerprintDTO>) {
        table.batchInsert(list)
    }

    override fun getFingerprints(hashesList: Collection<Int>): List<FingerprintDTO> {
        return table.fetchFingerprints(hashesList)
    }
}
