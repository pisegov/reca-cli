package domain.operating_specifiers.constellation_map_writers

class NoConstellationMapWriter : ConstellationMapWriter {
    override fun writePeaksData(timeStamp: Int, frequency: Float) {
        return
    }
}