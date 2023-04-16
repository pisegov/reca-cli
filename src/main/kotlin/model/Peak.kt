package model

data class Peak(
    val timeStamp: Int,
    val frequency: Int,
) {
    fun getAddress(reference: Peak): Address {
        return Address(reference.frequency, this.frequency, timeStamp - reference.timeStamp)
    }
}