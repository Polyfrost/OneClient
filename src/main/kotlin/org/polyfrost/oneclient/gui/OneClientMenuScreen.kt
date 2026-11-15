@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.oneclient.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.TitleScreen
//? if > 1.8.9 {
import com.mojang.realmsclient.RealmsMainScreen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.client.gui.screens.options.OptionsScreen
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen
//?} else {
/*import net.minecraft.client.gui.screen.menu.multiplayer.MultiplayerScreen as JoinMultiplayerScreen
import net.minecraft.client.gui.screen.menu.options.OptionsScreen
import net.minecraft.client.gui.screen.menu.world.SelectWorldScreen
import net.minecraft.realms.RealmsBridge
*///?}
import org.polyfrost.oneclient.OneClientConfig
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.components.ControlSize
import org.polyfrost.oneconfig.api.ui.v1.components.ControlStyle
import org.polyfrost.oneconfig.api.ui.v1.components.IconButton
import org.polyfrost.oneconfig.api.ui.v1.components.IconButtonStyle
import org.polyfrost.oneconfig.api.ui.v1.components.Text
import org.polyfrost.oneconfig.api.ui.v1.components.TextButton
import org.polyfrost.oneconfig.api.ui.v1.components.TooltipPlacement
import org.polyfrost.oneconfig.api.ui.v1.page.PageText
import org.polyfrost.oneconfig.api.ui.v1.page.resolve
import org.polyfrost.oneconfig.api.ui.v1.platform.OneConfigComposeScreen
import org.polyfrost.oneconfig.api.ui.v1.theme.OneConfigTheme

//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor
//?}

//? if < 26.1 && > 1.8.9 {
/*import net.minecraft.client.gui.GuiGraphics
*///?}

class OneClientMenuScreen : OneConfigComposeScreen(previous = null, renderMode = RenderMode.Continuous) {
    private var firstFrameDrawn = false

    init {
        OneConfigTheme.registerPixelIcons(MenuPixelIcons)
    }

    override val blurBackground: Boolean get() = false

    override val closeOnEscape: Boolean get() = false

    override val panoramaBackground: Boolean get() = true

    private var menuGuiScale by mutableStateOf(mcGuiScale())

    private fun syncGuiScaleState() {
        val gs = mcGuiScale()
        if (gs != menuGuiScale) menuGuiScale = gs
    }

    //? if = 1.8.9 {
    /*override fun render(mouseX: Int, mouseY: Int, tickDelta: Float) {
        syncGuiScaleState()
        super.render(mouseX, mouseY, tickDelta)
        MenuBackgroundPass.renderInline()
        firstFrameDrawn = true
    }
    *///?}

    //? if <26.1 && > 1.8.9 {
    /*override fun render(ctx: GuiGraphics, mouseX: Int, mouseY: Int, tickDelta: Float) {
        syncGuiScaleState()
        renderPanorama(ctx, tickDelta)
        if (firstFrameDrawn) {
            val gameRenderer = Minecraft.getInstance().gameRenderer
            //? if <1.21.4 {
            gameRenderer.processBlurEffect(tickDelta)
            //?} else {
            /^gameRenderer.processBlurEffect()
            ^///?}
        }
        MenuBackgroundPass.renderInline(ctx)
        super.render(ctx, mouseX, mouseY, tickDelta)
        firstFrameDrawn = true
    }
    *///?} elif >= 26.1 {
    override fun extractRenderState(ctx: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, tickDelta: Float) {
        syncGuiScaleState()
        Minecraft.getInstance().gameRenderer
            //? if >= 26.2 {
            .panorama()
            .extractRenderState(ctx, width, height)
            //?} else {
            /*.getPanorama()
            .extractRenderState(ctx, width, height, true)
            *///?}
        ctx.blurBeforeThisStratum()
        MenuBackgroundPass.renderInline(ctx)
        super.extractRenderState(ctx, mouseX, mouseY, tickDelta)
    }
    //?}

    //? if <26.1 && > 1.8.9 {
    /*override fun renderBackground(ctx: GuiGraphics, mouseX: Int, mouseY: Int, tickDelta: Float) {
    }
    *///?} elif >= 26.1 {
    override fun extractBackground(ctx: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, tickDelta: Float) {
    }
    //?}

    @Composable
    override fun Content() {
        val mc = Minecraft.getInstance()
        MainMenu(
            guiScale = menuGuiScale,
            actions = MenuActions(
                singleplayer = { MainMenuReplacement.openScreen(SelectWorldScreen(this)) },
                multiplayer = { MainMenuReplacement.openScreen(JoinMultiplayerScreen(this)) },
                realms = if (realmsSupported()) {
                    {
                        //? if > 1.8.9 {
                        MainMenuReplacement.openScreen(RealmsMainScreen(this))
                        //?} else {
                        /*RealmsBridge().createMainMenuScreen(this)
                        *///?}
                    }
                } else null,
                settings = {
                    //? if >= 26.3 {
                    MainMenuReplacement.openScreen(OptionsScreen(this, mc.options))
                    //?} elif >= 26.1 {
                    /*MainMenuReplacement.openScreen(OptionsScreen(this, mc.options, false))
                    *///?} else {
                    /*MainMenuReplacement.openScreen(OptionsScreen(this, mc.options))
                    *///?}
                },
                mods = { OneConfigUI.open(MODS_PAGE) },
                vanilla = {
                    OneClientConfig.useVanillaMainMenu = true
                    OneClientConfig.save()
                    MainMenuReplacement.openScreen(TitleScreen())
                },
                //? if >= 26.3 {
                fullscreen = {
                    mc.options.fullscreen().set(!mc.options.fullscreen().get())
                    mc.options.save()
                },
                //?} elif > 1.8.9 {
                /*fullscreen = { mc.window.toggleFullScreen() },
                *///?} else {
                /*fullscreen = { mc.toggleFullscreen() },
                *///?}
                quit = { mc.stop() },
            ),
        )
    }
}

private class MenuActions(
    val singleplayer: () -> Unit,
    val multiplayer: () -> Unit,
    val realms: (() -> Unit)?,
    val settings: () -> Unit,
    val mods: () -> Unit,
    val vanilla: () -> Unit,
    val fullscreen: () -> Unit,
    val quit: () -> Unit,
)

private const val ASSETS = "assets/oneclient/mainmenu/"
private const val MODS_PAGE = "oneconfig:mods"
private const val VANILLA_MENU_LABEL = "Vanilla Main Menu"
private const val BASE_WIDTH = 1240f
private const val BASE_HEIGHT = 720f
private const val REFERENCE_GUI_SCALE = 2f
private const val GUI_DENSITY_TRIM = 0.88f

private val MenuControlSize = ControlSize.ExtraLarge
private const val MEDIA_SHADOW_ALPHA = 0.2f
private const val MEDIA_SECONDARY_ALPHA = 0.6f

private val TextPrimary: Color
    @Composable get() = OneConfigTheme.current.textColor

private val OnMedia: Color
    @Composable get() = OneConfigTheme.current.onMedia

private val OnMediaSecondary: Color
    @Composable get() = OneConfigTheme.current.onMedia.let { it.copy(alpha = it.alpha * MEDIA_SECONDARY_ALPHA) }

private val MediaShadow: Color
    @Composable get() = OneConfigTheme.current.onMediaShadow.copy(alpha = MEDIA_SHADOW_ALPHA)

private val MediaTextShadow: Shadow
    @Composable get() = Shadow(OneConfigTheme.current.onMediaShadow, Offset(0f, 1f), 2f)

private val PanelShape: Shape
    @Composable
    get() = OneConfigTheme.shape(9.dp)

private val MenuPixelIcons: Map<String, String> =
    setOf("user-01", "minecraft-block").associate { "$ASSETS$it.svg" to "${ASSETS}minecraft/$it.svg" } +
        mapOf(
            "settings-02" to "cog",
            "settings-04" to "preferences",
            "users-01" to "profiles",
            "globe-01" to "cloud",
            "x-close" to "close",
            "maximize-02" to "maximise",
        ).entries.associate { (name, pixel) -> "$ASSETS$name.svg" to "/assets/oneconfig/ico/minecraft/$pixel.svg" }

private val Outfit: FontFamily by lazy {
    runCatching {
        val bytes = OneClientMenuScreen::class.java
            .getResourceAsStream("/${ASSETS}font/Outfit-Bold.ttf")!!.use { it.readBytes() }
        FontFamily(Font("Outfit-Bold", bytes, FontWeight.Bold))
    }.getOrDefault(FontFamily.Default)
}

private fun mcGuiScale(): Int = Minecraft.getInstance().window.guiScale.toInt()

private fun realmsSupported(): Boolean =
    //? if > 1.8.9 {
    runCatching { Minecraft.getInstance().allowsRealms() }.getOrDefault(false)
    //?} else {
    /*false
    *///?}

private fun platformLabel(): String = runCatching {
    val mcVersion = FabricLoader.getInstance()
        .getModContainer("minecraft").map { it.metadata.version.friendlyString }.orElse("")
    if (mcVersion.isBlank()) "Fabric" else "Fabric $mcVersion"
}.getOrDefault("Fabric")

@Composable
private fun scaledDensity(scale: Float): Density {
    val density = LocalDensity.current
    return remember(density, scale) { Density(density.density * scale, density.fontScale) }
}

@Composable
private fun Icon(path: String, color: Color, modifier: Modifier) =
    org.polyfrost.oneconfig.api.ui.v1.components.Icon(path, null, modifier, color)

@Composable
private fun MenuText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily = OneConfigTheme.current.fontFamily,
    shadow: Shadow? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center,
            shadow = shadow,
        ),
    )
}

@Composable
private fun MainMenu(guiScale: Int, actions: MenuActions) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val containFit = minOf(maxWidth.value / BASE_WIDTH, maxHeight.value / BASE_HEIGHT)
        val scale = minOf(guiScale / REFERENCE_GUI_SCALE * GUI_DENSITY_TRIM, containFit)
        val unscaled = { dp: Float -> (dp / scale).dp }
        CompositionLocalProvider(LocalDensity provides scaledDensity(scale)) {
            CenterColumn(Modifier.align(Alignment.Center), actions)
            WindowControls(Modifier.align(Alignment.TopEnd).padding(unscaled(16f)), actions)
            Footer(Modifier.fillMaxSize(), unscaled)
        }
    }
}

@Composable
private fun CenterColumn(modifier: Modifier, actions: MenuActions) {
    Column(modifier = modifier.width(440.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        MainLogo()
        Spacer(Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center) {
            MenuText("ONECLIENT", 42.sp, Modifier.offset(y = 3.dp), MediaShadow, FontWeight.Bold, 2.5.sp, Outfit)
            MenuText("ONECLIENT", 42.sp, color = OnMedia, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp, fontFamily = Outfit)
        }
        Spacer(Modifier.height(48.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            PillButton("Singleplayer", ASSETS + "user-01.svg", Modifier.fillMaxWidth(), actions.singleplayer)
            PillButton("Multiplayer", ASSETS + "users-01.svg", Modifier.fillMaxWidth(), actions.multiplayer)
            actions.realms?.let { realms ->
                PillButton("Realms", ASSETS + "globe-01.svg", Modifier.fillMaxWidth(), realms)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                PillButton("Settings", ASSETS + "settings-02.svg", Modifier.weight(1f), actions.settings)
                PillButton("Mods", ASSETS + "settings-04.svg", Modifier.weight(1f), actions.mods)
            }
        }
    }
}

@Composable
private fun MainLogo() {
    Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Icon(ASSETS + "logo.svg", MediaShadow, Modifier.size(96.dp).offset(y = 3.dp))
        Icon(ASSETS + "logo.svg", OnMedia, Modifier.size(96.dp))
    }
}

@Composable
private fun WindowControls(modifier: Modifier, actions: MenuActions) {
    val mac = remember { System.getProperty("os.name").lowercase().contains("mac") }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WindowButton(ASSETS + "minecraft-block.svg", VANILLA_MENU_LABEL, onClick = actions.vanilla)
        if (!mac) WindowButton(ASSETS + "maximize-02.svg", PageText.translatable("options.fullscreen", "Fullscreen").resolve(), onClick = actions.fullscreen)
        WindowButton(ASSETS + "x-close.svg", PageText.translatable("menu.quit", "Quit Game").resolve(), danger = true, onClick = actions.quit)
    }
}

@Composable
private fun Footer(modifier: Modifier, unscaled: (Float) -> Dp) {
    Box(modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = unscaled(24f), bottom = unscaled(18f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(ASSETS + "footer-logo.svg", OnMedia, Modifier.size(25.dp))
            FooterBrandText(platformLabel())
        }
        MenuText(
            "Copyright Mojang AB. Do not distribute!",
            13.sp,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = unscaled(24f), bottom = unscaled(18f)),
            OnMediaSecondary,
            shadow = MediaTextShadow,
        )
    }
}

@Composable
private fun FooterBrandText(platform: String) {
    val bodyFont = OneConfigTheme.current.fontFamily
    val primary = OnMedia
    val secondary = OnMediaSecondary

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = primary, fontWeight = FontWeight.Bold, fontFamily = Outfit)) {
                append("ONECLIENT")
            }
            withStyle(SpanStyle(color = secondary, fontFamily = bodyFont)) {
                append("   ")
                append(platform)
            }
        },
        style = TextStyle(
            fontSize = 13.sp,
            fontFamily = bodyFont,
            textAlign = TextAlign.Center,
            shadow = MediaTextShadow,
        ),
    )
}

@Composable
private fun PillButton(label: String, icon: String, modifier: Modifier, onClick: () -> Unit) {
    TextButton(
        label,
        icon,
        onClick,
        modifier,
        style = ControlStyle.Glass,
        size = MenuControlSize,
        shape = PanelShape,
        contentSpacing = 12.dp,
    )
}

@Composable
private fun WindowButton(icon: String, label: String, danger: Boolean = false, onClick: () -> Unit) {
    IconButton(
        icon,
        label,
        onClick,
        style = IconButtonStyle.Glass,
        size = MenuControlSize,
        danger = danger,
        tooltipPlacement = TooltipPlacement.AboveCenter,
        shape = PanelShape,
    )
}
