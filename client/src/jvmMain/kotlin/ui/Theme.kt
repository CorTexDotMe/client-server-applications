package ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable


private val LightColors = Colors(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryVariant = md_theme_light_primaryContainer,
//    onPrimaryVariant = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryVariant = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
//    tertiary = md_theme_light_tertiary,
//    onTertiary = md_theme_light_onTertiary,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
//    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
//    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
//    surfaceVariant = md_theme_light_surfaceVariant,
//    onSurfaceVariant = md_theme_light_onSurfaceVariant,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    surfaceTintColor = md_theme_light_surfaceTintColor,
    isLight = true
)

/**
 * Create a Material theme for the application
 */
@Composable
fun WroteTheme(
    content: @Composable() () -> Unit
) {
    MaterialTheme(
        colors = LightColors,
        content = { content() })
}