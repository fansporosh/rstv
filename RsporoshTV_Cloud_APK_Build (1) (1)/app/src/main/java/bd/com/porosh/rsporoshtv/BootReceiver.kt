package bd.com.porosh.rsporoshtv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/** Best-effort only. Modern Android may prohibit background activity launches after boot.
 *  Reliable boot-to-TV requires a managed kiosk/device-owner or OEM launcher integration.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val enabled = context.getSharedPreferences("rsporoshtv_preferences", Context.MODE_PRIVATE)
            .getBoolean("auto_start_on_boot", false)
        if (!enabled) return
        // Background activity launch restrictions apply on Android 10+.
        // Do not attempt to bypass the OS restrictions or start a foreground service.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i("RsporoshTV", "Boot detected; OS-managed launcher/kiosk is required for guaranteed auto-launch")
            return
        }
        try {
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.w("RsporoshTV", "Boot launch not permitted", e)
        }
    }
}
