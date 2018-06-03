package io.erva.lund

import android.annotation.SuppressLint
import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        timber()
    }

    private fun timber() {
        Timber.plant(
                object : Timber.DebugTree() {
                    @SuppressLint("DefaultLocale")
                    override fun createStackElementTag(element: StackTraceElement): String? {
                        return String.format("@@ %s.%s:%d thread[%s]",
                                super.createStackElementTag(element),
                                element.methodName, element.lineNumber, Thread.currentThread().name)
                    }

                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        if (BuildConfig.DEBUG)
                            super.log(priority, tag, message, t)
                    }
                })
    }
}