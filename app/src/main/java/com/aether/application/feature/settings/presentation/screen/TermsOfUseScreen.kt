package com.aether.application.feature.settings.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aether.core.ui.components.AetherTopBar
import com.aether.core.ui.theme.*

@Composable
fun TermsOfUseScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AetherTopBar(
            title = "Termos de uso",
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
                text = "Atualizado em 01/01/2026",
                style = bodyLargeMedium,
                color = textSecondaryLight
            )

            Spacer(Modifier.height(32.dp))

            SectionTitle("1. Aceitação dos termos")
            Paragraph("Ao utilizar o aplicativo Aether, você concorda com os termos e condições descritos neste documento. Caso não concorde, recomendamos que encerre o uso do aplicativo.")

            SectionTitle("2. Sobre o Aether")
            Paragraph("O Aether é uma plataforma de gestão de emissões de gases de efeito estufa voltada para empresas do setor corporativo. Oferece ferramentas de análise, calculadora de CO₂ e orientações sobre gases verdes para substituição.")

            SectionTitle("3. Uso da plataforma")
            Bullet("O usuário é responsável pela veracidade dos dados inseridos nos relatórios")
            Bullet("É proibido inserir dados falsos ou manipulados")
            Bullet("O acesso é pessoal e intransferível")
            Bullet("O compartilhamento de login é proibido")

            Spacer(Modifier.height(12.dp))

            SectionTitle("4. Responsabilidades do Aether")
            Bullet("Os resultados da calculadora são estimativas baseadas em dados científicos, não substituem laudos técnicos")
            Bullet("O Aether não se responsabiliza por decisões tomadas exclusivamente com base nas análises geradas pelo app")
            Bullet("A plataforma pode ficar indisponível para manutenção com aviso prévio")

            Spacer(Modifier.height(12.dp))

            SectionTitle("5. Alterações nos termos")
            Bullet("O Aether pode atualizar estes termos com aviso prévio de 15 dias")
            Bullet("O uso continuado após a atualização indica aceitação")

            Spacer(Modifier.height(12.dp))

            SectionTitle("6. Contato")
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = textSecondaryLight)) {
                        append("E-mail: ")
                    }
                    withStyle(style = SpanStyle(color = textPrimaryLight,)) {
                        append("grupoaetherjef@gmail.com")
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
        color = textPrimaryLight,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun Paragraph(text: String) {
    Text(
        text = text,
        style = bodyLargeMedium,
        color = textTertiaryLight,
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
            color = textSecondaryLight,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = bodyLarge,
            color = textSecondaryLight
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