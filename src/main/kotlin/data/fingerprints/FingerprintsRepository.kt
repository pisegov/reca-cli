package data.fingerprints

import data.fingerprints.model.FingerprintDTO

class FingerprintsRepository(
    private val dataSource: FingerprintsDataSource,
) {
    fun addFingerprintsList(list: List<FingerprintDTO>) {
        dataSource.addFingerprintsList(list)
    }

    fun getFingerprints(hashesList: Collection<Int>): List<FingerprintDTO> {
        return dataSource.getFingerprints(hashesList) ?: listOf()
    }
}