package com.matatov.movere.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class EventModel(
    var eventName: String? = null,
    var eventUriPhoto: String? = null,
    var eventDescription: String? = null,
    var g: String? = null,
    var l: GeoPoint? = null,
    var eventTimeStampStop: Timestamp? = null,
    var evenUserId: String? = null
)
