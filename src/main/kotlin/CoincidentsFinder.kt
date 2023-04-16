import model.Address
import java.io.File

class CoincidentsFinder {

    fun hashCoincidences() {
        val fileHashs = File("fileHashs.txt").readLines().map { it.trim().toLong() }
        val microHashs = File("microHashs.txt").readLines().map { it.trim().toLong() }

        var coincidenceCounter: Int = 0
        fileHashs.forEachIndexed { index, hash ->
            for (i in index until microHashs.size) {
                if (hash == microHashs[i]) coincidenceCounter++
            }
        }

        println("Found $coincidenceCounter coincidences")
    }

    fun addressesCoincidences(first: List<Address>, second: List<Address>) {
        var coincidenceCounter: Int = 0

        val f = first.map { it.hashCode() }
        val s = first.map { it.hashCode() }
        println("File addresses list size: ${first.size}")
        println("Micro addresses list size: ${second.size}")
        first.forEachIndexed { index, address ->
            for (i in index until second.size) {
                if (address.hashCode() == second[i].hashCode()) {
//                if (address == second[i]) {
                    println(address)
                    println(second[i])
                    coincidenceCounter++
                }
            }
        }

        println("Found $coincidenceCounter coincidences")
    }

}