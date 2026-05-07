# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Visão Geral

Aplicativo Android nativo de negociação de gado, desenvolvido em **Java**, seguindo o padrão **MVVM** com separação clara de responsabilidades entre camadas.

- **compileSdk / targetSdk**: 36 (Android 15)
- **minSdk**: 26 (Android 8.0) — APIs abaixo de 26 não precisam de verificação de versão, exceto onde explicitamente indicado
- **Java**: 11 (source e target compatibility)

---

## Arquitetura

### Padrão: MVVM
- **Model** — dados e lógica de negócio (repositórios, entidades, fontes de dados)
- **ViewModel** — expõe dados para a UI via `LiveData`; sem referência a Views ou Context
- **View** — Fragments observam o ViewModel e apenas atualizam a UI

### Estrutura de pacotes

```
app/src/main/java/com/botoni/flow/
├── data/
│   ├── models/           # Modelos de domínio (PrecificacaoBezerro, Configuration, etc.)
│   ├── repositories/     # Toda a lógica de negócio e acesso a dados
│   └── source/
│       ├── local/
│       │   ├── dao/          # DAOs Room
│       │   ├── entities/     # Entidades Room
│       │   └── converters/   # TypeConverters Room
│       └── network/          # Integração Gespec (HTTP puro via java.net) + Google Routes API
├── di/                   # Módulos Hilt: DataModule, ExecutorModule, LocationModule
├── ui/
│   ├── fragments/        # Fragments por feature
│   ├── viewmodel/        # ViewModels por feature
│   ├── state/            # Classes de estado da UI (UiState) — POJOs imutáveis
│   ├── helpers/          # Utilitários de UI (TaskHelper, AlertHelper, etc.)
│   ├── adapters/         # RecyclerView adapters
│   ├── mappers/          # Conversão domínio ↔ UiState (padrão BiMapper)
│   └── reports/          # Builders de relatórios PDF (PdfPrecificacaoBuilder, etc.)
└── utils/
    └── pdf/              # Sistema de geração de PDF em bandas
        └── bands/
```

### Fluxo de dados

```
Fragment → ViewModel.algumMetodo()
  → taskHelper.execute(
      () -> repository.operacao(),         // background thread
      result -> uiState.setValue(result),  // UI thread via Handler
      error -> uiState.setValue(error)
    )
```

- O ViewModel injeta Repository e TaskHelper via Hilt (`@HiltViewModel`, `@Inject`)
- O Fragment observa `LiveData<UiState>` e apenas atualiza Views
- Mappers convertem entre o modelo de domínio e o UiState, isolando a UI do domínio

### Navegação (Navigation Component + Safe Args)

5 destinos no grafo `navigation.xml`, todos com animações slide_in/slide_out:

```
precificacaoFragment (start)
  ├─→ precificacaoFreteFragment    args: cargaTotal (int)
  └─→ negociacaoFragment           args: quantidadeBezerros (int), pesoMedio (String)
        ├─→ detalhePrecificacaoFragment  args: quantidadeBezerros, pesoMedio, valorTotalFrete (default "0")
        │         └─→ sucessoFragment
        └─→ sucessoFragment         args: quantidade, pesoMedio, valorTotalFrete, origemDetalhe (default false)
```

---

## Convenções de Código

### Nomenclatura
- **Classes**: `PascalCase` — `UserRepository`, `LoginViewModel`
- **Métodos e variáveis**: `camelCase` — `getUserById()`, `isLoading`
- **Constantes**: `UPPER_SNAKE_CASE` — `MAX_RETRY_COUNT`
- **Layouts XML**: `snake_case` com prefixo de tipo — `activity_login.xml`, `fragment_home.xml`, `item_user.xml`
- **IDs de View**: `camelCase` com prefixo de tipo — `tvUserName`, `btnSubmit`, `rvList`

### ViewModel
- Nunca referenciar `Activity`, `Fragment` ou `Context` diretamente no ViewModel
- Expor dados somente via `LiveData` (imutável para a View); manter `MutableLiveData` privado
- Separar `state` de `error` em LiveData distintos — não misturar erros e resultados no mesmo stream:

```java
private final MutableLiveData<PrecificacaoBezerroUiState> state = new MutableLiveData<>();
private final MutableLiveData<Throwable> error = new MutableLiveData<>();

public LiveData<PrecificacaoBezerroUiState> getState() { return state; }
public LiveData<Throwable> getError() { return error; }
```

### UiState
Classes imutáveis (POJOs sem setters) que representam o estado visível de uma tela. Criadas pelos Mappers a partir dos modelos de domínio.

```java
public class PrecificacaoBezerroUiState {
    public final BigDecimal valorPorKg;
    public final BigDecimal valorPorCabeca;
    public final BigDecimal valorTotal;

    public PrecificacaoBezerroUiState(BigDecimal valorPorKg, ...) { ... }
}
```

### Mappers
Dois contratos disponíveis em `ui/mappers/`:

```java
interface Mapper<I, O> {
    O mapper(I input);
}

interface BiMapper<I, O> extends Mapper<I, O> {
    I mapFrom(O output);   // UiState → Modelo de domínio
    O mapTo(I input);      // Modelo de domínio → UiState  (alias de mapper)
}
```

- Injetados com `@Inject` nos ViewModels
- Nunca converter domínio→UI diretamente no ViewModel ou no Fragment — sempre via mapper

### Repositórios
- Toda operação de I/O (rede, banco) deve rodar em background thread via `TaskHelper`
- Repositório não conhece ViewModel nem View

### Tratamento de erros
- Encapsular resultados em uma classe `Result<T>` com estados: `Success`, `Error`, `Loading`
- Nunca silenciar exceções com catch vazio

### Um único nível de abstração por método
Cada método deve fazer apenas uma coisa. Se um método faz mais de uma coisa, extraia as responsabilidades em métodos privados com nomes descritivos.

### Um único nível de indentação por método
Se um método tem if dentro de for dentro de if, extraia os blocos internos para métodos privados. Use early return para reduzir aninhamento.

### DRY — Don't Repeat Yourself
Nunca duplicar lógica. Se o mesmo bloco aparece em dois lugares, extraia para um método, classe utilitária ou classe base.

### Sem modularização prematura
Não crie interfaces, abstrações ou camadas extras antes de haver necessidade real.
- Não crie interfaces com uma única implementação só para "seguir boas práticas"
- Não abstraia antes de ter pelo menos dois casos de uso concretos

### Nomes que dispensam comentários
O nome do método ou variável deve revelar a intenção. Comentários explicando o que o código faz são um sinal de que o nome está ruim.

---

## Banco de Dados Room

- **Nome do banco**: `Sample.db`
- **Versão atual**: 2
- **11 entidades** registradas em `AppDatabase`
- **Seed data** carregado no `RoomDatabase.Callback.onCreate()` — popula categorias de gado (Boi, Vaca, Bezerro) e tipos de veículo (TRUK, CARRETA BAIXA, CARRETA ALTA, CARRETA TRES EIXOS)
- TypeConverter: `Date ↔ Long`
- Nomes de tabelas: prefixo `xgp_` (ex: `xgp_negociacao_gado`)
- **Migration 1→2**: adicionou colunas `agio_bezerro` e `agio_bezerra` (REAL, default 30.0) em `xgp_valor_referencia`

Nunca replicar os valores de seed em outro lugar — a fonte verdade é o callback `onCreate`.

Ao adicionar migrações, declarar o objeto `static final Migration MIGRATION_X_Y` em `AppDatabase` e registrá-lo em `.addMigrations()` no builder.

---

## Helpers — `com.botoni.flow.ui.helpers`

### `TaskHelper`
Executa operações assíncronas em background e retorna o resultado na UI thread. Injetado via Hilt.

```java
taskHelper.execute(
    () -> repository.buscarDados(),       // Callable — roda em background
    result -> liveData.setValue(result),  // onSuccess — roda na UI thread
    error -> handleError(error)           // onError — roda na UI thread
);
```

> ⚠️ **Sempre usar `TaskHelper` para I/O.** Nunca usar `AsyncTask` (depreciado) nem operações de rede/banco diretamente na UI thread.

---

### `AlertHelper`
Exibe feedback visual ao usuário. Métodos estáticos, sem instância.

```java
AlertHelper.showSnackBar(binding.root, "Operação realizada com sucesso");

AlertHelper.showDialog(context, "Excluir", "Deseja excluir este item?",
    (dialog, which) -> viewModel.excluir(),
    (dialog, which) -> dialog.dismiss()
);
```

---

### `FileHelper`
Operações com arquivos: URI segura, salvar no MediaStore (Android Q+) e compartilhar via Intent.

```java
Uri uri = FileHelper.getUri(context, file);
Uri uri = FileHelper.salvar(context, file, "application/pdf", Environment.DIRECTORY_DOCUMENTS);
FileHelper.compartilhar(activity, pdfFile, "application/pdf", "Compartilhar relatório");
```

> `salvar()` requer `Build.VERSION_CODES.Q` (API 29+). Verificar versão antes de chamar.

---

### `FormatHelper`
Parsing e formatação de valores numéricos. Locale fixo `pt-BR`. Métodos estáticos.

```java
BigDecimal val = FormatHelper.getDecimal("1.234,56"); // formato pt-BR
String formatted = FormatHelper.formatCurrency(new BigDecimal("1234.56")); // "1.234,56"
```

> Para valores monetários, sempre usar `BigDecimal`. Nunca `float`/`double` para dinheiro.

---

### `PermissionHelper`
Solicita e verifica permissões em runtime. Integra com `ActivityResultLauncher`.

```java
ActivityResultLauncher<String[]> launcher = PermissionHelper.register(this, (granted, result) -> {
    if (granted) proceedWithCamera();
    else showPermissionDeniedMessage();
});
PermissionHelper.request(requireContext(), launcher, Manifest.permission.CAMERA);
```

---

### `TextWatcherHelper`
Cria `TextWatcher` simplificados, evitando boilerplate dos três métodos obrigatórios.

```java
editText.addTextChangedListener(
    TextWatcherHelper.SimpleTextWatcher(() -> viewModel.validarFormulario())
);
// Só dispara a partir de 3 caracteres:
searchInput.addTextChangedListener(
    TextWatcherHelper.SearchTextWatcher(() -> viewModel.buscar(searchInput.getText().toString()))
);
```

---

### `ViewHelper`
Utilitários gerais de View: leitura segura de texto, visibilidade, validação de vazio e manipulação de `TextView`.

```java
String nome = ViewHelper.requireText(binding.etNome);
BigDecimal val = ViewHelper.getBigDecimal(binding.etValor);
if (ViewHelper.anyEmpty(binding.etNome, binding.etEmail)) { ... }
ViewHelper.setVisible(isLoading, binding.progressBar);
ViewHelper.setText(binding.tvDescricao, produto.getDescricao());
ViewHelper.clearText(binding.etNome, binding.etEmail, binding.etTelefone);
```

---

## Utilitário de PDF — `com.botoni.flow.utils.pdf`

Sistema de geração de PDF baseado em **bandas** (`PdfBand`). Cada banda é um bloco de conteúdo que se empilha verticalmente na página. A paginação é automática.

### Estrutura

| Classe/Interface | Responsabilidade |
|---|---|
| `PdfGenerator` | Orquestra bandas, controla paginação e grava o arquivo |
| `PdfPageConfig` | Dimensões e margens da página |
| `PdfBand` | Interface base — `getHeight()` + `draw()` |
| `PageAware` | Interface para bandas que precisam saber nº da página (ex: rodapé) |
| `PdfColors` | Paleta de cores centralizada |
| `TextAlignment` | Enum `LEFT`, `CENTER`, `RIGHT` |

### Bandas disponíveis (`bands/`)

| Banda | Uso |
|---|---|
| `TitleBand` | Título grande com separador horizontal opcional |
| `TextBand` | Parágrafo de texto com alinhamento e padding configurável |
| `RowBand` | Linha de tabela com colunas por peso (`weight`). Modo `asHeader()` adiciona fundo |
| `SpacerBand` | Espaço em branco com altura fixa |
| `FooterBand` | Rodapé com ID do documento à esquerda e "Pág. 01/03" à direita. Implementa `PageAware` |

### Exemplo completo

```java
PdfPageConfig config = PdfPageConfig.a4Portrait(); // 595x842, margens 50f

File pdf = new PdfGenerator(config)
    .setFooter(new FooterBand("REL-2024-001"))
    .addBand(new TitleBand("Relatório de Vendas"))
    .addBand(new SpacerBand(8f))
    .addBand(new RowBand(9f, 20f,
        new RowBand.Column("Produto",    3f, TextAlignment.LEFT),
        new RowBand.Column("Qtd",        1f, TextAlignment.CENTER),
        new RowBand.Column("Valor",      1f, TextAlignment.RIGHT)
    ).asHeader())
    .addBand(new RowBand(9f, 18f,
        new RowBand.Column("Notebook",   3f, TextAlignment.LEFT),
        new RowBand.Column("2",          1f, TextAlignment.CENTER),
        new RowBand.Column("R$ 3.200,00",1f, TextAlignment.RIGHT)
    ))
    .generate(context, "relatorio.pdf");

// O arquivo fica em context.getFilesDir()/pdfs/relatorio.pdf
FileHelper.compartilhar(activity, pdf, "application/pdf", "Compartilhar relatório");
```

### `PdfColors` — Paleta

| Constante | Hex | Uso |
|---|---|---|
| `GRAPHITE` | `#1A1A1A` | Texto principal |
| `SECONDARY` | `#757575` | Texto secundário, rodapé |
| `SURFACE` | `#F2F2F2` | Fundo de cabeçalho de tabela |
| `RULE` | `#D0D0D0` | Linhas divisórias |

> Sempre usar `PdfColors` para cores no PDF. Nunca hardcodar valores `0xFF...` fora desta classe.

---

## Integração de Rede

### Google Routes API
`RoutesDataSource` chama `https://routes.googleapis.com/directions/v2:computeRoutes` via `java.net.HttpURLConnection`. A chave de API é lida do `AndroidManifest` via meta-data, injetada pelo plugin `secrets-gradle-plugin` a partir de `local.defaults.properties`.

### Gespec
`GespecSyncAcessoService` e `GespecSyncUsuarioService` sincronizam dados de acesso e usuário com o backend Gespec via HTTP puro.

### Chaves de API
Armazenadas em `local.properties` (não versionado). O arquivo `local.defaults.properties` define os nomes das variáveis sem os valores. Nunca hardcodar chaves no código.

---

## Regras de Negócio detalhadas

As regras de negócio com nível de detalhe mais aprofundado (entradas, saídas, invariantes e exemplos) estão em **`app/CLAUDE.md`**. O arquivo raiz resume; `app/CLAUDE.md` é a fonte de verdade para o domínio.

---

## Regras de Negócio

### Precificação de Bezerros

O **valor por arroba** embute implicitamente o custo de frete e logística, que varia por região. Por isso existem dois modos de cálculo:

| Modo | O que representa |
|---|---|
| **Com frete** | Valor bruto do bezerro — frete já embutido no arroba |
| **Sem frete** | Valor real do bezerro a ser negociado — frete descontado do valor/kg |

#### Constantes fixas de negócio

Estas constantes refletem premissas do mercado e **não devem ser alteradas sem revisão do domínio**:

| Constante | Valor | Significado |
|---|---|---|
| `PESO_ARROBA_KG` | 30 kg | Peso padrão de uma arroba |
| `ARROBAS_ABATE_ESPERADAS` | 21 arrobas | Projeção de arrobas no abate |
| `TAXA_FIXA_ABATE` | R$ 69,70 | Taxa fixa cobrada no frigorífico |
| `IMPOSTO_FUNRURAL` | 1,5% | Imposto sobre a venda no abate |

#### Lógica do Ágio

O ágio representa o prêmio pago pelo potencial de ganho de peso até o abate. Ele incide de forma diferente dependendo de onde o peso do animal está em relação ao `pesoBase`:

- **Bezerro acima ou no peso base**: o ágio é calculado diretamente sobre as arrobas restantes até o abate.
- **Bezerro abaixo do peso base**: o ágio é acumulado faixa a faixa — a cada arroba inteira de diferença, calcula-se a diferença de ágio naquele intervalo.

#### Modos de cálculo

**Sem frete** — `calcularNegociacaoBezerro(..., valorFrete, ...)`:
```
valorPorKg     = (valorTotalBezerroComFrete / peso) - valorFrete
valorPorCabeca = valorPorKg × peso
valorTotal     = valorPorCabeca × quantidade
```

**Com frete** — `calcularNegociacaoBezerroComFrete(...)`:
```
valorPorCabeca = valorBasePorPeso + valorTotalAgio
valorPorKg     = valorPorCabeca / peso
valorTotal     = valorPorCabeca × quantidade
```

> - O `valorFrete` é uma **incidência por kg**, não um valor fixo por cabeça ou lote.
> - Peso zero deve retornar `BigDecimal.ZERO` sem lançar exceção.
> - **Nunca alterar** `PrecificacaoBezerroRepository` sem revisão das premissas de domínio.

### Frete

4 tipos de veículo: `TRUK`, `CARRETA BAIXA`, `CARRETA ALTA`, `CARRETA TRES EIXOS`.  
3 categorias de gado: `Boi`, `Vaca`, `Bezerro`.  
2 modos de cobrança: tarifa fixa por faixa de distância (0–300 km) / tarifa por km (acima de 300 km).

As tabelas de capacidade por veículo e as tarifas estão seed-adas na criação do banco (`AppDatabase.onCreate`). Não replicar esses valores em outro lugar.

---

## Proibido

- Nunca usar `AsyncTask` (depreciado) — usar `TaskHelper`
- Nunca usar `findViewById` — usar `ViewBinding`
- Nunca fazer operações de rede ou banco na UI thread
- Nunca criar interfaces com uma única implementação sem necessidade real
- Nunca adicionar dependências sem alinhar com o time
- Nunca silenciar exceções com `catch` vazio
- Nunca hardcodar strings visíveis ao usuário — usar `res/values/strings.xml`
- Nunca hardcodar cores no PDF — usar `PdfColors`
- Nunca fazer commit de `local.properties` ou `secrets.properties`

---

## Comandos Principais

```bash
# Build de debug
./gradlew assembleDebug

# Build de release
./gradlew assembleRelease

# Testes unitários
./gradlew test

# Rodar um único teste unitário
./gradlew test --tests "com.botoni.flow.ExampleUnitTest"

# Testes de instrumentação (requer dispositivo/emulador)
./gradlew connectedAndroidTest

# Rodar um único teste de instrumentação
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.botoni.flow.PrecificacaoBezerroTest

# Limpar build
./gradlew clean
```

Os testes de instrumentação usam `@HiltAndroidTest` + `HiltAndroidRule` e requerem `CustomTestRunner` configurado no `build.gradle` como `testInstrumentationRunner`.

---

## Dependências Principais

| Biblioteca | Uso |
|---|---|
| Room 2.8.4 | Persistência local (ORM) |
| ViewModel / LiveData | Arquitetura MVVM |
| Hilt 2.59.2 | Injeção de dependência |
| Navigation Component 2.9.7 | Navegação entre Fragments (Safe Args) |
| Material Design 1.13.0 | Componentes de UI |
| GSON 2.13.2 | Serialização JSON (integração Gespec) |
| Google Play Services Location 21.3.0 / Maps 20.0.0 | Localização e rotas |
| ViewBinding / DataBinding | Acesso a Views sem `findViewById` |
| Secrets Gradle Plugin 2.0.1 | Gerenciamento de chaves de API via `local.properties` |
| Mockito 5.23.0 / MockK 1.14.9 / Espresso 3.7.0 | Testes |