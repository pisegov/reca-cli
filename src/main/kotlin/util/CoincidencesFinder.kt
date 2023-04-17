package util

import model.Address

class CoincidencesFinder {

    fun addressesCoincidences(first: List<Address>, second: List<Address>) {
        var coincidenceCounter: Int = 0

        println("File addresses list size: ${first.size}")
        println("Micro addresses list size: ${second.size}")
        first.forEachIndexed { index, address ->
            for (i in index until second.size) {
                if (address.hashCode() == second[i].hashCode()) {
                    println(address)
                    println(second[i])
                    println()
                    coincidenceCounter++
                }
            }
        }

        println("Found $coincidenceCounter coincidences")
    }
}