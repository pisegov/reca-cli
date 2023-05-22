package domain.operating_specifiers.constellation_map_writers

interface ConstellationMapWriter {
    fun writePeaksData(timeStamp: Int, frequency: Float)
}