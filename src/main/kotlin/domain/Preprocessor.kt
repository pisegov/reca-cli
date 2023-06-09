package domain

import ie.corballis.sox.SoXEffect
import ie.corballis.sox.Sox
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import constants.CONVERTED_SONGS_TEMP_DIRECTORY
import constants.CONVERTED_SONG_EXTENSION
import java.io.File

class Preprocessor(private val songFilePath: String) {

    private val file = File(songFilePath)
    private val songFileTitle = getFileTitleFromPath()

    fun getTitleWithArtist(): String {
        return try {
            val format = AudioFileIO.read(file)
            val tag = format.tag
            val artist = tag.getFirst(FieldKey.ARTIST)
            val title = tag.getFirst(FieldKey.TITLE)
            "$artist — $title"
        } catch (e: Throwable) {
            songFileTitle
        }
    }

    fun downSampleAndGetPath(): String {
        val preprocessedFilePath = CONVERTED_SONGS_TEMP_DIRECTORY + songFileTitle + CONVERTED_SONG_EXTENSION

        val sox = Sox("/usr/bin/sox")
        sox.inputFile(songFilePath)
            .outputFile(preprocessedFilePath)
            .effect(SoXEffect.SINC, "0-5k")
            .effect(SoXEffect.CHANNELS, "1")
            .effect(SoXEffect.RATE, "11025")
            .execute()
        return preprocessedFilePath
    }

    fun getFileTitleFromPath(): String {
        val lastDashIndex = songFilePath.lastIndexOf('/')
        val lastDotIndex = songFilePath.lastIndexOf('.')
        return songFilePath.substring(lastDashIndex + 1, lastDotIndex)
    }
}