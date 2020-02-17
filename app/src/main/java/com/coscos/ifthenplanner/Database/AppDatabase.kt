package com.coscos.ifthenplanner.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Plan::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlanDao(): PlanDao
}