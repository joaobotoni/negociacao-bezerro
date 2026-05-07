# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Visão Geral

Aplicativo Android nativo de negociação de gado, desenvolvido em **Java**, seguindo o padrão **MVVM** com separação clara de responsabilidades entre camadas.

- **Pacote base**: `com.example.myapplication`
- **compileSdk / targetSdk**: 36 (Android 15)
- **minSdk**: 24 (Android 7.0)
- **Java**: 11 (source e target compatibility)

---

## Arquitetura

### Padrão: MVVM
- **Model** — dados e lógica de negócio (repositórios, entidades, fontes de dados)
- **ViewModel** — expõe dados para a UI via `LiveData`; sem referência a Views ou Context
- **View** — Fragments observam o ViewModel e apenas atualizam a UI

### Estrutura de pacotes

```
app/src/main/java/com/example/myapplication/
├── data/
│   ├── models/           # Modelos de domínio (PrecificacaoBezerro, Rota, Transporte, etc.)
│   ├── repositories/     # Toda a lógica de negócio e acesso a dados (16 repositórios)
│   └── source/
│       ├── local/
│       │   ├── dao/          # DAOs Room (12 DAOs)
│       │   ├── entities/     # Entidades Room (12 entidades)
│       │   └── converters/   # TypeConverters Room
│       └── remote/
│           ├── gespec/       # Data sources Gespec via Retrofit
│           └── retrofit/     # Service interfaces Retrofit (Gespec + Routes)
├── di/                   # Módulos Hilt: DataModule, ExecutorModule, LocationModule
├── ui/
│   ├── fragments/        # Fragments por feature (3 principais + 3 BottomSheetDialogFragment)
│   ├── viewmodel/        # ViewModels por feature
│   ├── state/            # Classes de estado da UI (UiState) — POJOs imutáveis
│   ├── helpers/          # Utilitários de UI (TaskHelper, AlertHelper, etc.)
│   ├── adapters/         # RecyclerView adapters
│   └── mappers/          # (não usado — mappers estão em utils/mappers/domain/)
└── utils/
    ├── mappers/
    │   └── domain/       # Implementações de Mapper e BiMapper por entidade
    ├── pdf/              # Sistema de geração de PDF em bandas
    │   └── bands/
    └── BigDecimalUtil.java
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

### Strategy Pattern — Precificação de Bezerros

O cálculo de precificação usa o padrão Strategy para isolar cada modo de cálculo:

- **Contrato:** `domain/contract/PrecificacaoBezerroStrategy` — `calcular(peso, quantidade, parametros)`
- **Orquestrador:** `domain/implementation/PrecificacaoBezerroImplementation` — carrega `ParametrosBezerro` do banco e delega ao strategy selecionado
- **Estratégias concretas** em `domain/strategy/`:
  - `PrecificacaoBezerroComFrete` — frete embutido no arroba
  - `PrecificacaoBezerroSemFrete` — frete descontado do valor/kg
  - `PrecificacaoBezerroComFreteEComissao` — frete + comissão de corretor

Ao adicionar um novo modo de cálculo, implemente `PrecificacaoBezerroStrategy` e injete a implementação onde necessário — nunca adicione a lógica diretamente no ViewModel ou repositório.

---

### Navegação (Navigation Component + Safe Args)

3 destinos principais no grafo `navigation.xml`:

```
simulacaoFragment (start)
  └─→ negociacaoFragment    args: cargaTotal (int), pesoMedio (float)
        └─→ simulacaoFreteeFragment    args: cargaTotal (int)
```

Modais são implementadas como `BottomSheetDialogFragment` (BuscaLocalizacao, Corretor, Empresa) — abertas via `show()`, fora do grafo de navegação.

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
private final MutableLiveData<PrecificacaoFreteUiState> state = new MutableLiveData<>();
private final MutableLiveData<Throwable> error = new MutableLiveData<>();

public LiveData<PrecificacaoFreteUiState> getState() { return state; }
public LiveData<Throwable> getError() { return error; }
```

### UiState
Classes imutáveis (POJOs sem setters) que representam o estado visível de uma tela. Criadas pelos Mappers a partir dos modelos de domínio.

```java
public class PrecificacaoFreteUiState {
    public final String veiculoDescricao;
    public final BigDecimal valorTotal;

    public PrecificacaoFreteUiState(String veiculoDescricao, BigDecimal valorTotal) { ... }
}
```

### Mappers
Contrato único em `utils/mappers/`:

```java
public interface Mapper<I, O> {
    O mapTo(I i);    // Modelo de domínio → UiState
    I mapFrom(O o);  // UiState → Modelo de domínio
}
```

Implementações ficam em `utils/mappers/domain/`. Injetados com `@Inject` nos ViewModels. Nunca converter domínio→UI diretamente no ViewModel ou no Fragment — sempre via mapper.

### Repositórios
- Toda operação de I/O (rede, banco) deve rodar em background thread via `TaskHelper`
- Repositório não conhece ViewModel nem View

### Tratamento de erros
- Nunca silenciar exceções com catch vazio

### Um único nível de abstração por método
Cada método deve fazer apenas uma coisa. Se faz mais, extraia responsabilidades em métodos privados com nomes descritivos.

### Um único nível de indentação por método
Se um método tem if dentro de for dentro de if, extraia os blocos internos para métodos privados. Use early return para reduzir aninhamento.

### DRY — Don't Repeat Yourself
Nunca duplicar lógica. Se o mesmo bloco aparece em dois lugares, extraia para um método, classe utilitária ou classe base.

### Sem modularização prematura
Não crie interfaces com uma única implementação só para "seguir boas práticas". Não abstraia antes de ter pelo menos dois casos de uso concretos.

---

## Banco de Dados Room

- **Nome do banco**: `Sample.db`
- **Versão atual**: 1
- **12 entidades** registradas em `AppDatabase`: `Frete`, `CapacidadeFrete`, `CategoriaFrete`, `TipoVeiculoFrete`, `ValorReferencia`, `TipoReferencia`, `NegociacaoGado`, `NegociacaoAnimal`, `Empresa`, `Corretor`, `CategoriaNegociacao`, `Raca`
- **Seed data** carregado no `RoomDatabase.Callback.onCreate()` — popula categorias de gado (Boi, Vaca, Bezerro), tipos de veículo, tabelas de capacidade/frete, tipos de referência, empresas padrão, corretores e raças
- TypeConverter: `Date ↔ Long`
- Nomes de tabelas: prefixo `xgp_` (ex: `xgp_negociacao_gado`), exceto `empresa` e `xgp_corretor`

Nunca replicar os valores de seed em outro lugar — a fonte verdade é o callback `onCreate`.

Ao adicionar migrações, declarar o objeto `static final Migration MIGRATION_X_Y` em `AppDatabase` e registrá-lo em `.addMigrations()` no builder.

---

## Helpers — `com.example.myapplication.ui.helpers`

### `TaskHelper`
Executa operações assíncronas em background e retorna o resultado na UI thread. Injetado via Hilt. Internamente usa `ExecutorService` (pool fixo de 4 threads) + `Handler(mainLooper)`, ambos providos pelo `ExecutorModule`.

```java
taskHelper.execute(
    () -> repository.buscarDados(),       // Callable — roda em background
    result -> liveData.setValue(result),  // onSuccess — roda na UI thread
    error -> handleError(error)           // onError — roda na UI thread
);
```

> ⚠️ **Sempre usar `TaskHelper` para I/O.** Nunca usar `AsyncTask` (depreciado) nem operações de rede/banco diretamente na UI thread.

---

### `FormatHelper`
Formata e parseia números no locale brasileiro (`pt_BR`). Métodos estáticos.

```java
BigDecimal val  = FormatHelper.getDecimal(binding.etValor.getText().toString()); // "1.234,56" → BigDecimal
String texto    = FormatHelper.formatCurrency(bigDecimal);  // BigDecimal → "1.234,56"
Integer qtd     = FormatHelper.getInt(binding.etQtd.getText().toString());
```

> `FormatHelper.CURRENCY_FORMAT` é o `DecimalFormat` compartilhado — usar `formatCurrency()` em vez de instanciar um novo.

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

---

### `ViewHelper`
Utilitários para leitura e manipulação de Views:

```java
BigDecimal val = ViewHelper.getBigDecimal(binding.etValor);
if (ViewHelper.anyEmpty(binding.etNome, binding.etEmail)) { ... }
ViewHelper.setVisible(isLoading, binding.progressBar);
ViewHelper.setText(binding.tvDescricao, produto.getDescricao());
ViewHelper.clearText(binding.etNome, binding.etEmail, binding.etTelefone);
```

---

## Utilitário de PDF — `com.example.myapplication.utils.pdf`

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
`RoutesRemoteDataSource` chama a Routes API via `java.net.HttpURLConnection`. A chave de API é injetada pelo plugin `secrets-gradle-plugin` a partir de `secrets.properties` (não versionado); `local.defaults.properties` define os nomes das variáveis sem os valores.

### Gespec
Data sources em `data/source/remote/gespec/` consomem o backend Gespec via **Retrofit** — as interfaces de serviço estão em `data/source/remote/retrofit/gespec/`.

### Chaves de API
Armazenadas em `secrets.properties` (não versionado). Nunca hardcodar chaves no código.

---

## Regras de Negócio — Flow (Negociação de Gado)

### Precificação de Bezerros

#### Conceito central

O **valor por arroba** já embute implicitamente o custo de frete e logística, que varia por macro e micro região. Por isso existem dois modos de cálculo:

| Modo | O que representa |
|---|---|
| **Com frete** | Valor bruto do bezerro — frete já embutido no arroba |
| **Sem frete** | Valor real do bezerro a ser negociado — frete descontado do valor/kg |

O cálculo do frete não é um campo separado: é uma **incidência que reduz o valor por kg**, permitindo isolar o valor real do animal.

#### Entradas obrigatórias

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `peso` | `BigDecimal` (kg) | Peso vivo do bezerro |
| `precoPorArroba` | `BigDecimal` | Cotação do arroba na região |
| `percentualAgio` | `BigDecimal` | Ágio aplicado sobre o peso base (%) |
| `pesoBaseKg` | `BigDecimal` | Peso de referência que delimita a faixa do ágio |
| `quantidade` | `Integer` | Número de cabeças no lote |
| `valorFrete` | `BigDecimal` | Incidência do frete por kg (apenas no modo sem frete) |

#### Constantes fixas de negócio

Estas constantes refletem premissas do mercado e **não devem ser alteradas sem revisão do domínio**:

| Constante | Valor | Significado |
|---|---|---|
| `PESO_ARROBA_KG` | 30 kg | Peso padrão de uma arroba |
| `ARROBAS_ABATE_ESPERADAS` | 21 arrobas | Projeção de arrobas que o bezerro atingirá no abate |
| `TAXA_FIXA_ABATE` | R$ 69,70 | Taxa fixa de abate cobrada no frigorífico |
| `IMPOSTO_FUNRURAL` | 1,5% | Imposto sobre a venda no abate |

#### Lógica do Ágio

O ágio representa o **prêmio pago pelo potencial de ganho de peso** do bezerro até o abate:

- **Bezerro acima ou no peso base:** o ágio é calculado diretamente sobre as arrobas restantes até o abate.
- **Bezerro abaixo do peso base:** o ágio é acumulado faixa a faixa — a cada arroba inteira de diferença de peso, calcula-se a diferença de ágio naquele intervalo, até atingir o peso base.

> O `pesoBase` é o ponto de inflexão da curva de ágio. Bezerros mais leves têm mais arrobas a ganhar, portanto o ágio cresce progressivamente conforme o peso cai abaixo do base.

#### Modos de cálculo disponíveis

**Modo sem frete** — `PrecificacaoBezerroSemFrete`:

```
valorPorKg     = (valorTotalBezerroComFrete / peso) - valorFrete
valorPorCabeca = valorPorKg × peso
valorTotal     = valorPorCabeca × quantidade
```

**Modo com frete** — `PrecificacaoBezerroComFrete`:

```
valorPorCabeca = valorBasePorPeso + valorTotalAgio
valorPorKg     = valorPorCabeca / peso
valorTotal     = valorPorCabeca × quantidade
```

**Modo com frete e comissão** — `PrecificacaoBezerroComFreteEComissao`:

Igual ao modo com frete, mas deduz a comissão do corretor do valor por cabeça antes de calcular o total.

#### ⚠️ Regras invioláveis

- **Nunca alterar** `PrecificacaoBezerroRepository` sem revisão das premissas de domínio
- **Nunca substituir** `BigDecimal` por `float` ou `double` — perda de precisão monetária é inaceitável
- **Nunca alterar** as constantes de abate sem alinhamento com regras do mercado frigorífico
- O `valorFrete` é uma **incidência por kg**, não um valor fixo por cabeça ou por lote
- O frete **não é um campo de entrada do usuário no modo com frete** — já está embutido no arroba informado
- Peso zero deve retornar `BigDecimal.ZERO` sem lançar exceção

---

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
- Nunca fazer commit de `secrets.properties`

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
./gradlew test --tests "com.example.myapplication.ExampleUnitTest"

# Testes de instrumentação (requer dispositivo/emulador)
./gradlew connectedAndroidTest

# Limpar build
./gradlew clean
```

---

## Dependências Principais

| Biblioteca | Uso |
|---|---|
| Room | Persistência local (ORM) |
| ViewModel / LiveData | Arquitetura MVVM |
| Hilt | Injeção de dependência |
| Navigation Component + Safe Args | Navegação entre Fragments |
| Retrofit + GSON | Integração Gespec (REST) |
| Material Design | Componentes de UI |
| Google Play Services Location / Maps | Localização e rotas |
| ViewBinding / DataBinding | Acesso a Views sem `findViewById` |
| Secrets Gradle Plugin | Gerenciamento de chaves de API via `secrets.properties` |
| Espresso / JUnit | Testes |