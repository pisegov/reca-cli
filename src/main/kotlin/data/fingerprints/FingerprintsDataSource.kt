package data.fingerprints

import data.fingerprints.model.FingerprintDTO

interface FingerprintsDataSource {
    fun addFingerprintsList(list: List<FingerprintDTO>)
    fun getFingerprints(hashesList: Collection<Int>): List<FingerprintDTO>?
}