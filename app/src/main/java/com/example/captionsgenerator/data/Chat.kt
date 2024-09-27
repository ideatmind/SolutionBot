package com.example.captionsgenerator.data

import android.graphics.Bitmap

data class Chat(
    val prompt: String,
    var bitmap: Bitmap?,
    val isFromUser: Boolean
)
