# Aether Mobile

Aplicativo **Android** do [Aether](https://github.com/AetherGases): é por ele que o funcionário da indústria envia o inventário de emissões da unidade, acompanha o processamento, conversa com a IA (Aeko) e que o gestor revisa, aprova ou rejeita os inventários.

Todo o tráfego passa pelo [Kong](https://github.com/AetherGases/aether-kong-gateway), o mesmo ingress do portal web.

## Funcionalidades

- **Autenticação**: onboarding, login e recuperação de senha por código (via `aether-ms-auth`). A sessão fica criptografada no aparelho e o token é renovado automaticamente.
- **Início**: resumo com o último relatório e, para o gestor, indicadores e pendências.
- **Relatórios (inventários)**: envio da planilha, acompanhamento do status, histórico, detalhe e análise com plano de melhoria. O gestor aprova ou rejeita os pendentes.
- **Aeko**: chatbot de ESG com conversas salvas e busca.
- **Calculadora**: simulação de emissões e redução com gases verdes, em formulário de três passos.
- **Notificações**: lista no app e push via Firebase Cloud Messaging.
- **Configurações e perfil**.
- **Trava de proximidade**: o app só funciona perto do local de trabalho (veja os pontos de atenção).

## Stack

| Área | Tecnologia |
| --- | --- |
| Linguagem e UI | Kotlin 2.2, Jetpack Compose (Material 3) |
| Injeção de dependência | Koin |
| Rede | Retrofit + kotlinx.serialization, OkHttp (interceptors de auth, base URL e log) |
| Navegação | Navigation Compose / Navigation 3 |
| Armazenamento local | SQLite via `SQLiteOpenHelper` (perfil), DataStore (preferências, sessão) |
| Push | Firebase Cloud Messaging |
| Imagens e mídia | Coil, Media3 |
| Build | Gradle (Kotlin DSL) com version catalog |

`minSdk` 28, `targetSdk` 36, `applicationId` `com.aether.application`.

## Arquitetura

Organização por funcionalidade (*feature*) e, dentro de cada uma, Clean Architecture em três camadas:

```text
app/src/main/java/com/aether/application
├── core/                    # infraestrutura compartilhada
│   ├── network/             # ApiClient, AuthInterceptor, TokenAuthenticator, cloudinary/
│   ├── auth/ · security/    # sessão, JWT, criptografia (AndroidEncryptor)
│   ├── database/            # SQLite (perfil local)
│   ├── notification/        # FCM
│   ├── navigation/ · di/ · ui/ (tema e componentes) · location/ · utils/
├── feature/
│   ├── auth · home · reports · chatbot · calculator
│   ├── notifications · settings · profile · onboarding
│   ├── proximity · qa
│   └── <feature>/ data (remote, repository) · domain (model, repository, usecase) · presentation (screen, viewmodel, state)
└── mainscaffold/            # casca de navegação principal
```

- `presentation` usa `ViewModel` com estado de UI imutável (`*UiState`) e telas em Compose.
- `domain` concentra modelos e casos de uso (`*UseCase`) e não conhece Android.
- `data` implementa os repositórios com Retrofit e armazenamento local.
- O upload de arquivos vai **direto ao Cloudinary** com uma assinatura obtida do `aether-ms-cloudinary` (`CloudinaryUploader`).

## URL da API

A URL base é definida por `buildType` em `app/build.gradle.kts`:

| Build | `BASE_URL` |
| --- | --- |
| `debug` | `http://10.0.2.2:8080/` (o host da máquina, visto pelo emulador) |
| `release` | `https://aethergases.org/` |

Para desenvolver sem subir o backend, use o mock em [`mockoon/aether-api.json`](mockoon/aether-api.json): abra no [Mockoon](https://mockoon.com/) e inicie na porta 8080. Com o backend real, exponha o Kong ou o `ms-auth` na 8080 do host.

## Executando

Pré-requisitos: Android Studio recente, JDK 17 e um emulador ou aparelho com Android 9+.

1. Clone: `git clone https://github.com/AetherGases/aether-mobile.git`.
2. Coloque o `google-services.json` do projeto Firebase em `app/`. O arquivo não é versionado. Peça ao time ou gere um novo projeto Firebase.
3. Abra no Android Studio e rode a variante `debug`, ou use a linha de comando:

```bash
./gradlew assembleDebug
./gradlew test lintDebug
```

## CI/CD

| Workflow | Quando | O que faz |
| --- | --- | --- |
| `android-ci.yml` | Push e PR em `main` e `develop` | Testes, `lintDebug`, APK debug (artefato). Em `develop`, publica também um *pre-release* `dev-<n>` com o APK |
| `android-cd.yml` | Manual (`workflow_dispatch`) com uma tag existente | Gera o APK de release assinado e anexa à GitHub Release |

Segredos usados: `GOOGLE_SERVICES_JSON`, `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. O `versionName` vem da tag (sem o `v`) e o `versionCode` do número da execução.

## Pontos de atenção

- **Trava de proximidade** (`feature/proximity`): o app só libera o uso se o aparelho estiver a até 1 km do endereço da unidade (ou da empresa, se a unidade não tiver endereço). Sem endereço cadastrado ou geocodificável, a trava não é aplicada. Para testar longe do local, a variante **debug** mostra o botão "QA: ignorar proximidade" (`QaProximityBypass`). A variante release traz um *no-op* no mesmo lugar, então o atalho nunca vai para produção.
- O build de release está com `isMinifyEnabled = false`.
- **Rotas sem controller correspondente.** O app chama `GET /api/inventories/seal/progress`, `GET /api/profile/problem/categories` e `POST /api/profile/problem`, que não encontrei nos controllers atuais de `ms-inventory` e `ms-auth` (o registro de problemas está em `POST /api/support`). Confirme os contratos antes de uma release.
- Mudanças de contrato nos microsserviços ([ms-auth](https://github.com/AetherGases/aether-ms-auth), [ms-inventory](https://github.com/AetherGases/aether-ms-inventory), [ms-aeko-hub](https://github.com/AetherGases/ms-aeko-hub)) precisam ser refletidas nos DTOs de `feature/*/data/remote` e no mock do Mockoon.
