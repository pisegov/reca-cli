package domain.operating_specifiers.constellation_map_writers

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ConstellationMapFileWriter(peaksOutputFileName: String) : ConstellationMapWriter {
    private val path: Path = Paths.get(peaksOutputFileName)

    init {
        // empty the existing file
        Files.write(path, byteArrayOf(), StandardOpenOption.TRUNCATE_EXISTING)
    }

    override fun writePeaksData(timeStamp: Int, frequency: Float) {
        val str = "$timeStamp $frequency\n"
        Files.write(path, str.toByteArray(), StandardOpenOption.APPEND)
    }
}