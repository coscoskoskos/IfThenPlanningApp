package com.coscos.ifthenplanner.Database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "plans")
data class Plan (
    val titleText: String,
    @PrimaryKey val ifText: String,
    val thenText: String,
    val colorInt: Int,
    val isNotificationTrue: Boolean,
    val yearString: String,
    val monthString: String,
    val dateString: String,
    val dayStringRaw: String,
    val pMRaw: String,
    val hourString: String,
    val minString: String
)