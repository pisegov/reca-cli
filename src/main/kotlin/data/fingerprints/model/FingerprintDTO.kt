package data.fingerprints.model

data class FingerprintDTO(
    val songId: Int,
    val hash: Int,
    val timeOffsetFromOriginal: Int,
)
