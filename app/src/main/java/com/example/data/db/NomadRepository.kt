package com.example.data.db

import com.example.engine.BiomeType
import kotlinx.coroutines.flow.Flow

class NomadRepository(private val dao: NomadDao) {
    val playerProfile: Flow<PlayerProfileEntity?> = dao.getPlayerProfile()
    val unlockedModules: Flow<List<VehicleModuleEntity>> = dao.getAllUnlockedModules()
    val savedBlueprints: Flow<List<BlueprintEntity>> = dao.getAllBlueprints()
    val highscores: Flow<List<HighscoreEntity>> = dao.getAllHighscores()
    val dailyQuests: Flow<List<QuestEntity>> = dao.getAllQuests()

    suspend fun ensureInitialized() {
        val profile = dao.getPlayerProfileDirect()
        if (profile == null) {
            // Seed initial player profile
            dao.insertOrUpdateProfile(PlayerProfileEntity())

            // Seed initial starter unlocked modules (only 6 starters unlocked initially)
            val starterModuleIds = setOf("chassis_scout", "engine_ion", "wheels_cyber", "core_fusion", "stabilizer_gyro", "utility_drone")
            val defaultModules = DefaultModulesCatalog.ALL_CATALOG_MODULES
                .filter { starterModuleIds.contains(it.id) }
                .map {
                    VehicleModuleEntity(
                        moduleId = it.id,
                        name = it.name,
                        category = it.category.name,
                        rarity = it.rarity.name,
                        isEquipped = true
                    )
                }
            dao.insertModules(defaultModules)

            // Seed default blueprint
            dao.saveBlueprint(
                BlueprintEntity(
                    blueprintId = "default_rover",
                    name = "Вездеход «Номад Mk-I»",
                    chassisModuleId = "chassis_scout",
                    engineModuleId = "engine_ion",
                    wheelsModuleId = "wheels_cyber",
                    coreModuleId = "core_fusion",
                    stabilizerModuleId = "stabilizer_gyro",
                    utilityModuleId = "utility_drone"
                )
            )

            // Seed default highscores
            BiomeType.values().forEach { biome ->
                dao.insertHighscore(HighscoreEntity(biomeId = biome.id, bestDistanceMeters = 0f))
            }

            // Seed initial daily quests (in Russian)
            dao.insertQuests(
                listOf(
                    QuestEntity(
                        questId = "q_distance_1000",
                        title = "Пионер исследований",
                        description = "Преодолейте в общей сложности 1000 метров в любом мире.",
                        targetAmount = 1000,
                        rewardCrystals = 150,
                        rewardTechPoints = 3
                    ),
                    QuestEntity(
                        questId = "q_flips_5",
                        title = "Мастер трюков",
                        description = "Выполните 5 сальто в воздухе во время заездов.",
                        targetAmount = 5,
                        rewardCrystals = 200,
                        rewardTechPoints = 4
                    ),
                    QuestEntity(
                        questId = "q_grav_shifts_3",
                        title = "Покоритель гравитации",
                        description = "Выполните 3 динамических смены вектора гравитации.",
                        targetAmount = 3,
                        rewardCrystals = 250,
                        rewardTechPoints = 5
                    )
                )
            )
        }
    }

    suspend fun saveBlueprint(blueprint: BlueprintEntity) {
        dao.saveBlueprint(blueprint)
    }

    suspend fun getBlueprint(id: String): BlueprintEntity? {
        return dao.getBlueprint(id)
    }

    suspend fun updateRunResults(
        biomeId: String,
        distanceMeters: Float,
        crystalsEarned: Int,
        alloyEarned: Int,
        flipsDone: Int,
        gravShiftsDone: Int
    ) {
        val profile = dao.getPlayerProfileDirect() ?: PlayerProfileEntity()
        val newCrystals = profile.crystals + crystalsEarned
        val newAlloy = profile.alloy + alloyEarned
        val newTotalDist = profile.totalDistanceMeters + distanceMeters
        val newFlips = profile.totalFlips + flipsDone
        val newGravShifts = profile.totalGravityShifts + gravShiftsDone

        dao.insertOrUpdateProfile(
            profile.copy(
                crystals = newCrystals,
                alloy = newAlloy,
                totalDistanceMeters = newTotalDist,
                totalFlips = newFlips,
                totalGravityShifts = newGravShifts
            )
        )

        // Update active quests progress
        val activeQuests = dao.getAllQuestsDirect()
        activeQuests.forEach { quest ->
            if (!quest.isClaimed) {
                var added = 0
                when (quest.questId) {
                    "q_distance_1000" -> added = distanceMeters.toInt()
                    "q_flips_5" -> added = flipsDone
                    "q_grav_shifts_3" -> added = gravShiftsDone
                }
                if (added > 0) {
                    val newAmount = minOf(quest.targetAmount, quest.currentAmount + added)
                    dao.updateQuest(quest.copy(currentAmount = newAmount))
                }
            }
        }

        // Update highscore
        val currentHighscore = dao.getHighscoreForBiome(biomeId)
        val bestDist = currentHighscore?.bestDistanceMeters ?: 0f
        val maxFlips = currentHighscore?.maxFlipsInRun ?: 0
        if (distanceMeters > bestDist || flipsDone > maxFlips) {
            dao.insertHighscore(
                HighscoreEntity(
                    biomeId = biomeId,
                    bestDistanceMeters = maxOf(bestDist, distanceMeters),
                    maxFlipsInRun = maxOf(maxFlips, flipsDone),
                    crystalsCollectedRun = maxOf(currentHighscore?.crystalsCollectedRun ?: 0, crystalsEarned)
                )
            )
        }
    }

    suspend fun unlockTechSkill(skillId: String, costTechPoints: Int) {
        val profile = dao.getPlayerProfileDirect() ?: return
        if (profile.techPoints < costTechPoints) return
        val existingSkills = if (profile.unlockedSkillsCsv.isEmpty()) emptyList() else profile.unlockedSkillsCsv.split(",")
        if (!existingSkills.contains(skillId)) {
            val newSkillsCsv = (existingSkills + skillId).joinToString(",")
            dao.insertOrUpdateProfile(
                profile.copy(
                    techPoints = profile.techPoints - costTechPoints,
                    unlockedSkillsCsv = newSkillsCsv
                )
            )
        }
    }

    suspend fun unlockModule(moduleId: String) {
        val existing = DefaultModulesCatalog.getModuleById(moduleId)
        dao.insertModule(
            VehicleModuleEntity(
                moduleId = existing.id,
                name = existing.name,
                category = existing.category.name,
                rarity = existing.rarity.name,
                isEquipped = false
            )
        )
    }

    suspend fun addCrystalsAndAlloy(crystals: Int, alloy: Int, techPoints: Int = 0) {
        val profile = dao.getPlayerProfileDirect() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(
            profile.copy(
                crystals = profile.crystals + crystals,
                alloy = profile.alloy + alloy,
                techPoints = profile.techPoints + techPoints
            )
        )
    }

    suspend fun claimQuest(quest: QuestEntity) {
        val updatedQuest = quest.copy(isClaimed = true)
        dao.updateQuest(updatedQuest)
        addCrystalsAndAlloy(quest.rewardCrystals, 10, quest.rewardTechPoints)
    }
}
