package dev.iconpln.mims

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.StorageUtils
import dev.iconpln.mims.utils.StorageUtils.isPermissionAllowed

class MyApplication : MultiDexApplication() {
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession? = null

    override fun onCreate() {
        super.onCreate()
        if (isPermissionAllowed(this)) {
            val database =
                StorageUtils.getDirectory(StorageUtils.DIRECTORY_DATABASE) + "/" + Config.DATABASE_NAME
            val helper =
                dev.iconpln.mims.data.local.database.DaoMaster.DevOpenHelper(this, database)
            val db = helper.writableDb
            daoSession = dev.iconpln.mims.data.local.database.DaoMaster(db).newSession()
            dev.iconpln.mims.data.local.database_local.DatabaseReport.getDatabase(this)
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val currentOrientation = resources.configuration.orientation
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}