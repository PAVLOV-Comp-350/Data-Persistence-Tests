package com.example.dpt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [Contact::class],
)
abstract class ContactDatabase : RoomDatabase() {
    abstract val dao: ContactDao
}