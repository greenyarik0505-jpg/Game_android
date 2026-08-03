package com.example.engine

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.OverheatCrimson
import java.util.Random

class ParticleSystem {
    private val particles = mutableListOf<Particle>()
    private val random = Random()

    fun update(deltaTimeSec: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()
            p.currentLife -= deltaTimeSec
            if (p.currentLife <= 0f) {
                iterator.remove()
            } else {
                p.position += p.velocity * deltaTimeSec
            }
        }
        if (particles.size > 100) {
            particles.subList(0, particles.size - 100).clear()
        }
    }

    fun spawnThrustFlames(pos: Vector2D, dir: Vector2D) {
        for (i in 0..2) {
            val spreadX = (random.nextFloat() - 0.5f) * 15f
            val spreadY = (random.nextFloat() - 0.5f) * 15f
            val speed = 80f + random.nextFloat() * 120f
            val vel = dir * -speed + Vector2D(spreadX, spreadY)
            val color = if (random.nextBoolean()) NeonCyan else NeonPink
            particles.add(
                Particle(
                    position = pos,
                    velocity = vel,
                    color = color,
                    size = 4f + random.nextFloat() * 6f,
                    maxLife = 0.25f + random.nextFloat() * 0.2f
                )
            )
        }
    }

    fun spawnSparks(pos: Vector2D) {
        for (i in 0..6) {
            val velX = (random.nextFloat() - 0.5f) * 200f
            val velY = -random.nextFloat() * 150f - 50f
            particles.add(
                Particle(
                    position = pos,
                    velocity = Vector2D(velX, velY),
                    color = NeonAmber,
                    size = 3f + random.nextFloat() * 4f,
                    maxLife = 0.4f + random.nextFloat() * 0.3f
                )
            )
        }
    }

    fun spawnGravityWarpWave(pos: Vector2D) {
        for (i in 0..16) {
            val angle = (i / 16f) * 2f * Math.PI.toFloat()
            val vel = Vector2D(kotlin.math.cos(angle), kotlin.math.sin(angle)) * 220f
            particles.add(
                Particle(
                    position = pos,
                    velocity = vel,
                    color = NeonPink,
                    size = 5f,
                    maxLife = 0.5f
                )
            )
        }
    }

    fun spawnCrystalPickupShockwave(pos: Vector2D) {
        for (i in 0..10) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = 120f + random.nextFloat() * 80f
            val vel = Vector2D(kotlin.math.cos(angle) * speed, kotlin.math.sin(angle) * speed)
            particles.add(
                Particle(
                    position = pos,
                    velocity = vel,
                    color = NeonCyan,
                    size = 6f,
                    maxLife = 0.4f
                )
            )
        }
    }

    fun spawnExplosion(pos: Vector2D) {
        for (i in 0..25) {
            val angle = random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = 50f + random.nextFloat() * 300f
            val vel = Vector2D(kotlin.math.cos(angle) * speed, kotlin.math.sin(angle) * speed)
            val color = if (i % 2 == 0) OverheatCrimson else NeonAmber
            particles.add(
                Particle(
                    position = pos,
                    velocity = vel,
                    color = color,
                    size = 6f + random.nextFloat() * 8f,
                    maxLife = 0.6f + random.nextFloat() * 0.4f
                )
            )
        }
    }

    fun getActiveParticles(): List<Particle> = particles
}
