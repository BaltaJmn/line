package com.baltajmn.line.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import purl.shared.generated.resources.Res
import purl.shared.generated.resources.literata_regular

val MAX_CONTENT_WIDTH = 600.dp

/** The family's eight pastels, in the family's order. Sage is free and the default. */
enum class Cover(val id: String, val color: Color) {
    ROSE("rose", Color(0xFFF0AFBE)),
    PEACH("peach", Color(0xFFF5C39B)),
    BUTTER("butter", Color(0xFFEDDC98)),
    SAGE("sage", Color(0xFFB6D6AB)),
    MINT("mint", Color(0xFF9CD3C7)),
    SKY("sky", Color(0xFFA2C3E9)),
    PERIWINKLE("periwinkle", Color(0xFFB4B8EC)),
    LILAC("lilac", Color(0xFFD9AFE6)),
    ;

    companion object {
        /** An unknown id reads as sage. */
        fun of(id: String?): Cover = entries.firstOrNull { it.id == id } ?: SAGE
    }
}

// error points at the secondary text colour: nothing in the app is ever painted red, not even by a
// Material component that reaches for it on its own.
internal val Light = lightColorScheme(
    primary = Color(0xFF6FAE9B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9EDE5),
    onPrimaryContainer = Color(0xFF23453B),
    secondary = Color(0xFFC2A8D4),
    background = Color(0xFFFBF8F3),
    onBackground = Color(0xFF39352E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF39352E),
    surfaceVariant = Color(0xFFF0EBE2),
    onSurfaceVariant = Color(0xFF8B8479),
    outline = Color(0xFFE3DCD1),
    outlineVariant = Color(0xFFEFE9DF),
    error = Color(0xFF8B8479),
)

internal val Dark = darkColorScheme(
    primary = Color(0xFF8FC9B6),
    onPrimary = Color(0xFF12271F),
    primaryContainer = Color(0xFF2B4A40),
    onPrimaryContainer = Color(0xFFD9EDE5),
    secondary = Color(0xFFC7B2D8),
    background = Color(0xFF17150F),
    onBackground = Color(0xFFECE5D9),
    surface = Color(0xFF201D16),
    onSurface = Color(0xFFECE5D9),
    surfaceVariant = Color(0xFF2C2820),
    onSurfaceVariant = Color(0xFF9C9486),
    outline = Color(0xFF3A352B),
    outlineVariant = Color(0xFF2C2820),
    error = Color(0xFF9C9486),
)

private val SoftShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun LineTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, shapes = SoftShapes, content = content)
}

/**
 * The ten styles of docs/pantallas.md 1.2. Literata is only for what the user wrote; everything
 * around it is the system font, lighter and smaller, so the line is what the eye lands on.
 */
object Styles {
    val literata: FontFamily @Composable get() = FontFamily(Font(Res.font.literata_regular))

    val userLarge: TextStyle @Composable get() = user(22.sp, 32.sp)
    val userMedium: TextStyle @Composable get() = user(18.sp, 27.sp)
    val userSmall: TextStyle @Composable get() = user(16.sp, 24.sp)

    val title: TextStyle @Composable get() = system(20.sp, 26.sp, FontWeight.Medium, colors.onBackground)
    val dateLine: TextStyle @Composable get() = system(15.sp, 20.sp, FontWeight.Medium, colors.onSurfaceVariant)
    val body: TextStyle @Composable get() = system(15.sp, 22.sp, FontWeight.Normal, colors.onBackground)
    val secondary: TextStyle @Composable get() = system(13.sp, 18.sp, FontWeight.Normal, colors.onSurfaceVariant)
    val light: TextStyle @Composable get() = system(13.sp, 18.sp, FontWeight.Light, colors.onSurfaceVariant)
    val action: TextStyle @Composable get() = system(15.sp, 20.sp, FontWeight.Medium, colors.primary)

    /** Block and section labels. The caller passes the text through uppercase(). */
    val eyebrow: TextStyle @Composable get() =
        system(11.sp, 14.sp, FontWeight.Medium, colors.onSurfaceVariant).copy(letterSpacing = 1.4.sp)

    private val colors @Composable get() = MaterialTheme.colorScheme

    @Composable
    private fun user(size: TextUnit, line: TextUnit) =
        TextStyle(fontFamily = literata, fontSize = size, lineHeight = line, color = colors.onBackground)

    private fun system(size: TextUnit, line: TextUnit, weight: FontWeight, color: Color) =
        TextStyle(fontSize = size, lineHeight = line, fontWeight = weight, color = color)
}
