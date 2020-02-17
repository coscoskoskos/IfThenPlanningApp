package com.coscos.ifthenplanner.Database

import androidx.room.*

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg plan: Plan)

    @Query("DELETE FROM plans WHERE ifText = :ifContent")
    fun deletePlan(ifContent: String)

    @Query("UPDATE plans SET ifText = :ifContent, thenText = :thenContent WHERE ifText = :ifChanged AND thenText = :thenChanged")
    fun updatePlan(ifChanged: String, thenChanged: String, ifContent: String, thenContent: String)

    @Delete
    suspend fun deleteAll(plans: Array<Plan>)

    @Query("SELECT * FROM plans")
    suspend fun loadAllPlan(): Array<Plan>
}