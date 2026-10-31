package org.polyfrost.oneclient

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch

object OneClientConfig : Config(
    "oneclient.json",
    "OneClient",
    Category.OTHER,
) {
    @JvmStatic
    @Switch(
        title = "Vanilla Main Menu",
        description = "Use the vanilla Minecraft title screen instead of the OneClient main menu.",
    )
    var useVanillaMainMenu = false
}
