package co.kr.whitewave.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * 앱의 알림 설정 화면으로 이동
 */
fun Context.openNotificationSettings() {
    val intent = Intent().apply {
        when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O -> {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            else -> {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
        }
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
}