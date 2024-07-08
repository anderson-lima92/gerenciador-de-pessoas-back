
# Documentação da Aplicação

A API tem como objetivo gerenciar pessoas e seus endereços. As funcionalidades disponíveis incluem:

- Criar uma pessoa: Permite a criação de um novo registro de pessoa.
- Editar uma pessoa: Permite a atualização dos dados de uma pessoa existente.
- Consultar uma pessoa: Permite a busca de uma pessoa pelo seu CPF.
- Listar pessoas: Permite listar todas as pessoas registradas.

## Tecnologias Utilizadas
- Java 17
- Spring Boot 3.3.0
- Spring Boot
- Spring framework Data JPA, Security
- JWT
- Banco de dados H2
- Swagger com SpringDoc OpenAPI 3
- JUnit
- Mockito

## Configuração do Projeto

- **Configuração do Banco de Dados**: Utilização do H2 Database para desenvolvimento. Verifique o arquivo `application.properties` para detalhes.

## Executando a Aplicação

- Para executar a aplicação utilize IDE de sua escolha ou navege até a pasta e execute o comando:

	bash ./mvnw spring-boot:run

## Testando a Aplicação Opção 1

**Swagger**
- A API pode ser testada diretamente através do Swagger. Abra [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) no seu navegador para explorar e testar os endpoints disponíveis.

**Fluxo de testes swagger**
- A API é protegida pelo framework spring security e para acessar os seus endpoints é necessário que o usuário esteja logado, siga as instruções para realizar o login.


- Em tempo de inicialização para para facilitar os testes a classe `com.lima.api.gerenciador.config.InitialDataLoader` cria um usuário admin e ele pode ser utilizado para realizar o login, ou se prefirir crie um usuário utilizando o enpoint `/login/register`.


- Faça o login com o usuário escolhido em `/login`. A reposta será o token JWT que será urilizado na autenticação das proximas requisições, configure o token na autenticação do swagger na opção `Authorize` e os enpoints da aplicação ficaram liberados para testes.

## Testando a Aplicação Opção 2

**Interface Angular**

- Baixe o projeto em `https://github.com/anderson-lima92/gerenciador-de-pessoas-front` e siga as instruções Execução.


- Após seguir as intruções, acesse: `http://localhost:4200`, você será direcionado para a tela de login, para acessar é necessário que o usuário já esteja criado, através do swagger em: `/login/register` ou utilize o usuário padrão criado em: `com.lima.api.gerenciador.config.InitialDataLoader`.


- Com o login bem sucedido está disponível as funcionalidades em suas respectivas abas.


