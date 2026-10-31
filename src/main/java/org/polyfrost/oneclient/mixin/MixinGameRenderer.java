package org.polyfrost.oneclient.mixin;

//? if > 1.8.9 {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.GameRenderer;
import org.polyfrost.oneclient.gui.MainMenuReplacement;
import org.polyfrost.oneclient.gui.OneClientMenuScreen;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//?} else {
/*import net.minecraft.client.render.GameRenderer;
*///?}
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    //? if > 1.8.9 {
    @Unique
    private static final int ONECLIENT_PANORAMA_BLUR_RADIUS = 7;

    @ModifyExpressionValue(
        //? if >= 26.1 {
        method = "extractOptions",
        //?} elif >= 1.21.8 {
        /*method = "render",
        *///?} else {
        /*method = "processBlurEffect",
        *///?}
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getMenuBackgroundBlurriness()I")
    )
    private int oneclient$forcePanoramaBlurRadius(int original) {
        return MainMenuReplacement.currentScreen() instanceof OneClientMenuScreen ? ONECLIENT_PANORAMA_BLUR_RADIUS : original;
    }
    //?}
}
