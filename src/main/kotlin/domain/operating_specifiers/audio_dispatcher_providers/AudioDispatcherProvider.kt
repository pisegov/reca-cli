package domain.operating_specifiers.audio_dispatcher_providers

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioProcessor

interface AudioDispatcherProvider {
    fun addProcessorsAndStartDispatcher(processors: List<AudioProcessor>)
}