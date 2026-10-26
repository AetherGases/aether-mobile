package com.aether.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private sealed interface MdBlock {
    data class Paragraph(val text: String) : MdBlock
    data class Heading(val level: Int, val text: String) : MdBlock
    data class ListItem(val marker: String, val text: String, val indent: Int) : MdBlock
    data class Quote(val text: String) : MdBlock
    data class Code(val code: String) : MdBlock
}

private val headingRegex = Regex("^(#{1,6})\\s+(.*)$")
private val listRegex = Regex("^(\\s*)([-*+]|\\d+[.)])\\s+(.*)$")
private val quoteRegex = Regex("^>\\s?(.*)$")

private fun parseBlocks(markdown: String): List<MdBlock> {
    val blocks = mutableListOf<MdBlock>()
    val paragraph = mutableListOf<String>()
    var code: MutableList<String>? = null

    fun flushParagraph() {
        if (paragraph.isNotEmpty()) {
            blocks += MdBlock.Paragraph(paragraph.joinToString("\n"))
            paragraph.clear()
        }
    }

    for (line in markdown.lines()) {
        if (line.trimStart().startsWith("```")) {
            val open = code
            if (open == null) {
                flushParagraph()
                code = mutableListOf()
            } else {
                blocks += MdBlock.Code(open.joinToString("\n"))
                code = null
            }
            continue
        }
        code?.let { it += line; continue }

        val heading = headingRegex.matchEntire(line)
        val item = listRegex.matchEntire(line)
        val quote = quoteRegex.matchEntire(line)
        when {
            line.isBlank() -> flushParagraph()
            heading != null -> {
                flushParagraph()
                blocks += MdBlock.Heading(heading.groupValues[1].length, heading.groupValues[2])
            }
            item != null -> {
                flushParagraph()
                val marker = item.groupValues[2].let { if (it[0].isDigit()) it else "•" }
                blocks += MdBlock.ListItem(marker, item.groupValues[3], item.groupValues[1].length / 2)
            }
            quote != null -> {
                flushParagraph()
                blocks += MdBlock.Quote(quote.groupValues[1])
            }
            else -> paragraph += line
        }
    }
    code?.let { blocks += MdBlock.Code(it.joinToString("\n")) }
    flushParagraph()
    return blocks
}

private val inlineRegex = Regex(
    "`([^`]+)`" +                                    // 1: code
        "|\\*\\*(.+?)\\*\\*|__(.+?)__" +             // 2,3: bold
        "|~~(.+?)~~" +                               // 4: strikethrough
        "|\\*(?!\\s)(.+?)(?<!\\s)\\*" +              // 5: italic
        "|(?<![\\p{L}\\p{N}])_(?!\\s)(.+?)(?<!\\s)_(?![\\p{L}\\p{N}])" + // 6: italic
        "|\\[([^\\]]+)]\\((\\S+?)\\)"                // 7,8: link
)

private fun inlineMarkdown(
    text: String,
    codeBackground: Color,
    linkColor: Color
): AnnotatedString = buildAnnotatedString {
    fun appendInline(source: String) {
        var last = 0
        for (match in inlineRegex.findAll(source)) {
            append(source.substring(last, match.range.first))
            val g = match.groupValues
            when {
                g[1].isNotEmpty() -> withStyle(
                    SpanStyle(fontFamily = FontFamily.Monospace, background = codeBackground)
                ) { append(g[1]) }
                g[2].isNotEmpty() || g[3].isNotEmpty() ->
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(g[2].ifEmpty { g[3] }) }
                g[4].isNotEmpty() ->
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(g[4]) }
                g[5].isNotEmpty() || g[6].isNotEmpty() ->
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(g[5].ifEmpty { g[6] }) }
                else -> withLink(
                    LinkAnnotation.Url(
                        g[8],
                        TextLinkStyles(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
                    )
                ) { append(g[7]) }
            }
            last = match.range.last + 1
        }
        append(source.substring(last))
    }
    appendInline(text)
}

@Composable
fun MarkdownText(
    markdown: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    val blocks = remember(markdown) { parseBlocks(markdown) }
    val codeBackground = color.copy(alpha = 0.1f)
    val linkColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MdBlock.Paragraph -> Text(
                    text = inlineMarkdown(block.text, codeBackground, linkColor),
                    style = style,
                    color = color
                )
                is MdBlock.Heading -> Text(
                    text = inlineMarkdown(block.text, codeBackground, linkColor),
                    style = style.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = when (block.level) {
                            1 -> style.fontSize * 1.4f
                            2 -> style.fontSize * 1.25f
                            3 -> style.fontSize * 1.1f
                            else -> style.fontSize
                        }
                    ),
                    color = color
                )
                is MdBlock.ListItem -> Row(modifier = Modifier.padding(start = (block.indent * 12).dp)) {
                    Text(text = block.marker, style = style, color = color, modifier = Modifier.width(24.dp))
                    Text(
                        text = inlineMarkdown(block.text, codeBackground, linkColor),
                        style = style,
                        color = color
                    )
                }
                is MdBlock.Quote -> Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Column(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .background(color.copy(alpha = 0.4f))
                    ) {}
                    Text(
                        text = inlineMarkdown(block.text, codeBackground, linkColor),
                        style = style.copy(fontStyle = FontStyle.Italic),
                        color = color,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                is MdBlock.Code -> Text(
                    text = block.code,
                    style = style.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                    color = color,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(codeBackground)
                        .padding(8.dp)
                )
            }
        }
    }
}
