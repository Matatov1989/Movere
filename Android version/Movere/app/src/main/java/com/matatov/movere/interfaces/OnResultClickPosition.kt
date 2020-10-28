package com.matatov.movere.interfaces

import com.matatov.movere.models.EventModel
import com.matatov.movere.models.UserModel

interface OnResultClickPosition {

    interface Contact {
        fun onClickPosition(contact: UserModel)

        fun onClickPositionLongPress(contact: UserModel)
    }

    interface Event {
        fun onClickPosition(event: EventModel)
    }

}
