package utils

const val RESOURCES_DIRECTORY = "/home/pmikhail/reca-resources/"
const val AUDIO_RESOURCES = "audio/"
const val TEXT_RESOURCES = "txt-data/"
const val FULL_SONGS_ORIGINALS = "full-songs/originals/"
const val TEMP_DIRECTORY = "tmp/"
const val ORIGINAL_SONG_EXTENSION = ".mp3"
const val CONVERTED_SONG_EXTENSION = ".wav"
const val TEXT_EXTENSION = ".txt"

const val AUDIO_RESOURCES_DIRECTORY = RESOURCES_DIRECTORY + AUDIO_RESOURCES
const val ORIGINALS_DIRECTORY = RESOURCES_DIRECTORY + AUDIO_RESOURCES + FULL_SONGS_ORIGINALS
const val TEXT_RESOURCES_DIRECTORY = RESOURCES_DIRECTORY + TEXT_RESOURCES
const val CONVERTED_SONGS_TEMP_DIRECTORY = RESOURCES_DIRECTORY + AUDIO_RESOURCES + TEMP_DIRECTORY

val recordedSongsList = listOf<String>(
    "record",
    "rock",
    "pop",
    "japan",
    "in-the-end-acapella",
    "in-the-end-instrumental",
)
