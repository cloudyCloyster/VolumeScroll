package com.akylas.volumescroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import com.akylas.volumescroll.utils.registerReceiver
import java.util.Arrays
import java.util.HashSet

class ScrollAccessibilityService : AccessibilityService() {
    private var preferences: SharedPreferences? = null
    private var enabledApps: HashSet<String>? = null
    private var disabledApps: HashSet<String>? = null

    private val prefKey = "enabled_apps"

    private var listening = false

    @SuppressLint("WorldReadableFiles")
    override fun onCreate() {
        super.onCreate()
//        sharedInstance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences = this.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE)
        preferences!!.registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, s: String? -> loadApps() })
        loadApps()

        val intentFilter = IntentFilter()
        intentFilter.addAction("AKYLAS_VOLUME_SCROLL_KEY")
        this.registerReceiver(intentFilter) { intent ->
//            Log.i("AKYLAS_VOLUME_SCROLL_KEY ${intent}")
            if (intent?.action == "AKYLAS_VOLUME_SCROLL_KEY") {
                handleVolumeKeys(intent.getIntExtra("keyCode", KeyEvent.KEYCODE_VOLUME_DOWN))
            }
        }
    }


    private fun loadApps() {
        enabledApps = preferences!!.getString("enabled_apps", "")!!.split(",").toHashSet()
        disabledApps = preferences!!.getString("disabled_apps", "")!!.split(",").toHashSet()
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            val packageName = event.getPackageName().toString()
            if (!disabledApps!!.contains(packageName)) {
                listening = enabledApps!!.contains(packageName)
            }
        }
    }

    private fun findScrollableNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isScrollable()) {
            return node
        }
        for (i in 0 until node.getChildCount()) {
            val child = node.getChild(i)
            if (child != null) {
                val result = findScrollableNode(child)
                if (result != null) return result
            }
        }
        return null
    }

    //    @Override
    //    public boolean onKeyEvent(KeyEvent event) {
    //        int action = event.getAction();
    //        int keyCode = event.getKeyCode();
    //        boolean wakeOnVolume = SystemProperties.get("sys.wakeup_on_volume").equals("1");
    //            if (wakeOnVolume && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
    //                if (action == KeyEvent.ACTION_DOWN) {
    //                    handleVolumeKeys(keyCode);
    //
    //                }
    //                return true;
    //            }
    //            return super.onKeyEvent(event);
    //    }
    fun handleVolumeKeys(keyCode: Int) {
        val rootNode = getRootInActiveWindow()
        if (rootNode == null) return

        val scrollableNode = findScrollableNode(rootNode)
        if (scrollableNode != null) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                scrollableNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            }
        }
    }

    override fun onInterrupt() {}

    companion object {
//        var sharedInstance: ScrollAccessibilityService? = null
//
//        fun getSharedInstance(): ScrollAccessibilityService? {
//            return sharedInstance
//        }


        @JvmStatic
         fun isAccessibilityServiceEnabled(
            context: Context,
            service: Class<out AccessibilityService?>
        ): Boolean {
            val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices =
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            val expectedComponentName = ComponentName(context, service).flattenToString()

            for (serviceInfo in enabledServices) {
                if (expectedComponentName == serviceInfo.getId()) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun openAccessibilitySettings(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
    }
}
