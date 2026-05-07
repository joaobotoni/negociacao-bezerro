# Pendências de Reatividade e UX — Negociação

Este documento detalha os ajustes necessários para corrigir o fluxo de entrada de dados e a sincronização da UI.

## 1. Fluxo de Retorno da Simulação (`SimulacaoFragment`)
- **Comportamento Esperado**: Ao clicar em "Prosseguir", os campos `campo_valor_cabeca_entrada` e `campo_valor_kg_entrada` devem ser preenchidos visualmente.
- **Restrição**: O card de **Composição de Valores** (Tabela) **NÃO** deve ser afetado ou exibido neste momento. 
- **Gatilho de Atualização**: A tabela de composição só deve ser processada e exibida após o usuário efetivamente "Calcular o Frete" ou confirmar a aplicação dos valores na tela principal.

## 2. Recálculo de Incidência e Badges
Sempre que houver alteração no **Frete**, **Valor por Kg** ou **Valor por Cabeça**:
- **Incidência**: Recalcular a incidência do frete e atualizar o `helperText` do `campo_frete_entrada`.
- **Badge**: Atualizar o valor exibido no Badge de resumo.
- **Composição**: Recalcular e atualizar toda a tabela (Valor Cotado, Valor Pedido e Valor Final), garantindo que a Comissão seja somada ao final.

## 3. Reset de Estados (Campos Vazios)
- **Ação**: Se o usuário apagar o conteúdo do campo `campo_frete_entrada` (deixar vazio).
- **Resultado**: Os campos "Valor Pedido" e "Valor Final" na tabela de composição devem retornar ao **estado inicial/vazio**.
- **Invariante**: Mesmo que haja uma comissão calculada, sem o valor do frete (se este for obrigatório para o cálculo atual), a tabela deve ser resetada para evitar confusão.

## 4. Invalidação da Simulação vs. Preenchimento
- **Edição Manual**: Se o usuário digitar manualmente no `campo_frete_entrada`, o card de "Simular Frete" deve resetar imediatamente para o estado inicial (perda de vínculo com a simulação).
- **Seleção de Simulação**: Ao selecionar um frete vindo da simulação:
    1. Preencher `campo_frete_entrada`.
    2. Atualizar o `helperText` com a incidência calculada.
    3. Manter o vínculo visual no card de simulação.

## 5. Simplificação de UX (Diretriz Geral)
- Evitar múltiplos disparos de cálculos (Debounce em TextWatchers).
- Garantir que o usuário entenda a origem do valor (Manual vs Simulado) através do estado do card de simulação.