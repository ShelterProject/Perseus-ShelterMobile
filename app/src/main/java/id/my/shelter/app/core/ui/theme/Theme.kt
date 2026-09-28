package id.my.shelter.app.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = ShelterBluePrimary,
    onPrimary = ShelterBlueOnPrimary,
    secondary = ShelterGreenSecondary,
    onSecondary = ShelterOnSecondary,
    tertiary = ShelterAmberTertiary,
    onTertiary = ShelterOnTertiary,
    error = ShelterError,
    onError = ShelterOnError,
    background = ShelterBackground,
    onBackground = ShelterOnBackground,
    surface = ShelterSurface,
    onSurface = ShelterOnSurface,
)

private val DarkColors = darkColorScheme(
    primary = ShelterBluePrimaryDark,
    background = ShelterBackgroundDark,
    onBackground = ShelterOnSurfaceDark,
    surface = ShelterSurfaceDark,
    onSurface = ShelterOnSurfaceDark,
)

/**
 * minSdk is 31, so dynamic color (Material You) is always available on-device;
 * the static [LightColors]/[DarkColors] only matter for previews and as a documented fallback.
 */
@Composable
fun ShelterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShelterTypography,
        content = content,
    )
}
