package id.ac.esaunggul.breastcancerdetection.util.dispatcher

import androidx.annotation.MenuRes
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class MenuInflaterDispatcher {

    private val menuInflaterEmitter: EventEmitter<Int> = EventEmitter()
    val menuInflaterParam: EventSource<Int> = menuInflaterEmitter

    fun emit(@MenuRes resId: Int) {
        menuInflaterEmitter.emit(resId)
    }
}