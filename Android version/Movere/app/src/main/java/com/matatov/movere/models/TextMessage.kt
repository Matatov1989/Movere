package com.matatov.movere.models

import com.matatov.movere.interfaces.Message
import com.matatov.movere.utils.ConstantsUtil.TEXT
import java.util.*

class TextMessage(
    val text: String,
    override val time: Date,
    override val senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val type: String = TEXT
) : Message {
    constructor() : this("", Date(0), "", "", "")
}
