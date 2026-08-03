package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val crystals: Int = 250,
    val alloy: Int = 50,
    val techPoints: Int = 5,
    val totalDistanceMeters: Float = 0f,
    val totalFlips: Int = 0,
    val totalGravityShifts: Int = 0,
    val unlockedBiomesCsv: String = "desert,neon_city,lunar",
    val activeBlueprintId: String = "default_rover",
    val unlockedSkillsCsv: String = ""
)

@Entity(tableName = "unlocked_modules")
data class VehicleModuleEntity(
    @PrimaryKey val moduleId: String,
    val name: String,
    val category: String,
    val rarity: String,
    val isEquipped: Boolean = false
)

@Entity(tableName = "saved_blueprints")
data class BlueprintEntity(
    @PrimaryKey val blueprintId: String,
    val name: String,
    val chassisModuleId: String,
    val engineModuleId: String,
    val wheelsModuleId: String,
    val coreModuleId: String,
    val stabilizerModuleId: String,
    val utilityModuleId: String
)

@Entity(tableName = "highscores")
data class HighscoreEntity(
    @PrimaryKey val biomeId: String,
    val bestDistanceMeters: Float = 0f,
    val maxFlipsInRun: Int = 0,
    val crystalsCollectedRun: Int = 0
)

@Entity(tableName = "daily_quests")
data class QuestEntity(
    @PrimaryKey val questId: String,
    val title: String,
    val description: String,
    val targetAmount: Int,
    var currentAmount: Int = 0,
    val rewardCrystals: Int = 100,
    val rewardTechPoints: Int = 2,
    var isClaimed: Boolean = false
)
