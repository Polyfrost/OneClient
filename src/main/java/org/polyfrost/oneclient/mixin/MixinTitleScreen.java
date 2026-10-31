package org.polyfrost.oneclient.mixin;

//? if > 1.8.9 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.screens.TitleScreen;
import org.polyfrost.oneclient.gui.MainMenuReplacement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @WrapMethod(method = "init")
    private void oneclient$replaceMainMenu(Operation<Void> original) {
        if (MainMenuReplacement.enabled() && !MainMenuReplacement.alreadyOpen()) {
            MainMenuReplacement.open();
            return;
        }
        original.call();
    }
}
//?} else {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.render.platform.GlStateManager;
import org.polyfrost.oneclient.gui.MainMenuReplacement;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @WrapMethod(method = "init")
    private void oneclient$replaceMainMenu(Operation<Void> original) {
        original.call();
        if (Minecraft.getInstance().screen != (Object) this) return;
        if (MainMenuReplacement.enabled() && !MainMenuReplacement.alreadyOpen()) {
            MainMenuReplacement.open();
        }
    }

    @Inject(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/pipeline/RenderTarget;unbindWrite()V", shift = At.Shift.AFTER))
    private void oneclient$clearDefaultFramebuffer(int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
    }
}
*///?}
