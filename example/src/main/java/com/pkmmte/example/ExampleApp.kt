package com.pkmmte.example

import android.app.Application
import timber.log.Timber

/**
 * Created on June 29, 2017
 *
 * @author Pkmmte Xeleon
 */
class ExampleApp : Application() {
	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())
	}
}
