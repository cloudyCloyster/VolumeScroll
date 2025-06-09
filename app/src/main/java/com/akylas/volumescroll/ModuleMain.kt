package com.akylas.volumescroll

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.akylas.volumescroll.utils.Preferences
import com.akylas.volumescroll.utils.SystemProperties

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.callMethod
import kotlin.text.split

const val TAG = "com.akylas.volumescroll"
class ModuleMain : IXposedHookLoadPackage {


    var _appContext: Context? = null
    val appContext: Context
        get() {
            if (_appContext == null) {
                _appContext = AndroidAppHelper.currentApplication()

            }

            return _appContext!!
        }

    companion object {
        var currentAppName: String? = null
        var volumeMode: Boolean = false
    }

    @SuppressLint("NewApi")
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        Log.i("handleLoadPackage " + lpparam.packageName + " " + AndroidAppHelper.currentApplication())
        if (lpparam.packageName == "android") {
//            val ignoredPackages = listOf<String>("com.android.systemui")
//            findMethod( findClass(
//                "android.accessibilityservice.IAccessibilityServiceClient\$Stub\$Proxy",
//                lpparam.classLoader
//            )) { name == "onAccessibilityEvent" }
//                .hookBefore {
//                    val type = callMethod(it.args[0],"getEventType" )
//                    if (type ==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//                        val newAppName =callMethod(it.args[0],"getPackageName" ) as String?
//                        if (!ignoredPackages.contains(newAppName)) {
//                            currentAppName =newAppName
//                            Log.i("setPackageName $currentAppName")
//                            val prefs = Preferences()
//                            var enabled_apps = prefs.getString("enabled_apps", "").split(",").toList()
//                                .filter { it.isNotEmpty() }
//                            Log.i("enabled_apps $enabled_apps")
//                            volumeMode = enabled_apps.contains(currentAppName)
//                            Log.i("volumeMode $volumeMode")
//                        }
//
//
//                    }
//                }

        } else {
            findMethod( findClass(
                "android.app.Activity",
                lpparam.classLoader
            )) { name == "dispatchKeyEvent" }
                .hookBefore() {
                    val wakeOnVolume = SystemProperties.get("sys.wakeup_on_volume")
                    val mPowerManager =
                        appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                    val isLocked = mPowerManager?.isInteractive != true
                    Log.i("dispatchKeyEvent ${it.args[0]} wakeOnVolume:$wakeOnVolume volumeMode:$volumeMode")
                    if (isLocked && ("1" == wakeOnVolume || volumeMode)) {
                        val keyEvent = it.args[0] as KeyEvent
                        val action = keyEvent.action;
                        val keyCode = keyEvent.keyCode;
                        if (action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                            val intent = Intent("AKYLAS_VOLUME_SCROLL_KEY")
                            intent.putExtra("keyCode", keyCode)
                            appContext.sendBroadcast(intent)
                            it.result = true
//                            ScrollAccessibilityService.getSharedInstance().handleVolumeKeys(keyCode)
                        }
                    }


                }

        }
    }
}
