package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NomadDao {
    @Query("SELECT * FROM player_profile WHERE id = 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1")
    suspend fun getPlayerProfileDirect(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM unlocked_modules")
    fun getAllUnlockedModules(): Flow<List<VehicleModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: VehicleModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<VehicleModuleEntity>)

    @Query("SELECT * FROM saved_blueprints WHERE blueprintId = :id")
    suspend fun getBlueprint(id: String): BlueprintEntity?

    @Query("SELECT * FROM saved_blueprints")
    fun getAllBlueprints(): Flow<List<BlueprintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBlueprint(blueprint: BlueprintEntity)

    @Query("SELECT * FROM highscores")
    fun getAllHighscores(): Flow<List<HighscoreEntity>>

    @Query("SELECT * FROM highscores WHERE biomeId = :biomeId")
    suspend fun getHighscoreForBiome(biomeId: String): HighscoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighscore(highscore: HighscoreEntity)

    @Query("SELECT * FROM daily_quests")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM daily_quests")
    suspend fun getAllQuestsDirect(): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)
}
