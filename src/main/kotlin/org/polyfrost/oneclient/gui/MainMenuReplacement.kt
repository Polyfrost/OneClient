package org.polyfrost.oneclient.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import org.polyfrost.oneclient.OneClientConfig

object MainMenuReplacement {
    @JvmStatic
    fun enabled(): Boolean = !OneClientConfig.useVanillaMainMenu

    @JvmStatic
    fun alreadyOpen(): Boolean = currentScreen() is OneClientMenuScreen

    @JvmStatic
    fun create(): Screen = OneClientMenuScreen()

    @JvmStatic
    fun open() = openScreen(create())

    @JvmStatic
    fun currentScreen(): Screen? =
        //? if >= 26.2 {
        Minecraft.getInstance().gui.screen()
        //?} else {
        /*Minecraft.getInstance().screen
        *///?}

    fun openScreen(screen: Screen) {
        val mc = Minecraft.getInstance()
        //? if >= 26.2 {
        mc.gui.setScreen(screen)
        //?} elif > 1.8.9 {
        /*mc.setScreen(screen)
        *///?} else {
        /*mc.openScreen(screen)
        *///?}
    }
}
