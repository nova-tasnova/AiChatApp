package com.example.aichatapp

data class Message(
    val message: String = "",
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
