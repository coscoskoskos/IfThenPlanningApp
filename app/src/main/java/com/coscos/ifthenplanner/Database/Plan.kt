package com.coscos.ifthenplanner.Database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "plans")
data class Plan (
    @PrimaryKey val ifText: String,
    val thenText: String
)