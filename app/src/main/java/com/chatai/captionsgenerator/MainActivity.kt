
package com.chatai.captionsgenerator

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.chatai.captionsgenerator.ui.theme.CaptionsGeneratorTheme
import com.chatai.captionsgenerator.ui.theme.poppinsFontFamily
import com.chatai.captionsgenerator.ui.theme.ubuntuFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                uriState.update { uri.toString() }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaptionsGeneratorTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                navigationIcon = {
                                    IconButton(
                                        onClick = {

                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.new_logo),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(100.dp)
                                        )
                                    }
                                },
                                title = {
                                    Text(
                                        text = "Solution Bot",
                                        fontFamily = poppinsFontFamily,
                                        color = Color.Black
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.White
                                )
                            )
                        }
                    ) {
                        ChatScreen(paddingValues = it)
                    }

                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(paddingValues: PaddingValues) {
        val chaViewModel = viewModel<ChatViewModel>()
        val chatState = chaViewModel.chatState.collectAsState().value
        val context = LocalContext.current

        val bitmap = getBitmap()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(painterResource(id = R.drawable.new_logo),
                    contentScale = ContentScale.Inside)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .padding(top = paddingValues.calculateTopPadding())
                    .imePadding(),
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
                                bitmap = chat.bitmap,
                                onClick = {
                                    chaViewModel.onEvent(
                                        ChatUiEvent.CopyPrompt(
                                            chat.prompt,
                                            chat.bitmap
                                        )
                                    )
                                }
                            )
                        } else {
                            ModelChatItem(response = chat.prompt)
                        }
                    }
                }


                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        Modifier.clickable {
                            uriState.update { "" }
                        }
                    ) {
                        bitmap?.let {
                            Image(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(bottom = 2.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentDescription = "picked image",
                                contentScale = ContentScale.Crop,
                                bitmap = it.asImageBitmap()
                            )
                        }

                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest
                                            .Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build()
                                    )
                                },
                            painter = painterResource(R.drawable.addphoto),
                            contentDescription = "Add Photo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                color = Color.Cyan,
                                shape = RoundedCornerShape(55.dp),
                                width = 1.dp
                            ),
                        value = chatState.prompt,
                        onValueChange = {
                            chaViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                        },
                        placeholder = {
                            Text(
                                text = "Ask me anything...\uD83D\uDE03",
                                overflow = TextOverflow.Ellipsis,
                                color = Color.Black,
                                fontFamily = ubuntuFontFamily
                            )
                        },
                        maxLines = 7,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            unfocusedTextColor = Color.Black,
                            focusedTextColor = Color.Black,
                            selectionColors = TextSelectionColors(Color.Black, Color.White)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (chatState.prompt.isEmpty()) {
                                    Toast
                                        .makeText(context, "Empty message", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    chaViewModel.onEvent(
                                        ChatUiEvent.SendPrompt(
                                            chatState.prompt,
                                            bitmap
                                        )
                                    )
                                    uriState.update { "" }
                                }
                            },
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Send prompt",
                        tint = MaterialTheme.colorScheme.primary
                    )

                }

            }
        }
    }

    @Composable
    private fun getBitmap(): Bitmap? {
        val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }

        return null
    }

}
