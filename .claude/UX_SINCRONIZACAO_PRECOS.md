## Problema Atual
O tela de negociação possui campos de edição de texto **Valor por cabeça**, **Valor por kg** e **Valor do frete** eles tem resposabilidades importantes na hora da negociação.
o valor pedido é um reflexo direto a esses campos que é pre calculado após a cotação do frete, pois sendo assim é encontrado o valor do bezerro sem o desconto do frete e assim por diante. 

## Regras de UX
- **Origens do Frete**: Pode ser inserido de forma Manual ou Simulada.
- **Automatização**: A simulação bem-sucedida preenche o campo automaticamente.
- **Invalidação**: Qualquer edição manual no campo de valor descarta o resultado da simulação anterior.
- **Reflexo**: Todas as alterações dentrod do campo de edição de texto devem refletir diretamente no card de composição de valores
- **Calculo**: Após a alteração dos valores nos campos de texto, o cálculo deve ser realizado novamente. Assim, ao alterar o valor por cabeça, o valor por kg deve ser recalculado automaticamente com base no novo valor informado. Da mesma forma, ao alterar o valor por kg, o valor por cabeça deve ser recalculado automaticamente.
- **Consistência de Cálculo**: O sistema deve garantir consistência matemática entre os campos relacionados. Para uma mesma entrada de dados, o resultado gerado deve ser sempre o mesmo, independentemente do campo utilizado para iniciar o cálculo. Assim, ao informar um valor por cabeça ou um valor por kg equivalente, os cálculos derivados devem produzir resultados consistentes e previsíveis.

## Fluxo Proposto
- **Estados Vazios**: Enquanto os campos necessários para composição dos valores estiverem vazios ou incompletos, o card de composição não deve exibir valores calculados anteriormente. Os campos **Valor Pedido** e **Valor Final** devem permanecer vazios, ocultos ou em estado neutro até que exista informação suficiente para um novo cálculo válido.


## Ação
- **Entrada de Dados**: Os valores podem ser originados pela tela de simulação (`SimulacaoFragment`) através da cotação do frete ou por edição manual realizada pelo usuário durante a negociação. Ao informar ou atualizar o valor do frete, o sistema deve recalcular automaticamente a incidência do frete sobre o valor por kg do animal, permitindo recalcular toda a operação e encontrar o valor real do bezerro com o desconto do frete aplicado. Além disso, o usuário pode alterar manualmente os campos **Valor por cabeça** ou **Valor por kg**, conforme a necessidade da negociação e o valor acordado pelo corretor, mantendo todos os cálculos sincronizados e consistentes.


