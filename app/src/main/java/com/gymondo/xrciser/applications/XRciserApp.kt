package com.gymondo.xrciser.applications

import android.app.Activity
import android.app.Application
import android.content.Context

class XRciserApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {

        lateinit var appContext : Context
            private set

        lateinit var currentActivity: Activity
    }
}