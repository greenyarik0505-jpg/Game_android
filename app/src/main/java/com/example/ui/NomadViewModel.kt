package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.engine.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenState {
    MENU,
    PLAY,
    GARAGE,
    TECH_TREE,
    QUESTS,
    SANDBOX,
    ROGUELIKE_DRAFT,
    SUMMARY
}

data class RunStats(
    val distanceMeters: Float = 0f,
    val crystalsCollected: Int = 0,
    val alloyCollected: Int = 0,
    val flipsDone: Int = 0,
    val gravityShiftsDone: Int = 0,
    val isGameOver: Boolean = false,
    val gameOverReason: String = ""
)

class NomadViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = NomadRepository(database.nomadDao())

    val playerProfile: StateFlow<PlayerProfileEntity?> = repository.playerProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val unlockedModules: StateFlow<List<VehicleModuleEntity>> = repository.unlockedModules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyQuests: StateFlow<List<QuestEntity>> = repository.dailyQuests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val highscores: StateFlow<List<HighscoreEntity>> = repository.highscores.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI State
    var currentScreen = MutableStateFlow(ScreenState.MENU)
    var selectedBiome = MutableStateFlow(BiomeType.DESERT)
    var selectedGameMode = MutableStateFlow(GameMode.INFINITE)

    // Equipped Modules State
    var equippedChassisId = MutableStateFlow("chassis_scout")
    var equippedEngineId = MutableStateFlow("engine_ion")
    var equippedWheelsId = MutableStateFlow("wheels_cyber")
    var equippedCoreId = MutableStateFlow("core_fusion")
    var equippedStabilizerId = MutableStateFlow("stabilizer_gyro")
    var equippedUtilityId = MutableStateFlow("utility_drone")

    // Active Engine Instances
    var activePhysics: ModularVehiclePhysics? = null
    var activeTerrain: TerrainGenerator? = null
    val particleSystem = ParticleSystem()
    val audioSynthesizer = AudioSynthesizer()

    var liveRunStats = MutableStateFlow(RunStats())
    var roguelikeSector = MutableStateFlow(1)
    var roguelikeDraftChoices = MutableStateFlow<List<VehicleModule>>(emptyList())

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            loadEquippedBlueprint()
        }
    }

    private suspend fun loadEquippedBlueprint() {
        val blueprint = repository.getBlueprint("default_rover")
        if (blueprint != null) {
            equippedChassisId.value = blueprint.chassisModuleId
            equippedEngineId.value = blueprint.engineModuleId
            equippedWheelsId.value = blueprint.wheelsModuleId
            equippedCoreId.value = blueprint.coreModuleId
            equippedStabilizerId.value = blueprint.stabilizerModuleId
            equippedUtilityId.value = blueprint.utilityModuleId
        }
    }

    fun startRun(biome: BiomeType, mode: GameMode) {
        selectedBiome.value = biome
        selectedGameMode.value = mode

        val chassis = DefaultModulesCatalog.getModuleById(equippedChassisId.value)
        val engine = DefaultModulesCatalog.getModuleById(equippedEngineId.value)
        val wheels = DefaultModulesCatalog.getModuleById(equippedWheelsId.value)
        val core = DefaultModulesCatalog.getModuleById(equippedCoreId.value)
        val stabilizer = DefaultModulesCatalog.getModuleById(equippedStabilizerId.value)
        val utility = DefaultModulesCatalog.getModuleById(equippedUtilityId.value)

        activePhysics = ModularVehiclePhysics(chassis, engine, wheels, core, stabilizer, utility)
        activeTerrain = TerrainGenerator(biome)
        liveRunStats.value = RunStats()
        roguelikeSector.value = 1

        currentScreen.value = ScreenState.PLAY
    }

    fun updateGameLoop(deltaTimeSec: Float) {
        val physics = activePhysics ?: return
        val terrain = activeTerrain ?: return
        if (liveRunStats.value.isGameOver) return

        physics.update(deltaTimeSec, terrain, particleSystem, audioSynthesizer)
        particleSystem.update(deltaTimeSec)

        // Calculate stats
        val currentDist = (physics.pos.x - 100f) / 10f
        val crystalsInRun = terrain.getCrystals().count { it.collected }
        val alloyInRun = crystalsInRun / 3

        liveRunStats.value = liveRunStats.value.copy(
            distanceMeters = maxOf(0f, currentDist),
            crystalsCollected = crystalsInRun,
            alloyCollected = alloyInRun,
            flipsDone = physics.flipsCompletedCount
        )

        // Roguelike Sector Checkpoint Trigger
        if (selectedGameMode.value == GameMode.ROGUELIKE && currentDist >= roguelikeSector.value * 500f) {
            triggerRoguelikeDraftChoice()
        }

        // Check GameOver conditions
        if (physics.currentEnergy <= 0f && physics.vel.length() < 5f) {
            triggerGameOver("Энергия исчерпана!")
        } else if (physics.chassisHp <= 0f) {
            triggerGameOver("Корпус уничтожен!")
        }
    }

    fun performGravityShift() {
        val physics = activePhysics ?: return
        physics.toggleGravityVector()
        particleSystem.spawnGravityWarpWave(physics.pos)
        audioSynthesizer.playGravityShift()
        liveRunStats.value = liveRunStats.value.copy(
            gravityShiftsDone = liveRunStats.value.gravityShiftsDone + 1
        )
    }

    private fun triggerRoguelikeDraftChoice() {
        val choices = DefaultModulesCatalog.ALL_CATALOG_MODULES.shuffled().take(3)
        roguelikeDraftChoices.value = choices
        currentScreen.value = ScreenState.ROGUELIKE_DRAFT
    }

    fun applyRoguelikeUpgrade(module: VehicleModule) {
        when (module.category) {
            ModuleCategory.CHASSIS -> equippedChassisId.value = module.id
            ModuleCategory.ENGINE -> equippedEngineId.value = module.id
            ModuleCategory.WHEELS -> equippedWheelsId.value = module.id
            ModuleCategory.CORE -> equippedCoreId.value = module.id
            ModuleCategory.STABILIZER -> equippedStabilizerId.value = module.id
            ModuleCategory.UTILITY -> equippedUtilityId.value = module.id
        }
        roguelikeSector.value += 1
        currentScreen.value = ScreenState.PLAY
    }

    fun triggerGameOver(reason: String) {
        val stats = liveRunStats.value
        liveRunStats.value = stats.copy(isGameOver = true, gameOverReason = reason)

        viewModelScope.launch {
            repository.updateRunResults(
                biomeId = selectedBiome.value.id,
                distanceMeters = stats.distanceMeters,
                crystalsEarned = stats.crystalsCollected,
                alloyEarned = stats.alloyCollected,
                flipsDone = stats.flipsDone,
                gravShiftsDone = stats.gravityShiftsDone
            )
        }
        currentScreen.value = ScreenState.SUMMARY
    }

    fun equipModule(category: ModuleCategory, moduleId: String) {
        when (category) {
            ModuleCategory.CHASSIS -> equippedChassisId.value = moduleId
            ModuleCategory.ENGINE -> equippedEngineId.value = moduleId
            ModuleCategory.WHEELS -> equippedWheelsId.value = moduleId
            ModuleCategory.CORE -> equippedCoreId.value = moduleId
            ModuleCategory.STABILIZER -> equippedStabilizerId.value = moduleId
            ModuleCategory.UTILITY -> equippedUtilityId.value = moduleId
        }

        // Save equipped blueprint
        viewModelScope.launch {
            repository.saveBlueprint(
                BlueprintEntity(
                    blueprintId = "default_rover",
                    name = "Custom Nomad Rover",
                    chassisModuleId = equippedChassisId.value,
                    engineModuleId = equippedEngineId.value,
                    wheelsModuleId = equippedWheelsId.value,
                    coreModuleId = equippedCoreId.value,
                    stabilizerModuleId = equippedStabilizerId.value,
                    utilityModuleId = equippedUtilityId.value
                )
            )
        }
    }

    fun unlockModule(moduleId: String, crystalCost: Int) {
        viewModelScope.launch {
            val profile = playerProfile.value ?: return@launch
            if (profile.crystals >= crystalCost) {
                repository.addCrystalsAndAlloy(-crystalCost, 0)
                repository.unlockModule(moduleId)
            }
        }
    }

    fun unlockTechSkill(skillId: String, costTechPoints: Int) {
        viewModelScope.launch {
            repository.unlockTechSkill(skillId, costTechPoints)
        }
    }

    fun claimQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.claimQuest(quest)
        }
    }
}
