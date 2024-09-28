package com.example.captionsgenerator.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    private const val api_key = "AIzaSyBb29lQ49kbmOOwXW-6GtEaFqxs7-OM5ss"

    suspend fun getResponse(prompt: String): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = api_key
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }


    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = api_key
        )
        try {
            val inputContent = content {
                image(bitmap)
                mutableListOf(image(bitmap))
                text(prompt)
            }
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}