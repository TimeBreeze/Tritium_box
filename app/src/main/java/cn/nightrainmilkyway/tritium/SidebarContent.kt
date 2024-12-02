package cn.nightrainmilkyway.tritium

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@SuppressLint("SdCardPath")
@Composable
fun SidebarContent(context: Context, isSwitchChecked: MutableState<Boolean>) {
    val versionName = getVersionName(context)
    val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(
                color = Color(0xFFfff8f7),
                RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "设置",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.Black),
                modifier = Modifier
                    .padding(start = 16.dp, top = 32.dp)
                    .padding(bottom = 16.dp)
            )

            IconTitleBox(
                icon = R.drawable.settings_24dp_5f6368,
                title = "执行脚本",
                description = "使app服务存活",
                backgroundColor = Color(0xFFfdf2f0),
                showSwitch = true,
                initialSwitchChecked = isSwitchChecked.value,
                onSwitchChanged = { newChecked ->
                    isSwitchChecked.value = newChecked
                    sharedPreferences.edit().putBoolean("setting", newChecked).apply()
                    if (newChecked) {
                        setFilePermissionsAndRunScript("/data/data/cn.nightrainmilkyway.tritium/files/scripts/keep_alive.sh")
                    } else {
                        // Handle the case when the switch is turned off, if needed
                    }
                },
                iconBackgroundColor = Color(0xFFf7e3e1),
                preferenceKey = "setting",
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.view_module_24dp_5f6368,
                title = "附加模块",
                description = "将附加模块输出到DownLoad",
                backgroundColor = Color(0xFFfdf2f0),
                showSwitch = true,
                initialSwitchChecked = false,
                onSwitchChanged = { newChecked ->
                    println("Switch is now: $newChecked")
                },
                iconBackgroundColor = Color(0xFFf7e3e1),
                preferenceKey = "setting2",
                scriptPath = "/data/data/cn.nightrainmilkyway.tritium/files/scripts/keep_alive.sh"
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.verified_24dp_5f6368,
                title = "版本号",
                description = versionName,
                backgroundColor = Color(0xFFfdf2f0),
                preferenceKey = "settings_switch_state"
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.github,
                title = "源代码",
                description = "获取最新更新，讨论问题等",
                backgroundColor = Color(0xFFfdf2f0),
                url = "https://github.com/TimeBreeze/Tritium_box",
                preferenceKey = "settings_switch_state"
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.help_center_24dp_5f6368,
                title = "文档",
                description = "阅读文档解决问题",
                backgroundColor = Color(0xFFfdf2f0),
                url = "https://tritium.nightrainmilkyway.cn",
                preferenceKey = "settings_switch_state"
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.error_outline_24dp_5f6368,
                title = "错误",
                description = "请使用GitHub提issues",
                backgroundColor = Color(0xFFfdf2f0),
                url = "https://github.com/TimeBreeze/Tritium/issues/new?assignees=&labels=&projects=&template=bug-%E5%8F%8D%E9%A6%88.md&title=",
                preferenceKey = "settings_switch_state"
            )
            Spacer(modifier = Modifier.height(16.dp))
            IconTitleBox(
                icon = R.drawable.group_add_24dp_5f6368,
                title = "群组",
                description = "交流探讨",
                backgroundColor = Color(0xFFfdf2f0),
                url = "https://qm.qq.com/q/8Vsq7Cn224",
                preferenceKey = "settings_switch_state"
            )
        }
    }
}

fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "未知版本"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "未知版本"
    }
}

fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

fun isAccessibilityServiceEnabled(
    context: Context,
    service: Class<out AccessibilityService>
): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals(
                ComponentName(context, service).flattenToString(),
                ignoreCase = true
            )
        ) {
            return true
        }
    }
    return false
}

@Preview
@Composable
fun SidebarPreview() {
    SidebarContent(
        context = LocalContext.current,
        isSwitchChecked = remember { mutableStateOf(false) })
}