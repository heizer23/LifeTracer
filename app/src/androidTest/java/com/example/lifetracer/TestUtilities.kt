package com.example.lifetracer

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TestUtilities

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)

        val observer = Observer<T> {
            data[0] = it
            latch.countDown()
        }

        ArchTaskExecutor.getInstance().executeOnMainThread {
            liveData.observeForever(observer)
        }

        try {
            latch.await(2, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        ArchTaskExecutor.getInstance().executeOnMainThread {
            liveData.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}