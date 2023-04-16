package model

data class Address(
    val referenceFrequency: Int,
    val targetFrequency: Int,
    val timeDifference: Int,
) {
    companion object {
        const val FUZ_FACTOR = 5
    }

    override fun hashCode(): Int {
        return timeDifference +
                (targetFrequency - targetFrequency % FUZ_FACTOR) * 1000 +
                (referenceFrequency - referenceFrequency % FUZ_FACTOR) + 1000000
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (referenceFrequency != other.referenceFrequency) return false
        if (targetFrequency != other.targetFrequency) return false
        return timeDifference == other.timeDifference
    }
}