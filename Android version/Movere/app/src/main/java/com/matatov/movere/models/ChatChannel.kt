package com.matatov.movere.models

class ChatChannel (val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}