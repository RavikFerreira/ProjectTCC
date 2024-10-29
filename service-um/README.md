## COMO CRIAR E IMPLEMENTAR UM MICRO SERVIÇO COM JAVA E MICRONAUT:

### Pré-requisitos

Este guia assume que você já possui experiência com **Spring Boot** e desenvolvimento back-end.
## INTRODUÇÃO
Este tutorial é voltado para iniciantes em Java e Micronaut, apresentando uma introdução à estrutura em camadas do Micronaut para organizar o código. Ele ensina como criar um projeto usando o Micronaut Launch, facilitando a configuração inicial. O tutorial também explica a estrutura do projeto, a criação de entidades com JPA e Hibernate, além do uso do Lombok e certos cuidados que se deve ter.

Em seguida, aborda a criação de repositórios, mostrando como conectar bancos de dados relacionais e não relacionais. A camada de serviço é apresentada com um exemplo prático de operações CRUD, utilizando a anotação @Inject. Por fim, o tutorial ensina a criar controladores REST para expor a API e mapear endpoints de forma eficiente.

## CRIAÇÃO DO PROJETO NO MICRONAUT LAUNCH

1. Acesse o site oficial do **[Micronaut Launch](https://micronaut.io/launch)**.
2. No formulário, configure as opções:
    - Linguagem: **Java**
    - Versão: Selecione a versão mais recente
    - Nome do Projeto: Defina o nome para seu projeto
    - Nome do Pacote: Defina o nome do pacote base
    - Ferramenta de build: Escolha entre **Gradle** ou **Maven** (**Maven** como preferencial)
    - Estrutura de teste: **JUnit**
3. Adicione as **features** desejadas (dependências como Kafka, JPA, etc.).
4. Clique em **Generate Project** para baixar o projeto em formato **.zip**.
5. Extraia o arquivo baixado.
    > **Atenção**: Cuidado para não arrastar pastas duplicadas (`service-um > service-um`).
6. Abra o projeto em sua IDE preferida.
7. Carregue as dependências do projeto (ao abrir na IDE, clique em "Load" se solicitado).

Agora, você está pronto para começar a desenvolver o seu microserviço com **Micronaut**!

## ESTRUTURA DO PROJETO E CRIAÇÃO DAS ENTIDADES
Primeiramente é importante definir a estrutura do projeto, criando os pacotes para dividir as responsabilidades, então no **service-um > src > main > java > com.serviceum:** clique com botão direito, **New > Package > digita o nome do pacote:** faça isso para criar os seguintes pacotes como no exemplo a baixo:

````
service-um/
│
├── models/
├── repository/
├── service/
├── controller/
├── exceptions/
├── dtos/
├── enums/
````

Crie os pacotes garantindo que a classe com o método main estará na raiz dessa
hierarquia de pacotes. O primeiro que deve ser criado é o **models** que são as entidades que vão se conectar ao **repository**. A partir dos modelos destas classes que são entidades as tabelas do banco de dados serão criadas automaticamente. Clique com o botão direito no diretório **models: New > Java Class >** digite o **nome da classe**. Aqui vamos usar duas entidades: **Order** e **Product**.

Nesse exemplo vai ser usado o banco de dados **MongoDB**, que é usado as anatoções **@Serdeable**, ``que vai permitir que a classe seja serializada`` e o outra anotação de mapeamento *@MappedEntity* ``que vai servir para mapear a entidade para que a mesma seja reconhecida no repositorio``. Mas para isso, deve ser adicionado a dependência do micronaut-data-model: clique em **pom.xml** e procure pela tag **<dependencies>** e adicione essa dependência dentro dessa tag:
````xml
    <dependency>
        <groupId>io.micronaut.data</groupId>
        <artifactId>micronaut-data-model</artifactId>
        <version>4.8.1</version>
    </dependency>
````
Também pode se usar anotações como:

* **@NoArgsConstructor**  ````Serve para criar um construtor sem argumentos````
* **@AllArgsConstructor**  ````Serve para criar um construtor com argumentos````

Com isso, vamos criar dois atributos privados:
```java
private String id;
private List<Product> products
```
E para permitir que os atributos possam persistir com o banco de dados **MongoDB** vamos utilizar as anotações do **io.micronaut** acima do atributo **id**, a que vamos utilizar são:
* **@Id**
* **@GeneratedValue**

E principalmente crie os **Getters** e **Setters** 
> Não use as anotações do **lombok** para criar os gets e sets automaticamente, o lombok pode causar alguns erros futuramente.

Usando a dependência do lombok: clique em **pom.xml** e procure pela tag **<dependencies>** e adicione essa dependência dentro dessa tag:
````xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.26</version>
    <scope>provided</scope>
</dependency>
````

Caso queira persistir com o banco de dados relacional pode também utilizar as anotações do **jakarta persistence**, a que vamos utilizar são:

* Dentro de **Order**, deve colocar três anotações em cima da classe:
* **@Entity** ``Que serve para definir que a classe é uma entidade``
* **@Table(“nome-da-tabela”)** ``Que serve para definir o nome da tabela no banco de dados``
* **@Serdeable** ``Que serve para permitir que a classe possa ser serializada e deserilizada``
* **@Id**  ````Adicionando acima do atributo id, vai servir para definir a esse atributo que ele vai ser uma chave primária para que ele navegue por outras classes.````
* **@GeneratedValue(strategy = GenerationType.AUTO)**  ````Adicionando acima do atributo id para que o banco de dados defina um id automaticamente a esse atributo.````


Em **Product** usaremos as mesmas anotações que foram usadas em **Order**: Com isso, vamos criar três atributos privados:
```java
private Long id;
private String name;
private double price;
````

E principalmente crie os **Getters e Setters**, não use as anotações do **lombok** para criar os gets e sets automaticamente.

E para finalizar a parte dos **models** deve se fazer a **introspecção** das entidades para gerar metadados e melhorar o desempenho do sistema. Com isso vamos usar a anotação de introspecção: **(OBS: Para MongoDB essa configuração não é necessária).**

* **@Introspected(package = “com.serviceum.models” , includedAnnotations = Entity.class)** ````Isso vai introspectar todas as classes anotadas com @Entity no caminho que foi passado.````

Também é importante adicionar as configurações de escaneamento de entidades do **JPA** no **resources/application.properties/yaml**:

**Para YAML:**
````yaml
jpa: 
    default:
        entity-scan:
            packages:
                com.serviceum.models
````
**Para PROPERTIES:**
````properties
jpa.default.entity-scan.packages = com.serviceum.models
````
> Essas configurações vai servir para escanear todas as classes do pacote que foi passado no caminho da configuração.

## CRIANDO REPOSITÓRIOS E CONECTANDO AO BANCO DE DADOS

O próximo a ser criado é o repositório, que é uma classe responsável por "conversar" com a base de dados. No pacote **repository**, deve ser criado uma interface chamada **OrderRepository** e **ProductRepository**

Depois disso, deve ser feito a conexão com o banco de dados, e para isso existem algumas configurações que devem ser inseridas, mas antes disso temos que adicionar a dependência do banco de dados **PostgreSQL**:
````xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
````
Com a criação da interface, ela deve ser anotada com:
* **@Repository** ````Essa anotação serve para que o Micronaut entenda que essa interface é um repository. Essa interface deve estender o JpaRepository indicando a entidade e o tipo da chave que ela contém. Ex: extends JpaRepository<Order, Long>, isso vai fazer com que o Jpa mapeie toda a classe criando as tabelas automaticamente, fazer da mesma forma com o Product.````

* Da mesma forma para o **MongoDB** porém, a diferença é que ao invés de usar **@Repository** usa se **@MongoRepository(databaseName = “orders-db”)**  e também não estende ao **JpaRepository** e sim ao **CrudRepository**.
Mas antes deve ser adicionado às dependências do **MongoDB**:
```xml
<dependency>
    <groupId>io.micronaut.data</groupId>
    <artifactId>micronaut-data-mongodb</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <scope>runtime</scope>
</dependency>
````
Com isso, vamos criar dois repositórios, **OrderRepository e ProductRepository** que vão salvar todos os dados que foram inseridos nas entidades e salvar no banco de dados. LEMBRETE: todos os repositórios devem estar na mesma databaseName.


Com isso, dentro do resources/application.properties/yaml deve adicionar as seguintes configurações, caso esteja usando PostgreSQL:
**Para YAML:**
````yaml
datasources:
    default:
        url: jdbc:postgresql://localhost:5432/orders-db
        username: postgres
        password: postgres
        driver-class-name: org.postgresql.Driver
        dialect: io.micronaut.data.jdbc.postgres.PostgresDialect
````
**Para PROPERTIES:**
````properties
datasources.default.url=jdbc:postgresql://localhost:5432/orders-db
datasources.default.username= postgres
datasources.default.password= postgres
datasources.default.driver-class-name= org.postgresql.Driver
datasources.default.dialect=io.micronaut.data.jdbc.postgres.PostgresDialect
````
Para **MongoDB**: dentro do **resources/application.properties/yaml** deve adicionar as seguintes configurações:
**Para YAML:**
````yaml
mongodb:
    uri: mongodb://localhost:27017/orders-db
````
**Para PROPERTIES:**
````properties
mongodb.uri= mongodb://localhost:27017/orders-db
````
E também deve ser adicionado um path de processamento de dados, e mudar a tag de processamento:
````xml
<annotationProcessorPaths combine.children="append"> 
````
para:
````xml
<annotationProcessorPaths combine.self="override">
````
então, dentro do pom.xml na tag: 

````xml
<annotationProcessorPaths combine.self="override">
````
adicione esse path:
````xml
<path>
    <groupId>io.micronaut.data</groupId>
    <artifactId>micronaut-data-document-processor</artifactId>
    <version>4.8.1</version>
<exclusions>
    <exclusion>
        <groupId>io.micronaut</groupId>
        <artifactId>micronaut-inject</artifactId>
    </exclusion>
</exclusions>
</path>
````
E também deve ser adicionado esse <plugin>:
````xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.3.1</version>
</plugin>
````
## CRIANDO A CLASSE DE SERVIÇO

Agora vamos criar a camada de serviço, que vai ser responsável por toda regra de negócio da aplicação, então, no pacote service, crie duas classes **OrderService** e **ProductService** 
* Anote a classe com **@Singleton** ````Serve para que o Micronaut entenda que essa classe é uma classe de serviço.````
* Faça a injeção de dependência da interface do repository:

      private OrderRepository orderRepository;
* E depois anote ela com **@Inject** ````Vai fazer com que o atributo seja injetado e criado o construtor````

Com isso, vamos criar dois métodos: o **findAll e addOrder** , findAll vai servir para mostrar todos os dados que foram adicionados dentro do banco e o addOrder vai adicionar dados dentro do banco. Então:
```java
  public List<Order> findAll (){
      return orderRepository.findAll();
  } // esse método findAll() já vem no JpaRepository ou no CrudRepository.


  public Order addOrder (Order order){
      return orderRepository.save(order);
  } // esse método save() já vem no JpaRepository ou no CrudRepository.
  ```

Da mesma forma vamos fazer com ProductService:
```java
    public List<Product> findAll (){
        return productRepository.findAll();
    }
    
    public Order addProduct (Product product){
        return productRepository.save(product);
    }
```
## CRIANDO A CLASSE CONTROLLER

Agora por fim a camada de controle,que vai ser responsável por todo controle dos caminhos das requisições, então, no pacote controller, crie duas classes **OrderController** e **ProductController** 
* Anote a classe com **@Controller(“orders”)** ````Serve para que o Micronaut entenda que essa classe é uma classe de controle.````
* Faça a injeção de dependência da classe de serviço:

      private OrderService orderService;
  * E depois anote ela com **@Inject** que vai fazer com que o atributo seja injetado e criado o construtor.

Com isso, vamos criar dois métodos: o **findAll e addOrder**, findAll vai servir para trazer toda a regra de negócio que foi feita no findAll do  serviço e o addOrder a mesma coisa. Então:
```java
    @Get()
    public List<Order> findAll (){
        return orderService.findAll();
    }
    
    @Post()
    public Order addOrder (@Body Order order){
        return orderService.addOrder(order);
    }
```

E em cima do método deve passar o tipo de método HTTP que vai receber um objeto e ao mesmo tempo salvar ele no banco, o **@Body** que serve para que o Micronaut entenda que o método está recebendo um objeto como parâmetro.

Da mesma forma se faz com **ProductController**.

Lembrar também de adicionar a porta em que o projeto vai rodar manualmente em  **resources/application.properties/yaml**. usando o comando:
**Para PROPERTIES:**
````properties
micronaut.server.port=8080
````
**Para YAML:**
````yaml
micronaut:
    server:
        port: 8080
````
Com isso, já podemos rodar o projeto, vai na raiz do projeto, entre na classe Application e clique em Executar/Run.

segue [link](https://github.com/RavikFerreira/ProjectTCC/tree/feature/service-um) do repositório pronto e funcionando.
