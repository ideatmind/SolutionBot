package com.example.captionsgenerator

import android.graphics.Bitmap
import com.example.captionsgenerator.data.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt : String = "",
    val bitmap: Bitmap? = null
)
