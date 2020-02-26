package com.coscos.ifthenplanner.Database

import androidx.room.*

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlan(vararg plan: Plan)

    @Query("DELETE FROM plans WHERE madeAt = :madeWhen")
    fun deletePlan(madeWhen: String)

    @Query("UPDATE plans SET titleText = :titleContent, ifText = :ifContent, thenText = :thenContent, colorInt = :colorContent, isNotificationTrue = :notificationContent, yearString = :yearContent, monthString = :monthContent, dateString = :dateContent, dayStringRaw = :dayContent, pMRaw = :pMContent, hourString = :hourContent, minString = :minContent WHERE madeAt = :madeWhen")
    fun updatePlan(titleContent: String, ifContent: String, thenContent: String, colorContent: Int,
                   notificationContent: Boolean, yearContent: String, monthContent: String,
                   dateContent: String, dayContent: String, pMContent: String, hourContent: String, minContent: String, madeWhen: String)

    @Delete
    suspend fun deleteAll(plans: Array<Plan>)

    @Query("SELECT * FROM plans")
    suspend fun loadAllPlan(): Array<Plan>
}