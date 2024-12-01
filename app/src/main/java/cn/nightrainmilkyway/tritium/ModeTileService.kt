package cn.nightrainmilkyway.tritium

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException

@SuppressLint("SdCardPath")
class ModeTileService : TileService() {

    private val tileUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTile()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
        val filter = IntentFilter("cn.nightrainmilkyway.tritium.UPDATE_TILE")
        registerReceiver(tileUpdateReceiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStopListening() {
        super.onStopListening()
        unregisterReceiver(tileUpdateReceiver)
    }

    override fun onClick() {
        super.onClick()
        Log.d("ModeTileService", "Tile clicked")
        val currentMode = readModeFromFile()
        val newMode = when (currentMode) {
            "powersave" -> "balance"
            "balance" -> "performance"
            "performance" -> "fast"
            else -> "powersave"
        }
        setMode(newMode)
        Handler(Looper.getMainLooper()).post {
            updateTile()
            sendBroadcast(Intent("cn.nightrainmilkyway.tritium.UPDATE_TILE").setPackage(packageName))
            sendBroadcast(
                Intent("cn.nightrainmilkyway.tritium.UPDATE_ICON").putExtra(
                    "mode",
                    newMode
                )
            )
        }
    }

    private fun updateTile() {
        val tile = qsTile
        val currentMode = readModeFromFile()
        tile.state = Tile.STATE_ACTIVE
        tile.label = when (currentMode) {
            "powersave" -> "省电模式"
            "balance" -> "均衡模式"
            "performance" -> "性能模式"
            "fast" -> "极速模式"
            else -> "未知模式"
        }
        tile.icon = when (currentMode) {
            "powersave" -> Icon.createWithResource(this, R.drawable.powersave)
            "balance" -> Icon.createWithResource(this, R.drawable.balance)
            "performance" -> Icon.createWithResource(this, R.drawable.performance)
            "fast" -> Icon.createWithResource(this, R.drawable.fast)
            else -> Icon.createWithResource(this, R.drawable.unkonw)
        }
        tile.updateTile()
        Log.d("ModeTileService", "Tile updated: $currentMode")
    }

    private fun setMode(mode: String) {
        writeModeToFile(mode)
    }

    private fun writeModeToFile(mode: String) {
        try {
            val filePath = "/data/data/cn.nightrainmilkyway.tritium/files/binaries/mode.txt"
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "echo $mode > $filePath"))
            process.waitFor()
            Log.d("ModeTileService", "Mode written to file: $mode")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ModeTileService", "IOException: ${e.message}")
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e("ModeTileService", "InterruptedException: ${e.message}")
        }
    }

    private fun readModeFromFile(): String {
        return try {
            val filePath = "/data/data/cn.nightrainmilkyway.tritium/files/binaries/mode.txt"
            val file = File(filePath)
            if (file.exists()) {
                val mode = file.readText().trim()
                Log.d("ModeTileService", "Mode read from file: $mode")
                mode
            } else {
                "balance"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ModeTileService", "IOException: ${e.message}")
            "balance"
        }
    }
}