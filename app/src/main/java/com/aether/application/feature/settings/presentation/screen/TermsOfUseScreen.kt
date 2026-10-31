package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.application.R
import com.aether.application.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*

@Composable
fun TermsOfUseScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AetherTopBar(
            title = stringResource(R.string.terms_title),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 40.dp)
        ) {
            Text(
                text = stringResource(R.string.legal_updated_on),
                style = bodyLargeMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            SectionTitle(stringResource(R.string.terms_section1_title))
            Paragraph(stringResource(R.string.terms_section1_paragraph))

            SectionTitle(stringResource(R.string.terms_section2_title))
            Paragraph(stringResource(R.string.terms_section2_paragraph))

            SectionTitle(stringResource(R.string.terms_section3_title))
            Bullet(stringResource(R.string.terms_section3_bullet1))
            Bullet(stringResource(R.string.terms_section3_bullet2))
            Bullet(stringResource(R.string.terms_section3_bullet3))
            Bullet(stringResource(R.string.terms_section3_bullet4))

            Spacer(Modifier.height(12.dp))

            SectionTitle(stringResource(R.string.terms_section4_title))
            Bullet(stringResource(R.string.terms_section4_bullet1))
            Bullet(stringResource(R.string.terms_section4_bullet2))
            Bullet(stringResource(R.string.terms_section4_bullet3))

            Spacer(Modifier.height(12.dp))

            SectionTitle(stringResource(R.string.terms_section5_title))
            Bullet(stringResource(R.string.terms_section5_bullet1))
            Bullet(stringResource(R.string.terms_section5_bullet2))

            Spacer(Modifier.height(12.dp))

            SectionTitle(stringResource(R.string.terms_section6_title))
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append(stringResource(R.string.legal_email_label))
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append(stringResource(R.string.legal_contact_email))
                    }
                },
                style = bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = text,
        style = bodyLargeMedium,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

@Composable
private fun Bullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "•",
            style = bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1400)
@Composable
fun TermsOfUseScreenPreview() {
    AetherTheme {
        TermsOfUseScreen()
    }
}