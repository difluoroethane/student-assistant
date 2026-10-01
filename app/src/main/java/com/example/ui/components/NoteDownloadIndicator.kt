package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.notes.DownloadStatus
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.CatAcademic
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun NoteDownloadProgressIndicator(
    status: DownloadStatus,
    onDownload: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String = "note_download"
) {
    val palette = LocalBrutalPalette.current

    when (status) {
        is DownloadStatus.Idle -> {
            BrutalButton(
                onClick = onDownload,
                backgroundColor = BrutalNeonYellow,
                contentColor = BrutalBlack,
                borderWidth = 2.dp,
                shadowOffset = 3.dp,
                modifier = modifier.testTag("${testTagPrefix}_idle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download File",
                    modifier = Modifier.size(16.dp),
                    tint = BrutalBlack
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DOWNLOAD ATTACHMENT",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                )
            }
        }

        is DownloadStatus.Downloading -> {
            val progress = status.progress
            val hasDeterminateProgress = progress >= 0f

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .border(2.dp, palette.border, RectangleShape)
                    .background(palette.background, RectangleShape)
                    .padding(10.dp)
                    .testTag("${testTagPrefix}_progress_container")
            ) {
                // Header with spinner and progress percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.5.dp,
                            color = BrutalOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasDeterminateProgress) {
                                "DOWNLOADING FROM STORAGE • ${(progress * 100).toInt()}%"
                            } else {
                                "DOWNLOADING FROM STORAGE..."
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = palette.accentOrange
                        )
                    }

                    if (hasDeterminateProgress) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = palette.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Neo-Brutalist Linear Progress Bar
                val infiniteTransition = rememberInfiniteTransition(label = "indeterminate_download")
                val animatedOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bar_offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .border(2.dp, palette.border, RectangleShape)
                        .background(palette.surface, RectangleShape)
                ) {
                    if (hasDeterminateProgress) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = progress.coerceIn(0.04f, 1f))
                                .fillMaxHeight()
                                .background(BrutalNeonYellow, RectangleShape)
                                .border(1.dp, palette.border, RectangleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = 0.4f)
                                .fillMaxHeight()
                                .background(BrutalNeonYellow, RectangleShape)
                                .border(1.dp, palette.border, RectangleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (hasDeterminateProgress) {
                        "Streaming attachment bytes from Firebase Storage..."
                    } else {
                        "Connecting to Firebase Storage bucket..."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = palette.textSecondary
                )
            }
        }

        is DownloadStatus.Completed -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .border(2.dp, palette.border, RectangleShape)
                    .background(Color(0xFFE8F5E9), RectangleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("${testTagPrefix}_completed_container"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = CatAcademic,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ATTACHMENT DOWNLOADED",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Ready to view on device",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                BrutalButton(
                    onClick = onOpen,
                    backgroundColor = BrutalNeonYellow,
                    contentColor = BrutalBlack,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                    modifier = Modifier.testTag("${testTagPrefix}_open_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = BrutalBlack
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "OPEN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }

        is DownloadStatus.Error -> {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .border(2.dp, palette.border, RectangleShape)
                    .background(Color(0xFFFFEBEE), RectangleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("${testTagPrefix}_error_container"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DOWNLOAD FAILED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = status.message,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = palette.textSecondary,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                BrutalButton(
                    onClick = onDownload,
                    backgroundColor = BrutalOrange,
                    contentColor = BrutalWhite,
                    borderWidth = 1.5.dp,
                    shadowOffset = 2.dp,
                    modifier = Modifier.testTag("${testTagPrefix}_retry_button")
                ) {
                    Text(
                        text = "RETRY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}
