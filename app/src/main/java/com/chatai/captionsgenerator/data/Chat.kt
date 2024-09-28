package com.chatai.captionsgenerator.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Chat(
    val prompt: String,
    var bitmap: Bitmap?,
    val isFromUser: Boolean
)

val beautifulColors = listOf(
    Color(0xFFFFCDD2), // Red 100
    Color(0xFFF8BBD0), // Pink 100
    Color(0xFFE1BEE7), // Purple 100
    Color(0xFFD1C4E9), // Deep Purple 100
    Color(0xFFC5CAE9), // Indigo 100
    Color(0xFFBBDEFB), // Blue 100
    Color(0xFFB3E5FC), // Light Blue 100
    Color(0xFFB2EBF2), // Cyan 100
    Color(0xFFB2DFDB), // Teal 100
    Color(0xFFC8E6C9), // Green 100
    Color(0xFFDCEDC8), // Light Green 100
    Color(0xFFF0F4C3), // Lime 100
    Color(0xFFFFF9C4), // Yellow 100
    Color(0xFFFFECB3), // Amber 100
    Color(0xFFFFE0B2), // Orange 100
    Color(0xFFFFCCBC), // Deep Orange 100
    Color(0xFFD7CCC8), // Brown 100
    Color(0xFFCFD8DC)  // Blue Grey 100
)

// Function to get a random color from the list
fun getRandomColor(): Color {
    return beautifulColors[Random.nextInt(beautifulColors.size)]
}
