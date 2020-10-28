package com.matatov.movere.models

import com.matatov.movere.interfaces.Message
import com.matatov.movere.utils.ConstantsUtil.IMAGE
import java.util.*

class ImageMessage (val imagePath: String,
                    override val time: Date,
                    override val senderId: String,
                    override val recipientId: String,
                    override val senderName: String,
                    override val type: String = IMAGE)
    : Message {
    constructor() : this("", Date(0), "", "", "")
}
