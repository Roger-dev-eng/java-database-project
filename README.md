# Sistema Web de Gerenciamento de Jogos

Aplicação web em Python Streamlit para gerenciamento e visualização analítica de jogos, jogadores, plataformas e avaliações, conectada ao PostgreSQL do Supabase.

O site reúne login simples, operações CRUD, consultas SQL, joins, agregações e dashboard analítico em uma única interface. O código Java Swing permanece no repositório como implementação legada do cliente desktop, mas não é necessário para executar o site.

## Conteúdo

- [Visão geral](#visão-geral)
- [Stack](#stack)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Arquitetura](#arquitetura)
- [Modelo de dados da aplicação](#modelo-de-dados-da-aplicação)
- [Requisitos](#requisitos)
- [Configuração do banco](#configuração-do-banco)
- [Como compilar e executar](#como-compilar-e-executar)
- [Aplicação web](#aplicação-web)
- [Supabase e deploy no Render](#supabase-e-deploy-no-render)
- [Fluxo da aplicação](#fluxo-da-aplicação)
- [Consultas disponíveis](#consultas-disponíveis)
- [Views, functions e procedures](#views-functions-e-procedures)
- [Validações implementadas](#validações-implementadas)
- [Vídeo Explicativo e Demonstrativo](#vídeo-explicativo-e-demonstrativo)

## Visão geral

O sistema oferece:

- login simples com identificação do usuário
- menu principal para navegação entre módulos
- CRUD de jogos
- CRUD de jogadores
- CRUD de plataformas
- CRUD de avaliações
- tela de consultas com listagens, filtros, joins e agregações
- dashboard web com KPIs, filtros dinâmicos e gráficos analíticos

<img width="1447" height="848" alt="Captura de tela 2026-05-25 181749" src="https://github.com/user-attachments/assets/91afb1b0-b370-4af8-a3e6-8c71ab9bcc42" />

## Stack
- PostgreSQL
- Python
- Streamlit
- Pandas
- Plotly
- Psycopg

## Estrutura do projeto

```text
diagrama/
  Diagrama Entidade-Relacionamento.pdf
src/
  MenuPrincipal.java
  InterfaceSwing/
    MenuPrincipal.java
    telas/
      Conexao.java
      EstiloUI.java
      TelaLogin.java
      TelaBoasVindas.java
      TelaJogos.java
      TelaJogadores.java
      TelaPlataformas.java
      TelaAvaliacoes.java
      TelaVerTabelas.java
  app/
    db/
      Database.java
    model/
      Jogo.java
      Jogador.java
      Plataforma.java
      Avaliacao.java
    repository/
      JogoRepository.java
      JogadorRepository.java
      PlataformaRepository.java
      AvaliacaoRepository.java
    service/
      ConsultaResultado.java
      ConsultaService.java
    validation/
      Validator.java
      ValidationException.java
  ddl/
    Tabelas.java
  dml/
    Insert.java
    Update.java
    Delete.java
dql/
    jogos/
    jogadores/
    plataformas/
    avaliacoes/
dashboard/
  app.py
  dashboard.py
  requirements.txt
database/
  ddl/
    01_schema.sql
  dml/
  dql/
  views/
    01_view_resumo_jogos.sql
  functions/
    01_function_media_jogo.sql
  procedure/
    01_procedure_atualizar_status_avaliacao.sql
```

## Arquitetura

O código está organizado em camadas e módulos com responsabilidades bem definidas. A ideia central é separar interface, regras de aplicação, validação e acesso ao banco para reduzir acoplamento e facilitar manutenção.

- `InterfaceSwing`: concentra as telas, componentes visuais, navegação entre janelas e captura das ações do usuário.
- `app.model`: define as entidades de domínio usadas no sistema, como `Jogo`, `Jogador`, `Plataforma` e `Avaliacao`. Essas classes representam os dados de forma tipada e ajudam a evitar manipulação solta de valores pela aplicação.
- `app.repository`: cada repositório organiza operações de leitura e escrita para um tipo de dado específico e faz a ponte entre a aplicação e as classes SQL reutilizadas.
- `app.service`: reúne regras mais voltadas ao comportamento da aplicação, especialmente na composição e execução das consultas avançadas.
- `app.validation`: centraliza validações reutilizáveis de entrada, como obrigatoriedade, conversão numérica, datas e faixas permitidas.
- `app.db`: encapsula a criação da conexão JDBC com o PostgreSQL a partir das variáveis de ambiente.
- `ddl`: concentra a definição estrutural do banco, isto é, os elementos responsáveis pela criação das tabelas.
- `dml`: reúne as instruções de manipulação de dados, como inserções, atualizações e exclusões.
- `dql`: reúne as consultas SQL usadas para leitura de dados.
- `database/views`: contém views de leitura reutilizáveis pela aplicação e pelo dashboard.
- `database/functions`: contém functions PostgreSQL que retornam um valor e podem ser usadas em `SELECT`.
- `database/procedure`: contém procedures PostgreSQL para operações que alteram dados e são executadas com `CALL`.
- `dashboard`: concentra a camada analítica web construída com Streamlit. Essa parte consome o mesmo banco PostgreSQL da aplicação desktop, aplica filtros dinâmicos, executa consultas agregadas e apresenta KPIs e gráficos para apoio gerencial.

## Modelo de dados da aplicação

As entidades centrais do sistema são:

- `Jogo`: nome, ano de lançamento, desenvolvedora e gênero
- `Jogador`: nickname, email e jogo associado
- `Plataforma`: nome, horas jogadas, última sessão e jogador associado
- `Avaliacao`: nota, comentário, status, data, jogador e jogo associados

## Requisitos

- JDK compatível com o projeto
- PostgreSQL em execução
- variáveis de ambiente configuradas para acesso ao banco
- Maven opcional para build
- Python instalado para execução do dashboard web
- dependências do dashboard instaladas via `dashboard/requirements.txt`

O `pom.xml` está configurado com:

```xml
<maven.compiler.release>25</maven.compiler.release>
```

Se você for ajustar a versão do Java usada no ambiente, atualize esse valor para manter consistência com o compilador instalado.

## Configuração do banco

Defina as variáveis de ambiente antes da execução:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/seu_banco"
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

A conexão JDBC é centralizada em [src/app/db/Database.java](src/app/db/Database.java).

Para usar o Supabase, copie a connection string PostgreSQL do painel `Connect` e configure:

```powershell
$env:SUPABASE_DB_URL="postgresql://postgres.seu_projeto:SUA_SENHA@aws-0-regiao.pooler.supabase.com:6543/postgres?sslmode=require"
$env:DB_URL="jdbc:postgresql://aws-0-regiao.pooler.supabase.com:6543/postgres?sslmode=require"
$env:DB_USER="postgres.seu_projeto"
$env:DB_PASSWORD="SUA_SENHA"
```

O dashboard prioriza `SUPABASE_DB_URL`, depois `DATABASE_URL` e, por compatibilidade, `DB_URL`. A aplicação Java continua usando `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## Como compilar e executar

### Opção 1: compilação manual

```powershell
$files = Get-ChildItem ".\src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -d .\out $files
java -cp .\out MenuPrincipal
```

### Opção 2: Maven

```powershell
mvn compile
```

### Classe principal

O ponto de entrada do projeto é:

```text
src/MenuPrincipal.java
```

Essa classe delega a inicialização para a interface principal em `InterfaceSwing.MenuPrincipal`.

## Aplicação web

<img width="1446" height="847" alt="Captura de tela 2026-05-25 181807" src="https://github.com/user-attachments/assets/9c010c0a-1c03-426a-ae4e-9c3f597b2557" />

O site Streamlit substitui a navegação Swing e reúne as funções operacionais e analíticas em uma única aplicação web.

### Arquivos do dashboard

- `dashboard/dashboard.py`: arquivo principal da aplicação Streamlit
- `dashboard/app.py`: ponto de compatibilidade para abertura pelo launcher Java
- `dashboard/requirements.txt`: dependências Python do dashboard

### Funcionalidades do dashboard

- login simples por nome
- CRUD de jogos, jogadores, plataformas e avaliações
- consultas simples, filtros, joins e agregações
- KPIs com quantidade de jogos, jogadores cadastrados, média geral das notas e plataforma mais usada
- filtros dinâmicos por gênero, status da avaliação e faixa de ano de lançamento
- gráficos com agregações, agrupamentos, ordenações e filtros SQL
- ranking de jogos por volume de avaliações

### Como executar localmente

Instale as dependências do dashboard:

```powershell
pip install -r .\dashboard\requirements.txt
```

Depois execute:

```powershell
streamlit run .\dashboard\dashboard.py
```

### Observações importantes

- o site usa o banco PostgreSQL do Supabase
- ele usa `SUPABASE_DB_URL` no Render, ou `DB_URL`, `DB_USER` e `DB_PASSWORD` para compatibilidade local
- as dependências Python do dashboard não ficam no `pom.xml`, porque pertencem a outro ecossistema

## Supabase e deploy no Render

O arquivo `render.yaml` configura o deploy do dashboard como um Web Service Python. Para publicar:

1. Faça o push do projeto para um repositório GitHub.
2. No Render, escolha `New > Blueprint` e selecione o repositório.
3. Confirme o serviço `jogos-dashboard` criado pelo `render.yaml`.
4. No painel do serviço, informe `SUPABASE_DB_URL` usando a connection string do Supabase com `sslmode=require`.
5. Execute os scripts no SQL Editor do Supabase, seguindo a ordem indicada na seção [Views, functions e procedures](#views-functions-e-procedures).

O Render fornece automaticamente a variável `PORT`; o comando de inicialização já usa essa porta e o endpoint `/_stcore/health` é usado no health check.

## Fluxo da aplicação

1. O usuário acessa a tela de login.

<img width="1450" height="848" alt="Captura de tela 2026-05-25 181721" src="https://github.com/user-attachments/assets/3694d5a0-ecc9-466e-9efa-65ff1cf030f2" />

2. O menu principal libera acesso aos módulos.

<img width="1447" height="848" alt="Captura de tela 2026-05-25 181749" src="https://github.com/user-attachments/assets/03d9ef53-e376-4bab-b7ec-fc1e51a483a4" />

3. O botão `Dashboard` também permite abrir a visualização web analítica do sistema.

<img width="1446" height="847" alt="Captura de tela 2026-05-25 181807" src="https://github.com/user-attachments/assets/8f09195b-fd6e-4491-a6ae-9a8364de0a44" />

https://github.com/user-attachments/assets/13cd62b6-8579-459e-b6a7-da05a22b7fa9

4. Cada tela operacional realiza consultas e operações CRUD no banco.

<img width="1453" height="850" alt="Captura de tela 2026-05-25 182552" src="https://github.com/user-attachments/assets/55b61120-ad08-4047-93dd-fa71e8a2ceee" />

5. A opção "Análises" permite executar consultas simples e avançadas.

<img width="1450" height="844" alt="Captura de tela 2026-05-25 182653" src="https://github.com/user-attachments/assets/a95b145c-d146-4630-82e9-ef391fa324e5" />

## Consultas disponíveis

Na tela `TelaVerTabelas`, o usuário pode alternar entre quatro modos:

- `Simples`: listagem geral e busca por ID
- `Filtros`: filtros por campos específicos
- `Joins`: cruzamento de dados entre tabelas relacionadas
- `Agregacoes`: totais, médias e outras métricas

<img width="1449" height="846" alt="Captura de tela 2026-05-25 182746" src="https://github.com/user-attachments/assets/17bd5cdb-48e2-42b9-aea7-fb6b9a7383f3" />

## Views, functions e procedures

Os scripts ficam separados por responsabilidade e devem ser executados nesta ordem:

1. `database/ddl/01_schema.sql`
2. `database/views/01_view_resumo_jogos.sql`
3. `database/functions/01_function_media_jogo.sql`
4. `database/procedure/01_procedure_atualizar_status_avaliacao.sql`

Com o PostgreSQL configurado, eles podem ser aplicados pelo `psql` na ordem acima. Ajuste o banco e o host conforme o seu ambiente:

```powershell
psql -h localhost -U $env:DB_USER -d seu_banco -f .\database\views\01_view_resumo_jogos.sql
psql -h localhost -U $env:DB_USER -d seu_banco -f .\database\functions\01_function_media_jogo.sql
psql -h localhost -U $env:DB_USER -d seu_banco -f .\database\procedure\01_procedure_atualizar_status_avaliacao.sql
```

Exemplos de uso após a instalação:

```sql
SELECT * FROM vw_resumo_jogos ORDER BY media_nota DESC;
SELECT fn_media_jogo(1);
CALL pr_atualizar_status_avaliacao(1, 'Aprovada');
```

As views e functions podem ser consumidas com `Statement` ou `PreparedStatement` pela aplicação Java. A procedure deve ser chamada com `CallableStatement` ou com `CALL` em um `Statement`.

## Validações implementadas

O projeto possui validações reutilizáveis para:

- campos obrigatórios
- conversão de número inteiro
- conversão de data no formato `yyyy-MM-dd`
- validação de faixa numérica

Essas regras estão centralizadas em `app.validation.Validator`.

## Vídeo Explicativo e Demonstrativo
https://drive.google.com/file/d/1SSahKolN3dFNy1kENJBgL0YhF2CDtx4J/view?usp=sharing
