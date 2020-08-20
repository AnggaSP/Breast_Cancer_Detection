package id.ac.esaunggul.breastcancerdetection.util.dispatcher

import android.content.SharedPreferences
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class SharedPrefDispatcher {

    private val sharedPrefEmitter: EventEmitter<(SharedPreferences) -> Unit> = EventEmitter()
    val sharedPrefCommands: EventSource<(SharedPreferences) -> Unit> = sharedPrefEmitter

    fun emit(command: (SharedPreferences) -> Unit) {
        sharedPrefEmitter.emit(command)
    }
}