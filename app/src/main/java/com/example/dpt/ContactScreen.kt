package com.example.dpt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactScreen(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(ContactEvent.ShowDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact"
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    ) { padding ->
        if (state.isAddingConact) {
            AddContactDialog(state = state, onEvent = onEvent)
        }
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SortType.entries.forEach {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(ContactEvent.SortContacts(it))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == it,
                                onClick = {
                                    onEvent(ContactEvent.SortContacts(it))
                                }
                            )
                            Text(text = it.name)
                        }
                    }
                }
            }

            items(state.contactList) {
                ContactRowItem(it, onEvent)
            }
        }
    }
}

@Composable
fun ContactRowItem(
    contact: Contact,
    onEvent: (ContactEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "${contact.firstName} ${contact.lastName}",
                fontSize = 20.sp
            )
            Text(
                text = "+${contact.phoneNumber}",
                fontSize = 12.sp
            )
        }
        IconButton(onClick = {
            onEvent(ContactEvent.DeleteContact(contact))
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Contact")
        }
    }
}