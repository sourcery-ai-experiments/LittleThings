package me.advik.littlethings

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin

class LittleThings : JavaPlugin() {
    lateinit var listener: LittleListener

    override fun onEnable() {
        add_godAppleRecipe()
        add_alternateGodAppleRecipe()
        add_recipeFor_XP_Bottle()

        listener = LittleListener(this)

        logger.info("LittleThings has been enabled!")
    }

    override fun onDisable() {
        listener.unregister()
        logger.info("LittleThings has been disabled!")

    }

    fun add_godAppleRecipe() {
        val key = NamespacedKey(this, "god_apple")
        val godApple = ItemStack(Material.ENCHANTED_GOLDEN_APPLE)

        val recipe = ShapedRecipe(key, godApple)
        recipe.shape("GGG", "GAG", "GGG")
        recipe.setIngredient('G', Material.GOLD_BLOCK)
        recipe.setIngredient('A', Material.APPLE)

        server.addRecipe(recipe)
    }

    fun add_alternateGodAppleRecipe() {
        val key = NamespacedKey(this, "god_apple_from_xp_bottle")
        val godApple = ItemStack(Material.ENCHANTED_GOLDEN_APPLE)

        val recipe = ShapelessRecipe(key, godApple)
        recipe.addIngredient(Material.EXPERIENCE_BOTTLE)
        recipe.addIngredient(Material.GOLDEN_APPLE)
        server.addRecipe(recipe)

    }

    fun add_recipeFor_XP_Bottle() {
        val key = NamespacedKey(this, "xp_bottle")
        val xpBottle = ItemStack(Material.EXPERIENCE_BOTTLE, 64)

        val recipe = ShapelessRecipe(key, xpBottle)

        recipe.addIngredient(Material.NETHER_STAR)
        recipe.addIngredient(Material.GLASS_BOTTLE)

        server.addRecipe(recipe)
    }

}

