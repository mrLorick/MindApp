package com.mindbyromanzanoni.genrics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object RunInScope {

    fun mainThread(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
        work()
    }

    fun ioThread(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
        work()
    }

    fun unconfinedThread(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Unconfined).launch {
        work()
    }
}



