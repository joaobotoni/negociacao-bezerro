# Plataforma de Cotação e Negociação de Bovinos
Este projeto nasce da necessidade de proporcionar maior **flexibilidade e agilidade** no processo de aquisição de bezerros. Desenvolvida como uma extensão estratégica do ecossistema **Gespec**, a aplicação mobile permite que o comprador tome decisões baseadas em dados reais de mercado, centralizando indicadores complexos em uma interface intuitiva.

## Stack
- **Plataforma:** Android (Nativo)
- **Linguagem:** Java
- **Integração:** Sincronização com o ecossistema Gespec Web para gestão de indicadores e back-office.

### Cotação
Uma central de inteligência que oferece uma visão panorâmica do mercado.
- Integração com referências externas consolidadas (ex: **IMEA**).
- Monitoramento de variações de preços por região.
- Suporte à tomada de decisão baseada em benchmarks oficiais.

### Negociação
Interface detalhada para a formalização e análise da compra.
- **Registro Completo:** Identificação de comprador, corretor, datas e especificações técnicas dos animais.
- **Composição de Valores:**
  - **Valor Cotado:** Preço de referência do mercado.
  - **Valor Pedido:** Valor inicial solicitado pelo fornecedor.
  - **Valor Final:** Cálculo automático somando *Valor Pedido + Frete + Comissão*.

### Logística e Frete
Um dos grandes diferenciais da solução é o cálculo inteligente de logística para apurar o valor real do animal.
- **Cálculo de Deságio:** Dedução automática do frete sobre o valor da arroba (por macro e micro regiões) para encontrar o valor líquido dos bezerros.
- **Otimização de Carga:** Sugestão de modelos de transporte com base na categoria animal e quantidade.
- **Roteirização:** Cálculo de rotas nacionais com retorno de custo total e custo por kg transportado.

### Detalhamento
Permite o refino da negociação através do peso real de cada animal.
- Input de peso individual por cabeça.
- Geração de cotação específica incluindo o rateio do frete por animal.
- Visualização clara de métricas: **Valor por cabeça** e **Valor por kg**.

### Finalização 
Finaliza o processo de negociação 
- Gera relatorios de todo o processo de negociação
- Realiza processos internos dentro do ecossistema

