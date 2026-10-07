# Sistema Web de Gerenciamento de Jogos

Aplicação web em Java com frontend HTML, CSS e JavaScript para gerenciamento e visualização analítica de jogos, jogadores, plataformas e avaliações, conectada ao PostgreSQL do Supabase.

O site reúne login simples, operações CRUD, consultas SQL, joins, agregações e dashboard analítico em uma única interface. O código Java Swing e o dashboard Streamlit permanecem no repositório como implementações legadas, mas não são necessários para executar o site atual.

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

## Stack
- Java 21
- Maven
- Java HTTP Server
- JDBC
- PostgreSQL / Supabase
- HTML, CSS e JavaScript
- Docker

## Estrutura do projeto

```text
diagrama/
  Diagrama Entidade-Relacionamento.pdf
src/
  MenuPrincipal.java
  
  app/
    db/
      Database.java
    web/
      WebServer.java
      WebCrudService.java
      WebQueryService.java
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
frontend/
  index.html
  app.js
  styles.css
  overview.css
Dockerfile
render.yaml
database/
  ddl/
    01_schema.sql
  dml/
    Insert.java
    Update.java
    Delete.java
  dql/
    jogos/
    jogadores/
    plataformas/
    avaliacoes/
  views/
    01_view_resumo_jogos.sql
  functions/
    01_function_media_jogo.sql
  procedure/
    01_procedure_atualizar_status_avaliacao.sql
```

## Arquitetura

O código está organizado em camadas e módulos com responsabilidades bem definidas. A ideia central é separar interface, regras de aplicação, validação e acesso ao banco para reduzir acoplamento e facilitar manutenção.

- `app.web`: expõe o backend HTTP Java, integra o frontend e encaminha CRUDs e consultas para as camadas da aplicação.
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
- `frontend`: contém a interface web atual, incluindo login, menu principal, cadastros, consultas e dashboard.

## Modelo de dados da aplicação

As entidades centrais do sistema são:

- `Jogo`: nome, ano de lançamento, desenvolvedora e gênero
- `Jogador`: nickname, email e jogo associado
- `Plataforma`: nome, horas jogadas, última sessão e jogador associado
- `Avaliacao`: nota, comentário, status, data, jogador e jogo associados

## Requisitos

- JDK 21 ou superior
- Maven 3.9 ou superior
- Docker, para reproduzir o deploy localmente
- um projeto PostgreSQL/Supabase
- variáveis de ambiente configuradas para acesso ao banco

O `pom.xml` está configurado com Java 21:

```xml
<maven.compiler.release>21</maven.compiler.release>
```


## Configuração do banco

Defina as variáveis de ambiente antes da execução:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/seu_banco"
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

A conexão JDBC é centralizada em [src/app/db/Database.java](src/app/db/Database.java).

Para usar o Supabase, copie a connection string do painel `Connect`. O backend aceita uma URL única:

```powershell
$env:SUPABASE_DB_URL="postgresql://postgres.seu_projeto:SUA_SENHA@aws-0-regiao.pooler.supabase.com:6543/postgres?sslmode=require"
```

Também é possível usar separadamente `DB_URL`, `DB_USER` e `DB_PASSWORD`. Configure as variáveis na mesma sessão do terminal que iniciará o Java.

## Como compilar e executar

### Compilação com Maven

```powershell
mvn package -DskipTests dependency:copy-dependencies -DoutputDirectory=target/dependency
```

### Executar o site

## Aplicação web


O site é servido por um backend Java em `app.web.WebServer` e reúne a navegação operacional e analítica em uma única aplicação web.

### Arquivos da aplicação web

- `src/app/web/WebServer.java`: servidor HTTP Java e API JDBC
- `src/app/web/WebCrudService.java`: CRUD usando models, repositories, validações e DML
- `src/app/web/WebQueryService.java`: consultas usando `ConsultaService` e DQL
- `frontend/index.html`: frontend web
- `frontend/app.js`: navegação, CRUD, consultas e dashboard
- `frontend/styles.css`: identidade visual responsiva

### Funcionalidades do site

- login simples por nome
- CRUD de jogos, jogadores, plataformas e avaliações
- consultas simples, filtros, joins e agregações
- KPIs com quantidade de jogos, jogadores cadastrados, média geral das notas e plataforma mais usada
- ranking de jogos por volume de avaliações usando a view `vw_resumo_jogos`
- média geral usando a function `fn_media_jogo`
- atualização de status usando a procedure `pr_atualizar_status_avaliacao`

### Como executar localmente

Compile o backend Java:

```powershell
mvn clean package -DskipTests dependency:copy-dependencies -DoutputDirectory=target/dependency
```

Depois execute o site:

```powershell
java -cp ".\target\classes;.\target\dependency\*" app.web.WebServer
```

### Observações importantes

- o site usa o banco PostgreSQL do Supabase
- ele usa `SUPABASE_DB_URL` no Render, ou `DB_URL`, `DB_USER` e `DB_PASSWORD` para compatibilidade local
- a porta do servidor é lida da variável `PORT`, fornecida pelo Render
- o frontend é servido pelo próprio backend Java

## Supabase e deploy no Render

O arquivo `render.yaml` configura o deploy do site Java como um Web Service. Para publicar:

1. Faça o push do projeto para um repositório GitHub.
2. No Render, escolha `New > Blueprint` e selecione o repositório.
3. Confirme o serviço `jogos-dashboard` criado pelo `render.yaml`.
4. No painel do serviço, informe `SUPABASE_DB_URL` usando a connection string do Supabase com `sslmode=require`.
5. Execute os scripts no SQL Editor do Supabase, seguindo a ordem indicada na seção [Views, functions e procedures](#views-functions-e-procedures).

O Render fornece automaticamente a variável `PORT`; o comando de inicialização do Docker já usa essa porta. O health check usa `/`.

## Fluxo da aplicação

1. O usuário acessa a tela de login.

2. O menu principal libera acesso aos módulos.

3. O botão `Dashboard` também permite abrir a visualização web analítica do sistema.

4. Cada tela operacional realiza consultas e operações CRUD no banco.

5. A opção `Consultas` permite executar consultas simples e avançadas.


## Consultas disponíveis

Na página `Consultas`, o usuário pode alternar entre quatro modos:

- `Simples`: listagem geral e busca por ID
- `Filtros`: filtros por campos específicos
- `Joins`: cruzamento de dados entre tabelas relacionadas
- `Agregacoes`: totais, médias e outras métricas

As consultas são encaminhadas por `WebQueryService` para `ConsultaService`, que utiliza as classes DQL de `database/dql`.

## Views, functions e procedures

Os scripts ficam separados por responsabilidade e devem ser executados nesta ordem:

1. `database/ddl/01_schema.sql`
2. `database/views/01_view_resumo_jogos.sql`
3. `database/functions/01_function_media_jogo.sql`
4. `database/procedure/01_procedure_atualizar_status_avaliacao.sql`

Com o PostgreSQL configurado, eles podem ser aplicados pelo `psql` na ordem acima. Ajuste o banco e o host conforme o seu ambiente:

```powershell
psql -h localhost -U $env:DB_USER -d seu_banco -f .\database\ddl\01_schema.sql
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

O dashboard usa a view e a function diretamente no backend Java. A procedure é chamada pelo `WebCrudService` quando o status de uma avaliação é atualizado.

## Validações implementadas

O projeto possui validações reutilizáveis para:

- campos obrigatórios
- conversão de número inteiro
- conversão de data no formato `yyyy-MM-dd`
- validação de faixa numérica

Essas regras estão centralizadas em `app.validation.Validator`.

## Vídeo Explicativo e Demonstrativo
https://drive.google.com/file/d/1SSahKolN3dFNy1kENJBgL0YhF2CDtx4J/view?usp=sharing
