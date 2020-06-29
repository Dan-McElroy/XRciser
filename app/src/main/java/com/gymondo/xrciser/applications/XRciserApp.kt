package com.gymondo.xrciser.applications

import android.app.Application
import android.content.Context

class XRciserApp : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {

        lateinit var context : Context
            private set
    }
}