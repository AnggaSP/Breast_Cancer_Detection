package id.ac.esaunggul.breastcancerdetection.util.dispatcher

import androidx.annotation.StringRes
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class ToastDispatcher {

    private val toastEmitter: EventEmitter<Int> = EventEmitter()
    val toastParam: EventSource<Int> = toastEmitter

    fun emit(@StringRes resId: Int) {
        toastEmitter.emit(resId)
    }
}