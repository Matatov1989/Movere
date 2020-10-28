package com.matatov.movere.models

data class PushMessageModel(
    val title: String,
    val message: String,
    val senderName: String,
    val senderId: String,
    val senderToken: String,
    val receiverToken: String
)
