<h1 align="center" style="font-weight: bold;">Medical One<br>
M3P-BackEnd-Squad1</h1> 
<p align="center"> <b>Projeto de Avaliação Final do Curso FullStack - Turma Health,<br> 
oferecido por <a href="https://cursos.sesisenai.org.br/" target="_blank">Sesi/Senai SC</a></b> </p>

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![cypress](https://img.shields.io/badge/-cypress-%23E5E5E5?style=for-the-badge&logo=cypress&logoColor=058a5e)
![Insomnia](https://img.shields.io/badge/Insomnia-black?style=for-the-badge&logo=insomnia&logoColor=5849BE)

![Render](https://img.shields.io/badge/Render-%46E3B7.svg?style=for-the-badge&logo=render&logoColor=white)
?

<p align="center">
<a href="#descricao">Descrição</a> •
<a href="#instalacao">Instalação</a> • 
<a href="#routes">Endpoints</a> • 
<a href="#colab">Colaboradores</a> • 
<a href="#license">Licença</a> </p> 

<h2 id="descricao">Descrição</h2>

Medical One é uma aplicação backend desenvolvida em Java, 
utilizando o framework Spring Boot. 

Este sistema implementa um servidor REST que facilita o gerenciamento de informações de saúde, 
permitindo uma administração eficiente de pacientes, consultas e exames. 

### Problema Resolvido

O sistema foi criado para resolver a dificuldade no gerenciamento de informações de saúde, permitindo uma administração eficiente de pacientes, consultas e exames, com acesso diferenciado para usuários com diferentes perfis.

A aplicação oferece funcionalidades específicas para diferentes perfis de usuários,
incluindo administradores, médicos e pacientes, garantindo uma experiência personalizada e intuitiva.

### Tecnologias Utilizadas

- **Java**: Versão 17 ou superior
- **Spring Framework**: Spring Boot
- **Banco de Dados**: PostgreSQL
- **Outras Ferramentas**: Docker, Git, Insomnia, Swagger

<h2 id="instalacao">Instalação</h2>


### Pré-requisitos

- Java 17 ou superior
- Git
- Docker
- PostgreSQL
- Insomnia (opcional)

### Clonando o Repositório

```bash
git clone https://github.com/FullStack-Health/M3P-BackEnd-Squad1.git
```

### Variáveis de Ambiente

Altere as variáveis de ambiente encontradas em `application.properties` para acessar o banco de dados PostgreSQL ou crie um contêiner utilizando Docker.

#### Contêiner PostgreSQL

Crie um contêiner PostgreSQL utilizando Docker:

```bash
docker run -d   --name postgres-medicalone   -e POSTGRES_USER=admin   -e POSTGRES_PASSWORD=1q2w3E@!   -e POSTGRES_DB=dbMedicalOne   -p 5455:5432   postgres
```

### Rodando o Projeto

```bash
cd M3P-BackEnd-Squad1
mvn spring-boot:run
```

Por padrão, a aplicação roda na porta 8081.

### Insomnia

Uma collection do Insomnia está disponível em:
.\src\main\java\br\com\senai\medicalone\config\doc\collection\MedicalAPI-collections.json


### Docker

Para criar um ambiente completo com aplicação, banco de dados e PgAdmin:
```bash
cd docker
docker-compose up
```

Após baixar a imagem mais recente, a aplicação estará rodando na porta 8081.

<h2 id="routes">Endpoints</h2>


Com a aplicação rodando, acesse a documentação completa de endpoints em:
[http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

### Features

- Login
- Cadastro
- Pacientes
- Consultas
- Exames
- Prontuários
- Usuários
- Dashboard


<h2 id="colab">Colaboradores</h2>

- Camila Reimann ([camilareimann](https://github.com/camilareimann))
- Evelin Lilanda Nunes ([evelinlnunes](https://github.com/evelinlnunes))
- Felippe Kulkamp Sant Ana ([Felippeks](https://github.com/Felippeks))
- Ricardo Vieira dos Santos ([viesant](https://github.com/viesant))
- Sérgio Roberto Vieira Junior ([Brk-SirGio](https://github.com/Brk-SirGio))

<h2 id="license">Licença</h2>

Este projeto está licenciado nos termos da [licença MIT](https://choosealicense.com/licenses/mit/).
