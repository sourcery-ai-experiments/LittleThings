package me.advik.littlethings

import io.papermc.paper.entity.LookAnchor
import org.bukkit.Material
import org.bukkit.entity.Minecart
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.util.Vector
import org.bukkit.Particle
import java.util.logging.Logger

class LittleListener(parent: LittleThings) : Listener {
    val minecartBaseMultiplier = 1.5
    val validRails = arrayOf(Material.RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL)
    val conductors = mapOf(
        Material.IRON_BLOCK to minecartBaseMultiplier + 0.5,
        Material.WAXED_COPPER_BLOCK to minecartBaseMultiplier + 0.9,
        Material.COPPER_BLOCK to minecartBaseMultiplier + 1.1,
        Material.GOLD_BLOCK to minecartBaseMultiplier * 2,
    )
    val maxParticles = 10
    // Logger instance (will be assigned later)
    private lateinit var logger: Logger
    var playerParkor = mutableMapOf<String, Boolean>()

    init {
        parent.server.pluginManager.registerEvents(this, parent)
        logger = parent.logger
    }

    @EventHandler
    fun onVehicleMove(event: VehicleMoveEvent) {
        val vehicle = event.vehicle
        if (vehicle is Minecart) {
            val location = vehicle.location
            val block = location.block
            val belowBlock = block.getRelative(0, -1, 0)

            if (validRails.contains(block.type)) {
                if (conductors.containsKey(belowBlock.type)) {
                    vehicle.velocity = vehicle.velocity.multiply(conductors[belowBlock.type]!!)
                } else if (belowBlock.type == Material.SLIME_BLOCK) {
                    vehicle.velocity = vehicle.velocity.add(Vector(0.0, 1.0, 0.0))
                }
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {

        val player = event.player
        val block = player.location.block
        val belowBlock = block.getRelative(0, -1, 0)

        if (!playerParkor.containsKey(player.name)) {
            playerParkor[player.name] = false
        }

        // The player is walking on magenta glazed terracotta
        if (belowBlock.type == Material.MAGENTA_GLAZED_TERRACOTTA) {
            // Get the direction of the block
            // [east, west, south, north]
            val blockState = belowBlock.blockData.asString
            // minecraft:magenta_glazed_terracotta[facing=south]

            // Get the direction of the block
            val direction = blockState.split("=")[1].replace("]", "")

            // Change the player's velocity based on the direction
            when (direction) {
                "east" -> player.velocity = Vector(-1.0, 0.0, 0.0)
                "west" -> player.velocity = Vector(1.0, 0.0, 0.0)
                "south" -> player.velocity = Vector(0.0, 0.0, -1.0)
                "north" -> player.velocity = Vector(0.0, 0.0, 1.0)
            }

            // Play particles
            for (i in 0 until maxParticles) {
                player.world.spawnParticle(Particle.FLAME, player.location, 1)
            }

            playerParkor[player.name] = true
            return // Exit the function so the player doesn't jump up
        }

        // If the player has just finished the parkour
        if (playerParkor[player.name] == true) {
            // Make the player jump up
            player.velocity = player.velocity.add(Vector(0.0, 1.0, 0.0))
            // Display particles
            for (i in 0 until maxParticles) {
                player.world.spawnParticle(Particle.WATER_SPLASH, player.location, 1)
            }
            playerParkor[player.name] = false
        }

    }

    fun unregister() {
        VehicleMoveEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
    }
}