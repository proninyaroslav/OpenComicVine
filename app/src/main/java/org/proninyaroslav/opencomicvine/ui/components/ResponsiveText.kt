package org.proninyaroslav.opencomicvine.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f

@Composable
fun ResponsiveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = style.fontSize,
    lineHeight: TextUnit = style.lineHeight,
) {
    var currentFontSize by remember { mutableStateOf(fontSize) }
    var currentLineHeight by remember { mutableStateOf(lineHeight) }

    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = currentFontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = currentLineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        style = style,
        onTextLayout = { textLayoutResult ->
            val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1
            if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
                currentFontSize = currentFontSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
                currentLineHeight = currentLineHeight.times(TEXT_SCALE_REDUCTION_INTERVAL)
            }
        }
    )
}