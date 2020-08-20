package id.ac.esaunggul.breastcancerdetection.util.dispatcher

import androidx.navigation.NavController
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class NavigationDispatcher {

    private val navigationEmitter: EventEmitter<(NavController) -> Unit> = EventEmitter()
    val navigationCommands: EventSource<(NavController) -> Unit> = navigationEmitter

    fun emit(command: (NavController) -> Unit) {
        navigationEmitter.emit(command)
    }
}