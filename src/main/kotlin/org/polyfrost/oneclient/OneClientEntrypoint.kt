package org.polyfrost.oneclient

import net.fabricmc.api.ClientModInitializer

class OneClientEntrypoint : ClientModInitializer {
    override fun onInitializeClient() {
        OneClientConfig.preload()
    }
}
