package com.wings.mile

class ItemList : ArrayList<listItem>()

data class listItem(
    val avatar: String,
    val createdAt: String,
    val `data`: Data,
    val email: String,
    val favouriteColor: String,
    val firstName: String,
    val fromName: String,
    val id: String,
    val jobtitle: String,
    val lastName: String,
    val to: String
)

data class Data(
    val body: String,
    val fromId: String,
    val id: String,
    val meetingid: String,
    val title: String,
    val toId: String
)