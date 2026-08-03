package com.example.engine

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

// 2D Vector math helper
data class Vector2D(var x: Float = 0f, var y: Float = 0f) {
    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)
    operator fun times(scalar: Float) = Vector2D(x * scalar, y * scalar)
    operator fun div(scalar: Float) = if (scalar != 0f) Vector2D(x / scalar, y / scalar) else Vector2D(0f, 0f)
    fun length() = kotlin.math.sqrt(x * x + y * y)
    fun normalized(): Vector2D {
        val len = length()
        return if (len > 0f) this / len else Vector2D(0f, 0f)
    }
    fun dot(other: Vector2D) = x * other.x + y * other.y
    fun rotate(angleRad: Float): Vector2D {
        val cosA = kotlin.math.cos(angleRad)
        val sinA = kotlin.math.sin(angleRad)
        return Vector2D(x * cosA - y * sinA, x * sinA + y * cosA)
    }
}

enum class GravityMode(val displayName: String, val vector: Vector2D) {
    DOWN("Нормальная (Вниз)", Vector2D(0f, 9.8f)),
    UP("Инверсия (Вверх)", Vector2D(0f, -9.8f)),
    LEFT("Стена слева", Vector2D(-9.8f, 0f)),
    RIGHT("Стена справа", Vector2D(9.8f, 0f)),
    ZERO_G("Невесомость", Vector2D(0f, 0f)),
    PULSE("Грав-импульс", Vector2D(0f, 15f))
}

enum class SurfaceType(
    val displayName: String,
    val friction: Float,
    val speedMultiplier: Float,
    val isMagnetic: Boolean,
    val color: Color
) {
    NORMAL("Стандартный грунт", 0.8f, 1.0f, false, Color(0xFF8D6E63)),
    ICE("Ледниковый лёд", 0.15f, 1.2f, false, Color(0xFF80DEEA)),
    SAND("Песчаные дюны", 1.2f, 0.6f, false, Color(0xFFFFD54F)),
    METAL_MAGNET("Магнитный сплав", 1.0f, 1.1f, true, Color(0xFF90A4AE)),
    NEON_BOOSTER("Неоновый бустер", 0.9f, 2.2f, false, NeonCyan),
    TOXIC_SLIME("Токсичная слизь", 1.8f, 0.4f, false, Color(0xFF76FF03)),
    LAVA_ROCK("Лавовая корка", 0.85f, 0.9f, false, OverheatCrimson),
    VOID_GRID("Глитч-сетка", 0.7f, 1.3f, true, GravRingViolet)
}

enum class BiomeType(
    val id: String,
    val title: String,
    val description: String,
    val primaryColor: Color,
    val skyColor: Color,
    val defaultSurface: SurfaceType
) {
    DESERT("desert", "Песчаные Дюны", "Бескрайние песчаные холмы с порывами ветра.", SandColor, DesertSky, SurfaceType.SAND),
    NEON_CITY("neon_city", "Кибер-Мегаполис", "Высокоскоростные магнитные трассы и неоновые бустеры.", NeonCyan, CitySky, SurfaceType.METAL_MAGNET),
    VOLCANO("volcano", "Лавовый Кальдера", "Экстремально горячая зона. Остерегайтесь выбросов магмы!", OverheatCrimson, LavaSky, SurfaceType.LAVA_ROCK),
    LUNAR("lunar", "Лунная База", "Низкая гравитация, идеальная для головокружительных трюков.", GravRingViolet, SpaceSky, SurfaceType.NORMAL),
    GLITCH("glitch", "Глитч-Измерение", "Искажённая реальность со внезапной сменой гравитации.", NeonPink, GlitchSky, SurfaceType.VOID_GRID),
    TOXIC("toxic", "Био-Болото", "Кислотные лужи и липкая слизь, замедляющие вездеход.", Color(0xFF76FF03), ToxicSky, SurfaceType.TOXIC_SLIME),
    CLOUDS("clouds", "Архипелаг Облаков", "Парящие острова, соединённые гравитационными мостами.", Color(0xFF80D8FF), CloudSky, SurfaceType.ICE)
}

val SandColor = Color(0xFFFFC107)
val DesertSky = Color(0xFF1F1A0A)
val CitySky = Color(0xFF0B1021)
val LavaSky = Color(0xFF1C0A0A)
val SpaceSky = Color(0xFF080816)
val GlitchSky = Color(0xFF1A0A1F)
val ToxicSky = Color(0xFF0A1F0C)
val CloudSky = Color(0xFF0A1828)

enum class ModuleCategory(val displayName: String) {
    CHASSIS("Рама шасси"),
    ENGINE("Двигатель / Ускоритель"),
    WHEELS("Колёса / Движитель"),
    CORE("Энергоядро"),
    STABILIZER("Грав-стабилизатор"),
    UTILITY("Модуль поддержки")
}

enum class ModuleRarity(val displayName: String, val color: Color, val statMultiplier: Float) {
    COMMON("Обычный", Color(0xFFB0BEC5), 1.0f),
    RARE("Редкий", NeonCyan, 1.3f),
    EPIC("Эпический", GravRingViolet, 1.7f),
    LEGENDARY("Легендарный", NeonAmber, 2.3f)
}

data class VehicleModule(
    val id: String,
    val name: String,
    val category: ModuleCategory,
    val rarity: ModuleRarity,
    val description: String,
    val weight: Float, // Higher weight = heavier inertia
    val powerOutput: Float, // Acceleration force
    val energyCap: Float, // Max energy storage
    val coolingRate: Float, // Overheat dissipation rate
    val maxHp: Float, // Structural health before detaching
    val specialPerk: String = ""
)

enum class ActiveAbility(val displayName: String, val energyCost: Float, val cooldownSeconds: Float) {
    SHIELD("Защитный щит", 20f, 8f),
    NITRO_THRUST("Гипер-ускорение", 15f, 4f),
    GRAVITY_FLIP("Инверсия гравитации", 25f, 6f),
    REPAIR_DRONE("Ремонтный дрон", 30f, 12f),
    TIME_DILATION("Замедление времени", 35f, 15f),
    EMP_PULSE("ЭМИ-импульс", 20f, 10f)
}

enum class GameMode(val title: String, val description: String) {
    INFINITE("Бесконечный Номад", "Бесконечное исследование с динамической гравитацией."),
    ROGUELIKE("Протокол «Номад»", "Выбирайте улучшения модулей на чекпоинтах секторов."),
    CHALLENGES("Трюковые испытания", "Выполняйте сальто, трассы и спец-задания."),
    BOSS_BATTLE("Грав-Титан «Левиафан»", "Убегайте и победите гигантского механического босса."),
    SANDBOX("Песочница Гаража", "Тестируйте сборки с бесконечной энергией и гравитацией.")
}

data class EnergyCrystal(
    var position: Vector2D,
    val value: Int = 10,
    var collected: Boolean = false,
    val isSuperCrystal: Boolean = false
)

data class Particle(
    var position: Vector2D,
    var velocity: Vector2D,
    var color: Color,
    var size: Float,
    var maxLife: Float,
    var currentLife: Float = maxLife
)
