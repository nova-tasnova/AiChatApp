package com.example.aichatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.aichatapp.Message




class ChatActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ChatScreen(db)
            }
        }
    }
}

@Composable
fun ChatScreen(db: FirebaseFirestore) {
    var inputMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val chatList = remember { mutableStateListOf<Message>() }

    // Load chat history
    LaunchedEffect(Unit) {
        db.collection("chats")
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->
                value?.let {
                    chatList.clear()
                    for (doc in it.documents) {
                        doc.toObject(Message::class.java)?.let { msg ->
                            chatList.add(msg)
                        }
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false
        ) {
            items(chatList) { msg ->
                Text("${msg.sender}: ${msg.message}", modifier = Modifier.padding(8.dp))
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (inputMessage.isEmpty()) return@Button
                val msg = Message(inputMessage, "user")
                db.collection("chats").add(msg)
                inputMessage = ""

                // TODO: Implement AIService if needed

                AIService.sendToAI(msg.message) { aiResponse ->
                    val aiMsg = Message(aiResponse, "ai")
                    db.collection("chats").add(aiMsg)
                }

            }) {
                Text("Send")
            }
        }
    }
}
