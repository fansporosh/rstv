package bd.com.porosh.rsporoshtv

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : Activity() {
    companion object {
        private const val STREAM_URL = "https://rs.porosh.com.bd/tv.m3u8"
        private const val PREFS = "rsporoshtv_preferences"
        private const val BOOT_PREF = "auto_start_on_boot"
    }

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var status: TextView
    private lateinit var settingsPanel: View
    private lateinit var autoStart: CheckBox
    private val handler = Handler(Looper.getMainLooper())
    private var retryCount = 0
    private var released = false
    private val retryRunnable = Runnable { if (!released) startStream() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.playerView)
        status = findViewById(R.id.statusText)
        settingsPanel = findViewById(R.id.settingsPanel)
        autoStart = findViewById(R.id.autoStartCheck)
        autoStart.isChecked = getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(BOOT_PREF, false)
        autoStart.setOnCheckedChangeListener { _, checked ->
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(BOOT_PREF, checked).apply()
        }
        findViewById<Button>(R.id.retryButton).setOnClickListener { retryCount = 0; startStream() }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { toggleSettings() }
        findViewById<Button>(R.id.closeSettingsButton).setOnClickListener { settingsPanel.visibility = View.GONE }
        playerView.setOnClickListener { toggleSettings() }
    }

    override fun onStart() {
        super.onStart()
        released = false
        if (player == null) {
            player = ExoPlayer.Builder(this).build().also { exo ->
                playerView.player = exo
                exo.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> status.text = getString(R.string.buffering)
                            Player.STATE_READY -> { status.visibility = View.GONE; retryCount = 0 }
                            Player.STATE_ENDED -> scheduleRetry("Live stream ended")
                        }
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        scheduleRetry("Stream unavailable: ${error.errorCodeName}")
                    }
                })
            }
            startStream()
        }
    }

    private fun startStream() {
        handler.removeCallbacks(retryRunnable)
        if (!networkAvailable()) {
            scheduleRetry(getString(R.string.no_internet))
            return
        }
        status.visibility = View.VISIBLE
        status.text = getString(R.string.connecting)
        player?.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(STREAM_URL))
            prepare()
            playWhenReady = true
        }
    }

    private fun scheduleRetry(message: String) {
        handler.removeCallbacks(retryRunnable)
        retryCount++
        val delay = (2000L * (1L shl minOf(retryCount - 1, 4))).coerceAtMost(30000L)
        status.visibility = View.VISIBLE
        status.text = "$message\n${getString(R.string.retrying)} ${delay / 1000}s"
        handler.postDelayed(retryRunnable, delay)
    }

    private fun networkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun toggleSettings() {
        settingsPanel.visibility = if (settingsPanel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        if (settingsPanel.visibility == View.VISIBLE) findViewById<Button>(R.id.retryButton).requestFocus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_SETTINGS) {
            toggleSettings()
            return true
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && settingsPanel.visibility == View.VISIBLE) {
            settingsPanel.visibility = View.GONE
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
        released = true
        handler.removeCallbacks(retryRunnable)
        playerView.player = null
        player?.release()
        player = null
        super.onStop()
    }
}
