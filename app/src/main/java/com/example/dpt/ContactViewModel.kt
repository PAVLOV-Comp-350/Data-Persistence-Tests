package com.example.dpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(
    private val dao: ContactDao
) : ViewModel() {
    // The internal state of the view model is private so that the UI can only
    // send updates to the state through the ContactEvent Interface
    private val _state = MutableStateFlow(ContactState())

    // Automatically maps the sort type to the correct query into the database
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _contacts = _state.flatMapLatest {
        when (it.sortType) {
            SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
            SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
            SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // The public facing state that is accessed by the UI
    // This code executes whenever there is a change in either
    // the sort type flow or the state flow.
    val state = combine(_state, _contacts) { state, contacts ->
        state.copy(
            contactList = contacts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.removeContact(event.contact)
                }
            }

            ContactEvent.HideDialog -> {
                // Initiate a state update by copying stat n-1
                // and modifying only the isAddingContact flag
                _state.update {
                    it.copy(isAddingConact = false)
                }
            }

            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }
                // Write the new contact to the DB
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                viewModelScope.launch {
                    dao.addOrUpdateContact(contact)
                }
                // Update UI state
                _state.update {
                    it.copy(
                        isAddingConact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }
            }

            is ContactEvent.SetFirstName -> {
                _state.update {
                    it.copy(firstName = event.firstName)
                }
            }

            is ContactEvent.SetLastName -> {
                _state.update {
                    it.copy(lastName = event.lastName)
                }
            }

            is ContactEvent.SetPhoneNumber -> {
                _state.update {
                    it.copy(phoneNumber = event.phoneNumber)
                }
            }

            ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(isAddingConact = true)
                }
            }

            is ContactEvent.SortContacts -> {
                _state.update {
                    it.copy(sortType = event.sortType)
                }
            }
        }
    }
}