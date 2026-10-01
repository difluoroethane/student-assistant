package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrutalBlack
import com.example.ui.theme.LocalBrutalPalette

@Composable
fun BrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color? = null,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    content: @Composable RowScope.() -> Unit
) {
    val palette = LocalBrutalPalette.current
    val effectiveContentColor = contentColor ?: if (palette.isDark) Color.White else BrutalBlack
    val borderColor = palette.border
    val shadowColor = palette.shadow

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentShadow = if (isPressed) 0.dp else shadowOffset
    val paddingEnd = if (isPressed) 0.dp else shadowOffset
    val paddingBottom = if (isPressed) 0.dp else shadowOffset

    Box(
        modifier = modifier
            .padding(bottom = paddingBottom, end = paddingEnd)
            .drawBehind {
                if (!isPressed && currentShadow > 0.dp) {
                    drawRect(
                        color = shadowColor,
                        topLeft = Offset(currentShadow.toPx(), currentShadow.toPx()),
                        size = Size(size.width, size.height)
                    )
                }
            }
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.border(borderWidth, borderColor, RectangleShape),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = effectiveContentColor
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    shadowColor: Color? = null,
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 5.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val palette = LocalBrutalPalette.current
    val effectiveBg = backgroundColor ?: palette.surface
    val effectiveBorder = borderColor ?: palette.border
    val effectiveShadow = shadowColor ?: palette.shadow

    var boxModifier = modifier
        .padding(bottom = shadowOffset, end = shadowOffset)
        .drawBehind {
            if (shadowOffset > 0.dp) {
                drawRect(
                    color = effectiveShadow,
                    topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
                    size = Size(size.width, size.height)
                )
            }
        }

    if (onClick != null) {
        boxModifier = boxModifier.clickable(onClick = onClick)
    }

    Box(modifier = boxModifier) {
        Card(
            modifier = Modifier.border(borderWidth, effectiveBorder, RectangleShape),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = effectiveBg
            )
        ) {
            content()
        }
    }
}

@Composable
fun BrutalTag(
    text: String,
    backgroundColor: Color,
    textColor: Color? = null,
    modifier: Modifier = Modifier,
    shadowOffset: Dp = 0.dp
) {
    val palette = LocalBrutalPalette.current
    val effectiveBorder = palette.border
    val effectiveShadow = palette.shadow
    val effectiveTextColor = textColor ?: if (backgroundColor == BrutalBlack) Color.White else BrutalBlack

    var boxModifier = modifier

    if (shadowOffset > 0.dp) {
        boxModifier = boxModifier
            .padding(bottom = shadowOffset, end = shadowOffset)
            .drawBehind {
                drawRect(
                    color = effectiveShadow,
                    topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
                    size = Size(size.width, size.height)
                )
            }
    }

    Box(
        modifier = boxModifier
            .border(2.dp, effectiveBorder, RectangleShape)
            .background(backgroundColor, RectangleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
            color = effectiveTextColor
        )
    }
}

@Composable
fun BrutalBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = BrutalBlack,
    onClick: (() -> Unit)? = null
) {
    val palette = LocalBrutalPalette.current
    var boxMod = modifier
        .border(2.dp, palette.border, RectangleShape)
        .background(backgroundColor, RectangleShape)
        .padding(horizontal = 10.dp, vertical = 4.dp)

    if (onClick != null) {
        boxMod = boxMod.clickable(onClick = onClick)
    }

    Box(modifier = boxMod) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}
