# Regras de Negócio — Flow (Negociação de Gado)

---

## Precificação de Bezerros

### Conceito central

O **valor por arroba** já embute implicitamente o custo de frete e logística, que varia por macro e micro região. Por isso existem dois modos de cálculo:

| Modo | O que representa |
|---|---|
| **Com frete** | Valor bruto do bezerro — frete já embutido no arroba |
| **Sem frete** | Valor real do bezerro a ser negociado — frete descontado do valor/kg |

O cálculo do frete não é um campo separado: é uma **incidência que reduz o valor por kg**, permitindo isolar o valor real do animal. Essa separação é o que torna a negociação transparente.

---

### Entradas obrigatórias

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `peso` | `BigDecimal` (kg) | Peso vivo do bezerro |
| `precoPorArroba` | `BigDecimal` | Cotação do arroba na região |
| `percentualAgio` | `BigDecimal` | Ágio aplicado sobre o peso base (%) |
| `pesoBaseKg` | `BigDecimal` | Peso de referência que delimita a faixa do ágio |
| `quantidade` | `Integer` | Número de cabeças no lote |
| `valorFrete` | `BigDecimal` | Incidência do frete por kg (apenas no modo sem frete) |

---

### Constantes fixas de negócio

Estas constantes refletem premissas do mercado e **não devem ser alteradas sem revisão do domínio**:

| Constante | Valor | Significado |
|---|---|---|
| `PESO_ARROBA_KG` | 30 kg | Peso padrão de uma arroba |
| `ARROBAS_ABATE_ESPERADAS` | 21 arrobas | Projeção de arrobas que o bezerro atingirá no abate |
| `TAXA_FIXA_ABATE` | R$ 69,70 | Taxa fixa de abate cobrada no frigorífico |
| `IMPOSTO_FUNRURAL` | 1,5% | Imposto sobre a venda no abate |

---

### Lógica do Ágio

O ágio representa o **prêmio pago pelo potencial de ganho de peso** do bezerro até o abate. Ele incide de forma diferente dependendo de onde o peso do animal está em relação ao `pesoBase`:

**Bezerro acima ou no peso base:** o ágio é calculado diretamente sobre as arrobas restantes até o abate.

**Bezerro abaixo do peso base:** o ágio é acumulado faixa a faixa — a cada arroba inteira de diferença de peso, calcula-se a diferença de ágio naquele intervalo, até atingir o peso base, onde o cálculo acima se aplica.

> O `pesoBase` é o ponto de inflexão da curva de ágio. Bezerros mais leves têm mais arrobas a ganhar, portanto o ágio cresce progressivamente conforme o peso cai abaixo do base.

---

### Saídas do cálculo

Todos os modos retornam um objeto `PrecificacaoBezerro` com:

| Campo | Descrição |
|---|---|
| `valorPorKg` | Valor por kg do bezerro (com ou sem frete, conforme o modo) |
| `valorPorCabeca` | Valor total de um animal |
| `valorTotal` | Valor total do lote (`valorPorCabeca × quantidade`) |

---

### Modos de cálculo disponíveis

#### Modo sem frete — `calcularNegociacaoBezerro(..., valorFrete, ...)`
Usado para encontrar o **valor real de negociação** do bezerro, descontando a incidência do frete do valor por kg.

```
valorPorKg     = (valorTotalBezerroComFrete / peso) - valorFrete
valorPorCabeca = valorPorKg × peso
valorTotal     = valorPorCabeca × quantidade
```

#### Modo com frete — `calcularNegociacaoBezerroComFrete(...)`
Usado quando o frete já está embutido no arroba e o objetivo é precificar o animal **incluindo logística**.

```
valorPorCabeca = valorBasePorPeso + valorTotalAgio
valorPorKg     = valorPorCabeca / peso
valorTotal     = valorPorCabeca × quantidade
```

---

### ⚠️ Regras invioláveis

- **Nunca alterar** `PrecificacaoBezerroRepository` sem revisão das premissas de domínio
- **Nunca substituir** `BigDecimal` por `float` ou `double` — perda de precisão monetária é inaceitável
- **Nunca alterar** as constantes de abate sem alinhamento com regras do mercado frigorífico
- O `valorFrete` é uma **incidência por kg**, não um valor fixo por cabeça ou por lote
- O frete **não é um campo de entrada do usuário no modo com frete** — já está embutido no arroba informado
- Peso zero deve retornar `BigDecimal.ZERO` sem lançar exceção

---

## Outras Regras de Negócio

> Documente aqui os demais fluxos do domínio conforme forem definidos:
> - Fluxo de criação e fechamento de uma negociação
> - Regras de validação de formulários de entrada
> - Permissões por perfil de usuário
> - Comportamento offline / sincronização