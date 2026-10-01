package com.example.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.example.ui.components.BrutalButton
import com.example.ui.components.BrutalCard
import com.example.ui.components.BrutalTag
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.LocalBrutalPalette
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

data class AppUpdateInfo(
    val latestVersionCode: Int,
    val latestVersionName: String,
    val changelog: String,
    val apkUrl: String,
    val isUpdateAvailable: Boolean = false
)

object AppUpdateChecker {
    private const val TAG = "AppUpdateChecker"

    suspend fun checkForUpdates(firestore: FirebaseFirestore?): AppUpdateInfo? {
        val fs = firestore ?: return null
        return try {
            withTimeoutOrNull(4000L) {
                val doc = fs.collection("app_config").document("updates").get().await()
                if (doc != null && doc.exists()) {
                    val latestVersionCode = doc.getLong("latestVersionCode")?.toInt() ?: 0
                    val latestVersionName = doc.getString("latestVersionName") ?: "1.0"
                    val changelog = doc.getString("changelog") ?: "Performance enhancements and bug fixes."
                    val apkUrl = doc.getString("apkUrl") ?: ""

                    val currentVersionCode = BuildConfig.VERSION_CODE
                    Log.d(TAG, "Update check: Current=$currentVersionCode, Latest=$latestVersionCode, Apk=$apkUrl")

                    if (latestVersionCode > currentVersionCode && apkUrl.isNotBlank()) {
                        AppUpdateInfo(
                            latestVersionCode = latestVersionCode,
                            latestVersionName = latestVersionName,
                            changelog = changelog,
                            apkUrl = apkUrl,
                            isUpdateAvailable = true
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: FirebaseFirestoreException) {
            // Normal behavior when device or Firestore client is offline or unavailable
            Log.d(TAG, "Skipping update check (client offline/unavailable): ${e.message}")
            null
        } catch (e: Exception) {
            Log.d(TAG, "Update check skipped: ${e.message}")
            null
        }
    }

    fun downloadAndInstall(
        context: Context,
        updateInfo: AppUpdateInfo,
        onDownloadStarted: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val fileName = "student_assistant_v${updateInfo.latestVersionName}.apk"
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val destinationFile = File(downloadsDir, fileName)

            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            val request = DownloadManager.Request(Uri.parse(updateInfo.apkUrl))
                .setTitle("Downloading Student Assistant update")
                .setDescription("Version ${updateInfo.latestVersionName}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(destinationFile))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadId = downloadManager.enqueue(request)
            onDownloadStarted()

            val onCompleteReceiver = object : BroadcastReceiver() {
                override fun onReceive(recvContext: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                    if (id == downloadId) {
                        try {
                            recvContext?.unregisterReceiver(this)
                        } catch (ignored: Exception) {}

                        launchInstaller(context, destinationFile)
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                onCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_EXPORTED
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initiate download: ${e.message}")
            onError(e.localizedMessage ?: "Failed to initiate update download")
        }
    }

    fun launchInstaller(context: Context, apkFile: File) {
        try {
            if (!apkFile.exists()) {
                Log.w(TAG, "Target APK file not found at ${apkFile.absolutePath}")
                return
            }

            val authority = "${context.packageName}.fileprovider"
            val apkUri = FileProvider.getUriForFile(context, authority, apkFile)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(settingsIntent)
                }
            }

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch package installer: ${e.message}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAvailableDialog(
    updateInfo: AppUpdateInfo,
    isDownloading: Boolean,
    onDownloadAndInstall: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalBrutalPalette.current

    BasicAlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(3.dp, palette.border, RectangleShape)
                .testTag("update_available_dialog"),
            color = palette.surface,
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = BrutalOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "UPDATE AVAILABLE",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                    }
                    if (!isDownloading) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = palette.textPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "VERSION ${updateInfo.latestVersionName}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = palette.accentOrange
                    )
                    BrutalTag(
                        text = "BUILD ${updateInfo.latestVersionCode}",
                        backgroundColor = BrutalNeonYellow,
                        textColor = BrutalBlack,
                        shadowOffset = 2.dp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Current: v${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = palette.textSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Changelog Neo-Brutalist Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.background)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            "WHAT'S NEW",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            ),
                            color = palette.accentOrange
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = updateInfo.changelog,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = palette.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isDownloading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, palette.border, RectangleShape)
                            .background(BrutalNeonYellow)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.5.dp,
                            color = BrutalBlack
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "DOWNLOADING APK...",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = BrutalBlack
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BrutalButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            backgroundColor = palette.surface,
                            contentColor = palette.textPrimary,
                            borderWidth = 2.dp
                        ) {
                            Text(
                                "LATER",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                            )
                        }

                        BrutalButton(
                            onClick = onDownloadAndInstall,
                            modifier = Modifier
                                .weight(2f)
                                .testTag("download_and_install_button"),
                            backgroundColor = BrutalOrange,
                            contentColor = BrutalWhite,
                            borderWidth = 2.dp
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = BrutalWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "DOWNLOAD & INSTALL",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
