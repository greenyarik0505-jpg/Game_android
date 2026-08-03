package com.example.engine

import kotlin.math.*

class TerrainGenerator(val biome: BiomeType) {
    data class GroundPoint(
        val x: Float,
        val y: Float,
        val surfaceType: SurfaceType,
        val normalAngle: Float = 0f,
        val isPlatform: Boolean = false
    )

    data class Obstacle(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val type: ObstacleType
    )

    enum class ObstacleType {
        CRUMBLING_BRIDGE,
        LAVA_PIT,
        GRAVITY_ANOMALY,
        SPEED_BOOSTER,
        FLOATING_ISLAND
    }

    private val segmentWidth = 20f
    private val pointsMap = mutableListOf<GroundPoint>()
    private val crystalsList = mutableListOf<EnergyCrystal>()
    private val obstaclesList = mutableListOf<Obstacle>()
    private var maxGeneratedX = 0f

    init {
        generateChunk(0f, 2000f)
    }

    fun generateChunk(startX: Float, endX: Float) {
        if (endX <= maxGeneratedX) return

        var x = max(startX, maxGeneratedX)
        while (x < endX) {
            val groundY = computeHeightAt(x)
            val nextY = computeHeightAt(x + segmentWidth)
            val dx = segmentWidth
            val dy = nextY - groundY
            val normalAngle = atan2(dy, dx)

            val surface = chooseSurfaceForX(x)
            pointsMap.add(GroundPoint(x, groundY, surface, normalAngle))

            // Generate Energy Crystals
            if (x > 100f && (x.toInt() % 80 == 0)) {
                val crystalY = groundY - 30f - sin(x * 0.05f) * 20f
                val isSuper = (x.toInt() % 400 == 0)
                crystalsList.add(EnergyCrystal(position = Vector2D(x, crystalY), value = if (isSuper) 50 else 10, isSuperCrystal = isSuper))
            }

            // Generate Obstacles / Features
            if (x > 200f && (x.toInt() % 300 == 0)) {
                when (biome) {
                    BiomeType.VOLCANO -> obstaclesList.add(Obstacle(x, groundY + 10f, 120f, 40f, ObstacleType.LAVA_PIT))
                    BiomeType.NEON_CITY -> obstaclesList.add(Obstacle(x, groundY - 5f, 80f, 15f, ObstacleType.SPEED_BOOSTER))
                    BiomeType.GLITCH -> obstaclesList.add(Obstacle(x, groundY - 100f, 150f, 25f, ObstacleType.GRAVITY_ANOMALY))
                    BiomeType.CLOUDS -> obstaclesList.add(Obstacle(x, groundY - 60f, 200f, 30f, ObstacleType.FLOATING_ISLAND))
                    else -> obstaclesList.add(Obstacle(x, groundY, 100f, 20f, ObstacleType.CRUMBLING_BRIDGE))
                }
            }

            x += segmentWidth
        }
        maxGeneratedX = endX
    }

    private fun computeHeightAt(x: Float): Float {
        val baseLine = 400f
        return when (biome) {
            BiomeType.DESERT -> {
                baseLine - sin(x * 0.003f) * 110f - cos(x * 0.012f) * 35f
            }
            BiomeType.NEON_CITY -> {
                baseLine - sin(x * 0.004f) * 80f - sin(x * 0.015f) * 40f
            }
            BiomeType.VOLCANO -> {
                baseLine - sin(x * 0.005f) * 130f - cos(x * 0.018f) * 50f
            }
            BiomeType.LUNAR -> {
                baseLine - sin(x * 0.003f) * 70f - cos(x * 0.009f) * 60f
            }
            BiomeType.GLITCH -> {
                baseLine - sin(x * 0.008f) * 90f - cos(x * 0.02f) * 45f
            }
            BiomeType.TOXIC -> {
                baseLine - sin(x * 0.004f) * 95f - sin(x * 0.016f) * 30f
            }
            BiomeType.CLOUDS -> {
                baseLine - sin(x * 0.004f) * 120f - cos(x * 0.015f) * 45f
            }
        }
    }

    fun chooseSurfaceForX(x: Float): SurfaceType {
        val modX = x.toInt() % 500
        return when {
            modX in 100..180 && biome == BiomeType.NEON_CITY -> SurfaceType.NEON_BOOSTER
            modX in 220..300 && biome == BiomeType.NEON_CITY -> SurfaceType.METAL_MAGNET
            modX in 150..220 && biome == BiomeType.CLOUDS -> SurfaceType.ICE
            modX in 300..380 && biome == BiomeType.TOXIC -> SurfaceType.TOXIC_SLIME
            modX in 250..330 && biome == BiomeType.VOLCANO -> SurfaceType.LAVA_ROCK
            else -> biome.defaultSurface
        }
    }

    fun getGroundYAt(x: Float): Float {
        if (x > maxGeneratedX - 500f) {
            generateChunk(maxGeneratedX, maxGeneratedX + 1500f)
        }
        return computeHeightAt(x)
    }

    fun getVisibleGroundPoints(minX: Float, maxX: Float): List<GroundPoint> {
        if (maxX > maxGeneratedX - 500f) {
            generateChunk(maxGeneratedX, maxGeneratedX + 1500f)
        }
        if (pointsMap.isEmpty()) return emptyList()
        val startIndex = (minX / segmentWidth).toInt().coerceIn(0, pointsMap.size - 1)
        val endIndex = (maxX / segmentWidth).toInt().coerceIn(startIndex, pointsMap.size)
        return pointsMap.subList(startIndex, endIndex)
    }

    fun getGroundPoints(): List<GroundPoint> = pointsMap
    fun getCrystals(): List<EnergyCrystal> = crystalsList
    fun getObstacles(): List<Obstacle> = obstaclesList
}
