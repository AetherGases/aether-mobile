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
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AetherTopBar(
            title = "Política de Privacidade",
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

            SectionTitle("1. Introdução")
            Paragraph("O Aether respeita sua privacidade e está comprometido com a proteção dos seus dados pessoais, em conformidade com a Lei Geral de Proteção de Dados (LGPD — Lei 13.709/2018).")

            SectionTitle("2. Quais dados coletamos")
            Bullet("Dados de cadastro: nome, e-mail, cargo, empresa")
            Bullet("Dados de uso: relatórios enviados, simulações realizadas, histórico de acesso")
            Bullet("Dados técnicos: tipo de dispositivo, versão do app, logs de erro")

            Spacer(Modifier.height(12.dp))

            SectionTitle("3. Como usamos seus dados")
            Bullet("Personalizar a experiência do usuário no app")
            Bullet("Gerar análises e recomendações de gases verdes")
            Bullet("Enviar notificações sobre relatórios e atualizações")
            Bullet("Cumprir obrigações legais e regulatórias")

            Spacer(Modifier.height(12.dp))

            SectionTitle("4. Compartilhamento de dados")
            Bullet("Os dados não são vendidos a terceiros")
            Bullet("Podem ser compartilhados com gerentes, analistas e administradores autorizados da empresa cadastrada")
            Bullet("Podem ser compartilhados com autoridades reguladoras quando exigido por lei")

            Spacer(Modifier.height(12.dp))

            SectionTitle("5. Seus direitos (LGPD)")
            Bullet("Acessar seus dados pessoais")
            Bullet("Corrigir dados incorretos")
            Bullet("Solicitar exclusão dos dados")
            Bullet("Revogar consentimento a qualquer momento")
            Bullet("Solicitar portabilidade dos dados")

            Spacer(Modifier.height(12.dp))

            SectionTitle("6. Cookie e rastreamento")
            Bullet("O app não utiliza cookies de terceiros")
            Bullet("Dados de uso são coletados apenas para melhoria da plataforma")

            Spacer(Modifier.height(12.dp))

            SectionTitle("7. Contato")
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, heightDp = 1700)
@Composable
fun PrivacyPolicyScreenPreview() {
    AetherTheme {
        PrivacyPolicyScreen()
    }
}