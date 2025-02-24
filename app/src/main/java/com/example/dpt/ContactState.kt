package com.example.dpt

data class ContactState(
    val contactList: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingConact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
)
