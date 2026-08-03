package com.example.engine

import kotlin.math.*

data class DetachedModuleDebris(
    val moduleName: String,
    var position: Vector2D,
    var velocity: Vector2D,
    var rotation: Float,
    var rotVel: Float
)

class ModularVehiclePhysics(
    val chassis: VehicleModule,
    val engine: VehicleModule,
    val wheels: VehicleModule,
    val core: VehicleModule,
    val stabilizer: VehicleModule,
    val utility: VehicleModule
) {
    var pos = Vector2D(100f, 200f)
    var vel = Vector2D(0f, 0f)
    var angleRad = 0f
    var angularVel = 0f

    // Module Health
    var chassisHp = chassis.maxHp
    var engineHp = engine.maxHp
    var wheelsHp = wheels.maxHp
    var coreHp = core.maxHp
    var stabilizerHp = stabilizer.maxHp
    var utilityHp = utility.maxHp

    var isEngineDetached = false
    var isWheelsDetached = false
    var isUtilityDetached = false

    val detachedDebrisList = mutableListOf<DetachedModuleDebris>()

    // Stats calculated from modules
    val totalWeight = (chassis.weight + engine.weight + wheels.weight + core.weight + stabilizer.weight + utility.weight).coerceAtLeast(10f)
    val totalPower = chassis.powerOutput + engine.powerOutput + wheels.powerOutput + core.powerOutput + stabilizer.powerOutput
    val maxEnergy = (100f + chassis.energyCap + core.energyCap + utility.energyCap).coerceAtLeast(50f)
    var currentEnergy = maxEnergy

    var currentHeat = 0f
    val maxHeat = 100f
    val coolingRate = (chassis.coolingRate + engine.coolingRate + core.coolingRate + stabilizer.coolingRate).coerceAtLeast(0.5f)

    // Shield & Abilities
    var isShieldActive = false
    var shieldTimer = 0f
    var isSlowMoActive = false
    var slowMoTimer = 0f
    private var damageCooldownTimer = 0f

    // Flip Detection
    private var totalAirRotationAccumulator = 0f
    var flipsCompletedCount = 0
    var isGrounded = false

    // Gravity
    var currentGravityMode = GravityMode.DOWN

    // Controls input
    var throttleInput = 0f // -1f to 1f
    var leanInput = 0f // -1f (left) to 1f (right)

    fun update(
        deltaTimeSec: Float,
        terrain: TerrainGenerator,
        particleSystem: ParticleSystem,
        audioSynthesizer: AudioSynthesizer
    ) {
        val dt = if (isSlowMoActive) deltaTimeSec * 0.4f else deltaTimeSec

        // Timers
        if (damageCooldownTimer > 0f) damageCooldownTimer -= dt
        if (isShieldActive) {
            shieldTimer -= dt
            if (shieldTimer <= 0f) isShieldActive = false
        }
        if (isSlowMoActive) {
            slowMoTimer -= dt
            if (slowMoTimer <= 0f) isSlowMoActive = false
        }

        // Cool heat continuously
        currentHeat = (currentHeat - coolingRate * 15f * dt).coerceAtLeast(0f)

        // 1. Gravity & Ground Check
        val gravityAccel = 650f
        val gravForce = currentGravityMode.vector * (totalWeight * gravityAccel)

        val groundY = terrain.getGroundYAt(pos.x)
        val roverRadius = 24f
        val targetGroundY = groundY - roverRadius

        // 2. Throttle & Propulsion
        var engineForce = Vector2D(0f, 0f)
        if (!isEngineDetached && currentEnergy > 0f && currentHeat < maxHeat) {
            if (abs(throttleInput) > 0.05f) {
                val speedMultiplier = if (wheels.id == "wheels_antigrav") 1.25f else 1.0f
                val drivePower = totalWeight * 260f * throttleInput * speedMultiplier

                val nextY = terrain.getGroundYAt(pos.x + 8f)
                val prevY = terrain.getGroundYAt(pos.x - 8f)
                val slopeAngle = atan2(nextY - prevY, 16f)

                val driveAngle = if (isGrounded) slopeAngle else angleRad
                val forwardDir = Vector2D(cos(driveAngle), sin(driveAngle))
                engineForce = forwardDir * drivePower

                // Energy & Heat
                val drain = (3f + totalPower * 0.02f) * dt * abs(throttleInput)
                currentEnergy = (currentEnergy - drain).coerceAtLeast(0f)
                currentHeat = (currentHeat + 14f * dt * abs(throttleInput)).coerceAtMost(maxHeat)

                // Particle flames & Sound
                val thrusterPos = pos - forwardDir * 30f
                particleSystem.spawnThrustFlames(thrusterPos, forwardDir)
                audioSynthesizer.playEngineSound(vel.length() / 250f, abs(throttleInput))
            }
        }

        // Acceleration
        val totalForce = gravForce + engineForce
        val accel = totalForce / totalWeight
        vel += accel * dt

        // Velocity Clamping (Crucial for 60FPS physics stability)
        vel.x = vel.x.coerceIn(-120f, 380f)
        vel.y = vel.y.coerceIn(-350f, 350f)

        // Friction & Air Drag
        if (isGrounded) {
            val surface = terrain.chooseSurfaceForX(pos.x)
            vel.x *= (1f - surface.friction * 0.4f * dt)
            if (surface == SurfaceType.NEON_BOOSTER) {
                vel.x += 250f * dt
                particleSystem.spawnSparks(pos)
            }
        } else {
            vel.x *= (1f - 0.2f * dt)
            vel.y *= (1f - 0.05f * dt)
        }

        // Ceiling force (Prevent flying into empty space)
        if (pos.y < -250f) {
            vel.y += 300f * dt
        }

        // Lean Pitch Control
        if (abs(leanInput) > 0.05f) {
            val torquePower = if (isGrounded) 3.5f else 6.0f
            angularVel += leanInput * torquePower * dt
        }
        angularVel *= (1f - 2.5f * dt)
        angleRad += angularVel * dt

        // Position Integration
        pos += vel * dt

        // 3. Ground Collision & Re-Entry
        if (pos.y >= targetGroundY && currentGravityMode == GravityMode.DOWN) {
            val prevVelY = vel.y
            pos.y = targetGroundY
            if (vel.y > 0f) vel.y = 0f
            isGrounded = true

            // Ground slope alignment
            val nextY = terrain.getGroundYAt(pos.x + 10f)
            val prevY = terrain.getGroundYAt(pos.x - 10f)
            val slopeAngle = atan2(nextY - prevY, 20f)

            var angleDiff = slopeAngle - angleRad
            while (angleDiff > Math.PI.toFloat()) angleDiff -= (2f * Math.PI.toFloat())
            while (angleDiff < -Math.PI.toFloat()) angleDiff += (2f * Math.PI.toFloat())
            angleRad += angleDiff * 7.0f * dt

            // Impact Damage (Only on hard landings from airborne height)
            if (prevVelY > 480f && damageCooldownTimer <= 0f && !isShieldActive) {
                val damage = ((prevVelY - 450f) * 0.12f).coerceAtMost(15f)
                applyDamage(damage, particleSystem, audioSynthesizer)
                damageCooldownTimer = 0.5f
            }

            // Flips Bonus
            if (abs(totalAirRotationAccumulator) >= 2f * Math.PI) {
                val flips = (abs(totalAirRotationAccumulator) / (2f * Math.PI)).toInt()
                flipsCompletedCount += flips
                currentEnergy = (currentEnergy + 25f).coerceAtMost(maxEnergy)
                particleSystem.spawnGravityWarpWave(pos)
                audioSynthesizer.playCrystalPickup()
            }
            totalAirRotationAccumulator = 0f
        } else {
            isGrounded = false
            totalAirRotationAccumulator += angularVel * dt
        }

        // 4. Update Detached Debris Physics
        val debrisIterator = detachedDebrisList.iterator()
        while (debrisIterator.hasNext()) {
            val d = debrisIterator.next()
            d.position += d.velocity * dt
            d.velocity += currentGravityMode.vector * 10f * dt
            d.rotation += d.rotVel * dt
            if (d.position.y > pos.y + 1000f) {
                debrisIterator.remove()
            }
        }

        // 5. Check Crystal Pickups
        terrain.getCrystals().forEach { crystal ->
            if (!crystal.collected && (crystal.position - pos).length() < 40f) {
                crystal.collected = true
                currentEnergy = (currentEnergy + crystal.value * 2f).coerceAtMost(maxEnergy)
                particleSystem.spawnCrystalPickupShockwave(crystal.position)
                audioSynthesizer.playCrystalPickup()
            }
        }
    }

    fun applyDamage(amount: Float, particleSystem: ParticleSystem, audioSynthesizer: AudioSynthesizer) {
        if (isShieldActive) return
        val clampedAmount = amount.coerceAtMost(15f)
        chassisHp = (chassisHp - clampedAmount * 0.4f).coerceAtLeast(0f)
        engineHp = (engineHp - clampedAmount * 0.3f).coerceAtLeast(0f)
        wheelsHp = (wheelsHp - clampedAmount * 0.3f).coerceAtLeast(0f)

        particleSystem.spawnExplosion(pos)
        audioSynthesizer.playImpactExplosion()

        // Check Detach Thresholds
        if (engineHp <= 0f && !isEngineDetached) {
            isEngineDetached = true
            detachedDebrisList.add(
                DetachedModuleDebris("Engine", pos, Vector2D(-60f, -80f), angleRad, 3f)
            )
        }
        if (wheelsHp <= 0f && !isWheelsDetached) {
            isWheelsDetached = true
            detachedDebrisList.add(
                DetachedModuleDebris("Wheels", pos, Vector2D(60f, -60f), angleRad, -3f)
            )
        }
    }

    fun activateAbility(ability: ActiveAbility, particleSystem: ParticleSystem, audioSynthesizer: AudioSynthesizer) {
        if (currentEnergy < ability.energyCost) return
        currentEnergy -= ability.energyCost

        when (ability) {
            ActiveAbility.SHIELD -> {
                isShieldActive = true
                shieldTimer = 5f
                particleSystem.spawnGravityWarpWave(pos)
            }
            ActiveAbility.NITRO_THRUST -> {
                val forwardDir = Vector2D(cos(angleRad), sin(angleRad))
                vel += forwardDir * 450f
                particleSystem.spawnThrustFlames(pos, forwardDir)
                audioSynthesizer.playGravityShift()
            }
            ActiveAbility.GRAVITY_FLIP -> {
                toggleGravityVector()
                particleSystem.spawnGravityWarpWave(pos)
                audioSynthesizer.playGravityShift()
            }
            ActiveAbility.REPAIR_DRONE -> {
                chassisHp = (chassisHp + 50f).coerceAtMost(chassis.maxHp)
                engineHp = (engineHp + 40f).coerceAtMost(engine.maxHp)
                wheelsHp = (wheelsHp + 40f).coerceAtMost(wheels.maxHp)
                particleSystem.spawnCrystalPickupShockwave(pos)
            }
            ActiveAbility.TIME_DILATION -> {
                isSlowMoActive = true
                slowMoTimer = 6f
                particleSystem.spawnGravityWarpWave(pos)
            }
            ActiveAbility.EMP_PULSE -> {
                particleSystem.spawnExplosion(pos)
                audioSynthesizer.playImpactExplosion()
            }
        }
    }

    fun toggleGravityVector() {
        currentGravityMode = when (currentGravityMode) {
            GravityMode.DOWN -> GravityMode.UP
            GravityMode.UP -> GravityMode.LEFT
            GravityMode.LEFT -> GravityMode.RIGHT
            GravityMode.RIGHT -> GravityMode.ZERO_G
            GravityMode.ZERO_G -> GravityMode.DOWN
            GravityMode.PULSE -> GravityMode.DOWN
        }
    }
}
