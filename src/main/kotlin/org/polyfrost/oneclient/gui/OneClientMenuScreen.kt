@file:OptIn(ExperimentalOneConfigUiApi::class)

package org.polyfrost.oneclient.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
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
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.sounds.SoundEvents
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
import org.polyfrost.oneconfig.internal.legacy.LegacyPanoramaTracker
*///?}
import org.polyfrost.oneclient.OneClientConfig
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.theme.OneConfigTheme
import org.polyfrost.oneconfig.api.ui.v1.theme.ProvideOneConfigTheme
import org.polyfrost.oneconfig.internal.ui.compose.ComposeScreen
import kotlin.math.cos
import kotlin.math.sin

//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor
//?}

//? if < 26.1 && > 1.8.9 {
/*import net.minecraft.client.gui.GuiGraphics
*///?}

class OneClientMenuScreen : ComposeScreen(RenderMode.CONTINUOUS) {
    private var firstFrameDrawn = false

    private var menuGuiScale by mutableStateOf(mcGuiScale())

    private fun syncGuiScaleState() {
        val gs = mcGuiScale()
        if (gs != menuGuiScale) menuGuiScale = gs
    }

    //? if > 1.8.9 {
    override fun shouldCloseOnEsc(): Boolean = false
    //?} else {
    /*override fun init() {
        legacyPanorama()
        super.init()
    }

    override fun render(mouseX: Int, mouseY: Int, tickDelta: Float) {
        syncGuiScaleState()
        super.render(mouseX, mouseY, tickDelta)
        MenuBackgroundPass.renderInline()
        firstFrameDrawn = true
    }

    private fun legacyPanorama() {
        if (LegacyPanoramaTracker.current() != null) return
        val mc = Minecraft.getInstance()
        val title = runCatching {
            TitleScreen().also { it.init(mc, mc.window.guiScaledWidth, mc.window.guiScaledHeight) }
        }.getOrNull() ?: return
        LegacyPanoramaTracker.capture(title)
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
    override fun compose() {
        val mc = Minecraft.getInstance()
        ProvideOneConfigTheme {
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
private const val BASE_WIDTH = 1240f
private const val BASE_HEIGHT = 720f
private const val REFERENCE_GUI_SCALE = 2f
private const val GUI_DENSITY_TRIM = 0.88f
private const val PANEL_BORDER_ANGLE_DEG = 20.0

private val PanelBorderWidth = 1.5.dp
private val CloseBackground = Color(0x80FF4444)

private val PanelBorderBrush: Brush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val radians = Math.toRadians(PANEL_BORDER_ANGLE_DEG)
        val ux = cos(radians).toFloat()
        val uy = sin(radians).toFloat()
        val len = size.width * ux + size.height * uy
        return LinearGradientShader(
            from = Offset.Zero,
            to = Offset(ux * len, uy * len),
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.5f),
            ),
            colorStops = listOf(0f, 0.5f, 1f),
        )
    }
}

private val PanelBackground: Color
    @Composable get() = OneConfigTheme.current.componentBackground.copy(alpha = 0.5f)

private val TextPrimary: Color
    @Composable get() = OneConfigTheme.current.textColor

private val TextSecondary: Color
    @Composable get() = OneConfigTheme.current.textColorSecondary

private val isMinecraftTheme: Boolean
    @Composable
    get() = OneConfigTheme.current.name.let { it == OneConfigTheme.MINECRAFT_DARK.name || it == OneConfigTheme.MINECRAFT_LIGHT.name }

private val PanelShape: Shape
    @Composable
    get() = if (isMinecraftTheme) RectangleShape else RoundedCornerShape(9.dp)

private val PixelIcons = setOf("user-01", "minecraft-block")
private val OneConfigPixelIcons = mapOf(
    "settings-02" to "cog",
    "settings-04" to "preferences",
    "users-01" to "profiles",
    "globe-01" to "cloud",
    "x-close" to "close",
    "maximize-02" to "maximise",
)

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

private fun playButtonClickSound() {
    val minecraft = Minecraft.getInstance()
    //? if > 1.8.9 {
    minecraft.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    //?} else {
    /*minecraft.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f, 1.0f))
    *///?}
}

private fun Modifier.clickableWithSound(onClick: () -> Unit): Modifier = clickable {
    playButtonClickSound()
    onClick()
}

@Composable
private fun scaledDensity(scale: Float): Density {
    val density = LocalDensity.current
    return remember(density, scale) { Density(density.density * scale, density.fontScale) }
}

@Composable
private fun themedIcon(path: String): String {
    if (!isMinecraftTheme) return path
    val name = path.substringAfterLast('/').removeSuffix(".svg")
    if (name in PixelIcons) return "${ASSETS}minecraft/$name.svg"
    return OneConfigPixelIcons[name]?.let { "/assets/oneconfig/ico/minecraft/$it.svg" } ?: path
}

@Composable
private fun Icon(path: String, color: Color, modifier: Modifier) =
    org.polyfrost.oneconfig.api.ui.v1.components.Icon(themedIcon(path), color, modifier)

@Composable
private fun MenuText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily = OneConfigTheme.current.fontFamily,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center,
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
            MenuText("ONECLIENT", 42.sp, Modifier.offset(y = 3.dp), Color(0x33000000), FontWeight.Bold, 2.5.sp, Outfit)
            MenuText("ONECLIENT", 42.sp, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp, fontFamily = Outfit)
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
        Icon(ASSETS + "logo.svg", Color(0x33000000), Modifier.size(96.dp).offset(y = 3.dp))
        Icon(ASSETS + "logo.svg", Color.White, Modifier.size(96.dp))
    }
}

@Composable
private fun WindowControls(modifier: Modifier, actions: MenuActions) {
    val mac = remember { System.getProperty("os.name").lowercase().contains("mac") }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        IconButton(ASSETS + "minecraft-block.svg", onClick = actions.vanilla)
        if (!mac) IconButton(ASSETS + "maximize-02.svg", onClick = actions.fullscreen)
        IconButton(ASSETS + "x-close.svg", CloseBackground, actions.quit)
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
            Icon(ASSETS + "footer-logo.svg", Color.White, Modifier.size(25.dp))
            FooterBrandText(platformLabel())
        }
        MenuText(
            "Copyright Mojang AB. Do not distribute!",
            13.sp,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = unscaled(24f), bottom = unscaled(18f)),
            TextSecondary,
        )
    }
}

@Composable
private fun FooterBrandText(platform: String) {
    val bodyFont = OneConfigTheme.current.fontFamily
    val secondary = TextSecondary

    BasicText(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Outfit)) {
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
        ),
    )
}

@Composable
private fun PillButton(label: String, icon: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(45.dp)
            .clip(PanelShape)
            .background(PanelBackground)
            .border(PanelBorderWidth, PanelBorderBrush, PanelShape)
            .clickableWithSound(onClick)
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, TextPrimary, Modifier.size(20.dp))
        MenuText(label, 16.sp)
    }
}

@Composable
private fun IconButton(icon: String, background: Color = PanelBackground, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(PanelShape)
            .background(background)
            .border(PanelBorderWidth, PanelBorderBrush, PanelShape)
            .clickableWithSound(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, TextPrimary, Modifier.size(20.dp))
    }
}
