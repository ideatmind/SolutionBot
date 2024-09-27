package com.example.captionsgenerator

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.captionsgenerator.ui.theme.CaptionsGeneratorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                uriState.update { uri.toString() }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaptionsGeneratorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold (
                        topBar = {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary)
                                    .height(55.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    text = stringResource(R.string.app_name),
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    ){
                        ChatScreen(paddingValues = it)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(paddingValues: PaddingValues) {
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitMap()

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->
                    if (chat.isFromUser) {
                        UserChatItem(
                            prompt = chat.prompt,
                            bitmap = chat.bitmap
                        )
                    } else {
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.5f)
                ) {
                    bitmap?.let {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(bottom = 1.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap()
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        painter = painterResource(R.drawable.addphoto),
                        contentDescription = "Add photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(8.5.dp))

                Box(
                    modifier = Modifier
                        .weight(8f)
                        .clip(RoundedCornerShape(14.dp)) // Set the desired corner radius
                        .border(
                            width = 1.dp,
                            color = Color.Cyan,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    OutlinedTextField(
                        value = chatState.prompt,
                        onValueChange = {
                            chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                        },
                        textStyle = TextStyle(
                            color = Color.Black
                        ),
                        placeholder = {
                            Text(
                                text = "Type a prompt",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        maxLines = 7,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent, // Set to transparent to avoid double border
                            unfocusedBorderColor = Color.Transparent // Set to transparent to avoid double border
                        )
                    )
                }



                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .weight(2f)
                        .clickable {
                            chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                        },
                    painter = painterResource(R.drawable.send),
                    contentDescription = "Send Prompt",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }




    @Composable
    fun UserChatItem(prompt: String, bitmap: Bitmap?) {
        val backgroundColor = getRandomColor()
        Column (
            modifier = Modifier.padding(start = 100.dp, bottom = 20.dp).background(backgroundColor)
        ) {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(bottom = 1.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(16.dp),
                text = prompt,
                fontSize = 17.sp,
                color = Color.Black
            )
        }
    }


    @Composable
    fun ModelChatItem(response: String) {
        val backgroundColor = getRandomColor()
        Column (
            modifier = Modifier.padding(end = 100.dp, bottom = 20.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .padding(16.dp),
                text = response,
                fontSize = 17.sp,
                color = Color.Black
            )
        }
    }


    @Composable
    private fun getBitMap(): Bitmap? {
            val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state

        if(imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }
        return null
    }

}


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