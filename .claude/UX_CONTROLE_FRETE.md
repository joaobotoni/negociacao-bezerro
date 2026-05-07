## Problema Atual
O sistema possui um estado **implícito** sem feedback visual suficiente. O usuário não sabe se o valor presente no campo é fruto de uma **simulação** ativa ou de uma **inserção** manual, e não entende que editar o valor rompe o vínculo com a simulação.

## Regras de UX
- **Origens do Frete**: Pode ser inserido de forma Manual ou Simulada.
- **Automatização**: A simulação bem-sucedida preenche o campo automaticamente.
- **Invalidação**: Qualquer edição manual no campo de valor descarta o resultado da simulação anterior.

## Fluxo Proposto
**Campo de Valor**: Vazio ou com valor manual padrão.
**Interface**: Exibe o card/botão "Simular frete" em estado normal.
**Contexto**: O sistema entende o estado como Manual.

## Ação: Usuário Simula
**Ação**: Usuário abre o modal/seção de simulação e conclui o processo.
**Retorno**: O sistema retorna o valor da cotação.
**Feedback**: O EditText (campo de valor) é preenchido automaticamente.
**Estado**: O sistema marca este frete como Simulado/Vinculado à Cotação.

##  Ação: Edição Manual (Invalidação)
**Gatilho**: Usuário decide alterar o valor manualmente (digita qualquer caractere no campo).
**Resultado Imediato**: A cotação anterior é invalidada. O card de simulação volta ao seu estado inicial (indicando que não há uma simulação ativa).
**Estado Final**: O sistema define o valor como Manual.