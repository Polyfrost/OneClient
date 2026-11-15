@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.oneclient.gui

import net.minecraft.client.gui.screens.Screen
import org.polyfrost.oneclient.OneClientConfig
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.platform.OneConfigScreens

object MainMenuReplacement {
    @JvmStatic
    fun enabled(): Boolean = !OneClientConfig.useVanillaMainMenu

    @JvmStatic
    fun alreadyOpen(): Boolean = currentScreen() is OneClientMenuScreen

    @JvmStatic
    fun create(): Screen = OneClientMenuScreen()

    @JvmStatic
    fun open(): Boolean {
        openScreen(create())
        return alreadyOpen()
    }

    @JvmStatic
    fun currentScreen(): Screen? = OneConfigScreens.current()

    fun openScreen(screen: Screen) = OneConfigScreens.display(screen)
}
