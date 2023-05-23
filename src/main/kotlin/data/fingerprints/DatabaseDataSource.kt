package data.fingerprints

import data.fingerprints.model.FingerprintDTO

class DatabaseDataSource : FingerprintsDataSource {

    override fun addFingerprintsList(list: List<FingerprintDTO>) {
        FingerprintsTable.batchInsert(list)
    }

    override fun getFingerprints(hashesList: Collection<Int>): List<FingerprintDTO> {
        return FingerprintsTable.fetchFingerprints(hashesList)
    }
}
