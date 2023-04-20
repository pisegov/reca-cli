package data.fingerprints

import data.fingerprints.model.FingerprintDTO

class DatabaseDataSource : FingerprintsDataSource {

    override fun addFingerprintsList(list: List<FingerprintDTO>) {
        FingerprintsDAO.batchInsert(list)
    }

    override fun getFingerprints(hashesList: Collection<Int>): List<FingerprintDTO> {
        return FingerprintsDAO.fetchFingerprints(hashesList)
    }
}
