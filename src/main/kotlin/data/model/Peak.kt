package data.model

data class Peak(
    val timeStamp: Int,
    val frequency: Int,
) {
    fun getAddress(anchorPoint: Peak): Address {
        return Address(
            anchorPoint.frequency,
            this.frequency,
            timeStamp - anchorPoint.timeStamp,
            anchorPoint.timeStamp
        )
    }
}