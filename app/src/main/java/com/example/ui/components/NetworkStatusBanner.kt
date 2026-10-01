package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.ConnectivityObserver
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.BrutalEmerald
import com.example.ui.theme.BrutalNeonYellow
import com.example.ui.theme.BrutalOrange
import com.example.ui.theme.BrutalWhite
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun NetworkStatusBanner(
    status: ConnectivityObserver.NetworkStatus,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalBrutalPalette.current

    val (bgColor, textColor, statusText, subText, indicatorColor) = when (status) {
        ConnectivityObserver.NetworkStatus.AvailableWifi -> {
            Tuple5(
                if (palette.isDark) Color(0xFF142B1B) else Color(0xFFE8F5E9),
                if (palette.isDark) BrutalEmerald else Color(0xFF1B5E20),
                "[ONLINE • WI-FI]",
                "Connected to campus network",
                BrutalEmerald
            )
        }
        ConnectivityObserver.NetworkStatus.AvailableCellular -> {
            Tuple5(
                if (palette.isDark) Color(0xFF2C2508) else Color(0xFFFFFDE7),
                if (palette.isDark) BrutalNeonYellow else Color(0xFFF57F17),
                "[MOBILE DATA]",
                "Connected via cellular",
                BrutalNeonYellow
            )
        }
        ConnectivityObserver.NetworkStatus.Lost -> {
            Tuple5(
                if (palette.isDark) Color(0xFF33140C) else Color(0xFFFFEBEE),
                if (palette.isDark) Color(0xFFFF8A65) else Color(0xFFB71C1C),
                "[OFFLINE MODE]",
                "Showing saved offline content",
                BrutalOrange
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .border(2.dp, palette.border, RectangleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("network_status_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Blinking/Solid status dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                        .border(1.dp, BrutalBlack, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = textColor
                    )
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = palette.textSecondary
                    )
                }
            }

            // Quick Sync button on banner
            Box(
                modifier = Modifier
                    .border(1.5.dp, palette.border, RectangleShape)
                    .background(if (status.isOnline) BrutalEmerald else palette.surface, RectangleShape)
                    .clickable { onSyncClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("banner_sync_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = if (status.isOnline) BrutalBlack else palette.textPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SYNC",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        ),
                        color = if (status.isOnline) BrutalBlack else palette.textPrimary
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)
