package org.polyfrost.oneclient.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.polyfrost.oneclient.gui.MainMenuReplacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

//? if >= 26.2 {
import net.minecraft.client.gui.Gui;
//?}

//? if >= 26.2 {
@Mixin(Gui.class)
//?} else {
/*@Mixin(Minecraft.class)
*///?}
public class MixinGui {
    //? if > 1.8.9 {
    @WrapMethod(method = "setScreen")
    //?} else {
    /*@WrapMethod(method = "openScreen")
    *///?}
    private void oneclient$replaceScreen(Screen screen, Operation<Void> original) {
        if (MainMenuReplacement.enabled() && oneclient$opensTitleScreen(screen)) {
            if (!MainMenuReplacement.alreadyOpen()) {
                original.call(MainMenuReplacement.create());
            }
            return;
        }
        original.call(screen);
    }

    @Unique
    private static boolean oneclient$opensTitleScreen(Screen screen) {
        return screen instanceof TitleScreen || (screen == null && Minecraft.getInstance().player == null);
    }
}
