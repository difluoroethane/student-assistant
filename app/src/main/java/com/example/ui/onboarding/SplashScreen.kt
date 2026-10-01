package com.example.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTimeout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "STUDENT\nASSISTANT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = com.example.ui.theme.BrutalBlack,
                    offset = androidx.compose.ui.geometry.Offset(6f, 6f),
                    blurRadius = 0f
                )
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "A CALEB AND SAMUEL PRODUCTION",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .background(androidx.compose.ui.graphics.Color.White)
                .border(2.dp, com.example.ui.theme.BrutalBlack)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            color = com.example.ui.theme.BrutalBlack
        )
    }
}
