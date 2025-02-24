package com.example.dpt

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Upsert
    suspend fun addOrUpdateContact(contact: Contact)

    @Delete
    suspend fun removeContact(contact: Contact)

    @Query("SELECT * FROM contacts ORDER BY first_name ASC")
    fun getContactsOrderedByFirstName(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY last_name ASC")
    fun getContactsOrderedByLastName(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY phone_number ASC")
    fun getContactsOrderedByPhoneNumber(): Flow<List<Contact>>
}