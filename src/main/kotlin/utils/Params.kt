package utils

import constants.NUMBER_OF_PEAKS
import constants.REFERENCE_PEAK_DISTANCE
import constants.TARGET_AREA_SIZE

data class Params(
//    val peakFindingRange: ClosedFloatingPointRange<Float> = PEAK_FINDING_RANGE,
    val numberOfPeaks: Int = NUMBER_OF_PEAKS,
    val referencePeakDistance: Int = REFERENCE_PEAK_DISTANCE,
    val targetAreaSize: Int = TARGET_AREA_SIZE,
) {
    val testOutputFileName: String
        get() = "${numberOfPeaks}n-${referencePeakDistance}d-${targetAreaSize}ta"
}