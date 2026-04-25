# Sistema de Gerenciamento de Banco de Dados sobre Jogos

Aplicação desktop em Java, Swing e PostgreSQL para gerenciamento de jogos, jogadores, plataformas e avaliações.

O projeto combina interface gráfica com operações CRUD e consultas SQL, usando uma organização em camadas para separar interface, acesso a dados, validação e serviços.

## Conteúdo

- [Visão geral](#visão-geral)
- [Stack](#stack)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Arquitetura](#arquitetura)
- [Modelo de dados da aplicação](#modelo-de-dados-da-aplicação)
- [Requisitos](#requisitos)
- [Configuração do banco](#configuração-do-banco)
- [Como compilar e executar](#como-compilar-e-executar)
- [Fluxo da aplicação](#fluxo-da-aplicação)
- [Consultas disponíveis](#consultas-disponíveis)
- [Validações implementadas](#validações-implementadas)
- [Estado atual do projeto](#estado-atual-do-projeto)
- [Melhorias recomendadas](#melhorias-recomendadas)
- [Licença](#licença)

## Visão geral

O sistema oferece:

- login simples com identificação do usuário
- menu principal para navegação entre módulos
- CRUD de jogos
- CRUD de jogadores
- CRUD de plataformas
- CRUD de avaliações
- tela de consultas com listagens, filtros, joins e agregações

<img width="753" height="448" alt="Image" src="https://github.com/user-attachments/assets/7427ab1d-7cda-4851-b842-12e6c29548b7" />

## Stack

- Java
- Swing
- PostgreSQL
- JDBC
- Maven

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

## Fluxo da aplicação

1. O usuário acessa a tela de login.

<img width="579" height="339" alt="Image" src="https://github.com/user-attachments/assets/c3c14183-4427-4fa6-a5f2-0ce72a47ef9a" />

2. A aplicação exibe a tela de boas-vindas.

<img width="623" height="368" alt="Image" src="https://github.com/user-attachments/assets/6b7765d7-7b06-44d0-ad28-d3d9d51c2c44" />

3. O menu principal libera acesso aos módulos.

<img width="753" height="448" alt="Image" src="https://github.com/user-attachments/assets/7427ab1d-7cda-4851-b842-12e6c29548b7" />

4. Cada tela operacional realiza consultas e operações CRUD no banco.

<img width="1094" height="732" alt="Image" src="https://github.com/user-attachments/assets/c8c315be-7db3-4c82-ab19-9326c48690aa" />

5. A tela "Ver Tabelas" permite executar consultas simples e avançadas.

<img width="1195" height="745" alt="Image" src="https://github.com/user-attachments/assets/7edc9253-f030-4012-b052-1ce4bd87fcfc" />

## Consultas disponíveis

Na tela `TelaVerTabelas`, o usuário pode alternar entre quatro modos:

- `Simples`: listagem geral e busca por ID
- `Filtros`: filtros por campos específicos
- `Joins`: cruzamento de dados entre tabelas relacionadas
- `Agregacoes`: totais, médias e outras métricas

<img width="1196" height="755" alt="Image" src="https://github.com/user-attachments/assets/243d22a3-de94-4759-bf3a-50730cc960b2" />

## Validações implementadas

O projeto possui validações reutilizáveis para:

- campos obrigatórios
- conversão de número inteiro
- conversão de data no formato `yyyy-MM-dd`
- validação de faixa numérica

Essas regras estão centralizadas em `app.validation.Validator`.
