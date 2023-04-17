import domain.ioproviders.FileInputOutputProvider
import domain.SampleAnalyzer
import util.CoincidencesFinder
import util.Recorder

fun recordSong(recorder: Recorder, title: String = "record.wav") {
    println("Press enter to start recording $title")
    readln()
    recorder.recordSample(title)
}

fun main(args: Array<String>) {

    val sampleAnalyzer = SampleAnalyzer(1024, 11025F)

    val fileAddresses = sampleAnalyzer.getHashesFromSample(
        FileInputOutputProvider("japan-orig.wav", "data.txt")
//        FileInputOutputProvider("out.wav", "data.txt")
    )
    val microAddresses = sampleAnalyzer.getHashesFromSample(
//        MicrophoneInputOutputProvider("micro.txt")
//        FileInputOutputProvider("in-the-end-a-capella.wav", "micro.txt")
        FileInputOutputProvider("japan.wav", "micro.txt")
    )
    CoincidencesFinder().addressesCoincidences(fileAddresses, microAddresses)


//    val songsList = listOf<String>(
//        "record",
//        "rock",
//        "pop",
//        "japan",
//        "in-the-end-a-capella",
//        "in-the-end-instrumental",
//    )

//    var i = 0
//    songsList.map { song ->
//        sampleAnalyzer.getHashesFromSample(
//            FileInputOutputProvider("$song.wav", "$song.txt")
//        )
//    }.forEach { songAddressesList ->
//        println("\n${songsList[i++]}")
//        CoincidencesFinder().addressesCoincidences(fileAddresses, songAddressesList)
//    }

//        val recorder = util.Recorder(11025F)
//    songsList.forEach { song ->
//        recordSong(recorder, song)
//
//    }
}