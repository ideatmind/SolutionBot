package com.chatai.captionsgenerator

import android.graphics.Bitmap
import com.chatai.captionsgenerator.data.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt : String = "",
    val bitmap: Bitmap? = null
)
