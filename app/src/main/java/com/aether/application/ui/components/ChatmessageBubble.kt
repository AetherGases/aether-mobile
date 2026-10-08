package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.theme.*
import com.aether.application.R

enum class ChatSender { USER, AEKO }

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String
)

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onCopyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (message.sender) {
        ChatSender.USER -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFA37CF6),
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = message.text,
                        style = bodyMediumMd,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        ChatSender.AEKO -> {
            Column(modifier = modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp),
                            ambientColor = purple700.copy(alpha = 0.4f),
                            spotColor = purple700.copy(alpha = 0.3f)
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = message.text,
                        style = bodyMediumMd,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AekoAvatarSmall()

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .clickable(onClick = onCopyClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_copy),
                            contentDescription = "Copiar",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AekoAvatarSmall(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_aeko),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(35.dp).offset(x = 1.dp, y = 3.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
fun ChatMessageBubblePreview() {
    AetherTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatMessageBubble(
                message = ChatMessage("1", ChatSender.USER, "Eu queria saber como faz as coisa")
            )
            ChatMessageBubble(
                message = ChatMessage(
                    "2",
                    ChatSender.AEKO,
                    "Opa, o sistema aqui é inteligente, mas ainda não aprendeu a ler mentes! \uD83E\uDD16\uD83D\uDD2E Me diz aí: o que exatamente você quer resolver hoje?"
                )
            )
        }
    }
}