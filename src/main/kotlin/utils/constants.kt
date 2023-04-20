package utils

const val RESOURCES_DIRECTORY = "/home/pmikhail/reca-resources/"
const val AUDIO_RESOURCES = "audio/"
const val SAMPLE_RATE_11025 = "11025/"
const val TEXT_RESOURCES = "txt-data/"
const val FULL_SONGS = "full-songs/converted/"
const val SONG_EXTENSION = ".wav"
const val TEXT_EXTENSION = ".txt"

const val AUDIO_RESOURCES_DIRECTORY = RESOURCES_DIRECTORY + AUDIO_RESOURCES + SAMPLE_RATE_11025
const val FULL_SONGS_DIRECTORY = RESOURCES_DIRECTORY + AUDIO_RESOURCES + FULL_SONGS
const val TEXT_RESOURCES_DIRECTORY = RESOURCES_DIRECTORY + TEXT_RESOURCES

val recordedSongsList = listOf<String>(
    "record",
    "rock",
    "pop",
    "japan",
    "in-the-end-acapella",
    "in-the-end-instrumental",
)
