package com.flamyoad.tsukiviewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flamyoad.tsukiviewer.ui.TsukiViewerApp

/**
 * Main entry point Activity for the Compose-based UI.
 * 
 * This Activity hosts the entire Compose UI through [TsukiViewerApp].
 * To use this as your main activity, update the AndroidManifest.xml
 * to make this the launcher activity.
 * 
 * Example AndroidManifest.xml change:
 * ```xml
 * <activity
 *     android:name=".ComposeMainActivity"
 *     android:exported="true"
 *     android:theme="@style/Theme.TsukiViewer">
 *     <intent-filter>
 *         <action android:name="android.intent.action.MAIN" />
 *         <category android:name="android.intent.category.LAUNCHER" />
 *     </intent-filter>
 * </activity>
 * ```
 */
class ComposeMainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TsukiViewerApp(
                onRestartApp = { restartApp() }
            )
        }
    }
    
    private fun restartApp() {
        val intent = Intent(this, LauncherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
