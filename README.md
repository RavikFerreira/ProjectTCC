# CRIANDO CONFIGURAÇÃO DE MICRO SERVIÇO COM APACHE KAFKA

Este documento fornece um guia para a criação e configuração de microsserviços com Apache Kafka usando Micronaut. O exemplo inclui dois microsserviços: `orchestrator-service` e `service-dois`. Ambos os serviços estão configurados para produzir e consumir mensagens usando Kafka.

Crie dois novos microsserviços com o nome de **orchestrator-service** e **service-dois**, adicionar eles no mesmo módulo, como foi feito em **“COMO CRIAR E IMPLEMENTAR UM MICRO SERVIÇO COM JAVA E MICRONAUT”**:

## ADICIONANDO DEPENDÊNCIAS, CONFIGURAÇÕES INICIAIS E APRESENTANDO ESTRUTURA DO PROJETO

Primeiramente deve-se adicionar a dependência do kafka em todos os microsserviços:
````xml
    <dependency>
        <groupId>io.micronaut.kafka</groupId>
        <artifactId>micronaut-kafka</artifactId>
        <scope>compile</scope>
    </dependency>
````

É importante definir a estrutura do projeto, criando as pastas para dividir as responsabilidades:

    orchestrator-service/
    │
    ├── config/ 
    ├── service/
    ├── kafka/  
    ├── exceptions/
    ├── dtos/
    ├── utils/
    ├── saga/
    ├── enums/
> **OBS:** `config` e `kafka` deve ser adicionado em todos os microsserviços.

Com isso vamos adicionar algumas configurações do kafka no resources/application.properties/yaml. 
> **OBS:** deve ser adicionada a mesma configuração em todos os microsserviços

PARA **PROPERTIES**:
````properties
kafka.bootstrap.servers = localhost:9092
logger.levels.org.apache.kafka= OFF
````
> **OBS:** `kafka.bootstrap.servers`: Essa configuração vai servir para conectar ao cliente kafka e apontar os micro serviços para o mesmo cliente/servidor.

> **OBS:** `logger.levels.org.apache.kafka`: Essa configuração serve para evitar que o kafka fique lançando logs constantemente.

PARA **YAML**:
````yaml
kafka:
    bootstrap:
        servers: localhost:9092
    logger:
        levels:
            org.apache.kafka: OFF
````

Deve ser adicionado também uma configuração para identificar qual micro serviço está trabalhando, **isso deve ser adicionado em todos os micro serviços**, usando a concatenação **nome-microservice-group**, então em  **resources/application.yaml/properties:**

PARA **PROPERTIES**:
````properties
kafka.consumer.group-id= orchestrator-group
kafka.consumer.auto-offset-reset= latest
````
> **OBS:** `kafka.consumer.group-id`  Serve para identificar o micro serviço que ele pertence.

> **OBS:** `kafka.consumer.auto-offset-reset`  Serve para sempre buscar todos os eventos mais recentes.

PARA **YAML**:
````yaml
kafka:
  consumer:
    group-id: orchestrator-group
    auto-offset-reset: latest
````
## CONFIGURANDO TÓPICOS DO KAFKA

Em **orchestrator-service** deve ser adicionado nessas configurações os tópicos da aplicação que vai ser responsável por **produzir e consumir** as mensagens que serão enviadas para esses tópicos, entao em  **resources/application.yaml/properties**:

PARA **PROPERTIES**:
````properties
kafka.topic.start = start
kafka.topic.finish-success= finish-success
kafka.topic.finish-fail= finish-fail
kafka.topic.orchestrator= orchestrator
````

PARA **YAML**:
````yaml
kafka:
  topic:
    start: start
    finish-success: finish-success
    finish-fail: finish-fail
    orchestrator: orchestrator
````
Em **service-um** deve ser adicionado nessas configurações os tópicos da aplicação que vai ser responsável por **produzir e consumir** as mensagens que serão enviadas para esses tópicos:

PARA **PROPERTIES**:
````properties
kafka.topic.start= start
````

PARA **YAML**:
````yaml
kafka:
    topic:
        start: start
````

Em **service-dois** deve ser adicionado nessas configurações os tópicos da aplicação que vai ser responsável por **produzir e consumir** as mensagens que serão enviadas para esses tópicos, então em **resources/application.yaml/properties:**

PARA **PROPERTIES**:
````properties
kafka.topic.orchestrator= orchestrator
kafka.topic.payment-success= payment-success
kafka.topic.payment-fail= payment-fail
````
PARA **YAML**:
````yaml
kafka:
  topic:
    orchestrator: orchestrator
    payment-success: payment-sucess
    payment-fail: payment-fail
````
## IMPLEMENTANDO CONFIGURAÇÃO DO KAFKA

Com isso, já podemos criar a classe **KafkaConfig** que vai servir para adicionar alguns dados importantes de serialização e deserialização, então podemos adicionar essa classe dentro do pacote config de todos os microsserviços, **Obs: Essas configurações devem ser feitas em todos os micro serviços existentes e futuros.**

Anota ela com **@Factory**, essa anotação vai servir para definir que a classe fornece beans para o contexto de injeção de dependência.
Adiciona três atributos do tipo String que vai servir para apontar com o **@Value** para o caminho do kafka que está lá no **resources/application.yaml/properties:**
````java
@Value("${kafka.bootstrap.servers}")
private String bootstrapServers;
@Value("${kafka.consumer.group-id}")
private String groupId;
@Value("${kafka.consumer.auto-offset-reset}")
private String autoOffsetReset;
````

Depois disso, vamos precisar de alguns métodos de configuração, **consumerProps e o producerProps**:
````java
private Map<String, Object> consumerProps() {
    Map<String, Object> props = new HashMap<>(); 
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); 
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); 
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); 
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); 
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    return props;
}
````

> ``ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG`` Define os servidores Kafka para conexão inicial.

> ``ConsumerConfig.GROUP_ID_CONFIG`` Define o ID do grupo de consumer.

> ``ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG ``Define o deserializador de chaves.

> ``ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG ``Define o deserializador de valores.

> ``ConsumerConfig.AUTO_OFFSET_RESET_CONFIG`` Define o comportamento do consumer quando não há offset inicial ou quando o offset atual não existe mais.

````java
private Map<String, Object> producerProps() {
    Map<String, Object> props = new HashMap<>(); 
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); 
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); 
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    return props;
}
````

> ``ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG`` Define o serializador de chaves.

> ``ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG`` Define o serializador de valores.

Vamos precisar também de alguns métodos de criação de beans, **kafkaConsumer e o kafkaProducer**:

De início, ele vai ser anotado com **@Singleton** para garantir que apenas uma instância seja criada para esse serviço e **@Bean** que é para a classe conseguir identificar que ele é um bean.
````java
@Singleton
@Bean
public KafkaConsumer<String, String> kafkaConsumer() {
    return new KafkaConsumer<>(consumerProps());
}
@Singleton
@Bean
public KafkaProducer<String, Object> kafkaProducer() {
    return new KafkaProducer<>(producerProps());
}
````

E por fim, vamos criar um método que vai criar os tópicos kafka automáticamente, mas para isso vamos precisar de uma classe enum que vai definir cada tópico cujo o nome deve ser igual ao nome que foi adicionado no **resources/application.yaml/properties.** Então, no diretório **enums** crie uma classe do tipo enum chamada **ETopic** e dentro dela coloque o nome dos tópicos e um atributos topic com o construtor e o get para usar na classe de configuração. 
> **OBS:** Essa configuração só é usada APENAS na classe de configuração do kafka, ou seja, em orchestrator-service, e nela deve ser adicionada todos os tópicos das aplicações:
````java
START("start"),
ORCHESTRATOR("orchestrator"),
PAYMENT_SUCCESS("payment-success"),
PAYMENT_FAIL("payment-fail");
FINISH_SUCCESS("finish-success"),
FINISH_FAIL("finish-fail");

private final String topic;

ETopic(String topic) {
    this.topic = topic;
}
public String getTopic() {
    return topic;
}
````

Voltando para a classe de configuração do kafka, vamos criar um método chamado buidTopic, que vai ser responsável por receber o tópico e configurar com o nome, número de partições e número de réplicas:
````java
private NewTopic buildTopic(String name){
    NewTopic topic = new NewTopic(name, 1, (short)1);
    return topic;
}
````

E com isso, já podemos criar os métodos que vão receber os tópicos:

Anotando todos os métodos com **@Bean** que vai servir para criar os tópicos dentro do kafka.
````java
@Bean
public NewTopic startTopic(){
    return buildTopic(ETopic.START.getTopic());
}
@Bean
public NewTopic orchestratorTopic(){
    return buildTopic(ETopic.ORCHESTRATOR.getTopic());
}
@Bean
public NewTopic payment_successTopic(){
    return buildTopic(ETopic.PAYMENT_SUCCESS.getTopic());
}
@Bean
public NewTopic payment_failTopic(){
    return buildTopic(ETopic.PAYMENT_FAIL.getTopic());
}
@Bean
public NewTopic finish_successTopic(){
    return buildTopic(ETopic.FINISH_SUCCESS.getTopic());
}
@Bean
public NewTopic finish_failTopic(){
    return buildTopic(ETopic.FINISH_FAIL.getTopic());
}
````
Como não é usado a classes ETopic nas outras aplicações deve ser adicionado alguns atributos que vão receber esses tópicos:

Em **service-um** como só tem o tópico de start então dentro de **KafkaConfig** vai receber o atributo de startTopic do tipo String e método que vai consumir esse tópico, ou seja a classe recebe as mesmas configurações mas o tópico que cada um nele pertence:
````java
@Value("${kafka.topic.start}")
private String startTopic;

public NewTopic startTopic(){
    return buildTopic(startTopic);
}
````
Em **service-dois** como só tem os tópicos de **payment-success e payment-fail**, então dentro de **KafkaConfig** vai receber os atributos de cada um deles do tipo String e método que vai consumir esses tópicos:
````java
@Value("${kafka.topic.payment-success}")
private String paymentSuccessTopic;

@Value("${kafka.topic.payment-fail}")
private String paymentFailTopic;

@Bean
public NewTopic paymentSuccessTopic(){
    return buildTopic(paymentSuccessTopic);
}
@Bean
public NewTopic paymentFailTopic(){
    return buildTopic(paymentFailTopic);
}
````

Finalizando essa configuração, precisaremos voltar ao micro serviço principal (service-um) para adicionar algumas entidades que o kafka vai utilizar para enviar as mensagens, nesse exemplo vamos usar o **MongoDB** como banco de dados.

## CRIAÇÃO DOS MODELS E REPOSITORY DOS EVENTS NO SERVICE-UM

1. Em **models** adicione mais duas entidades **Event e History**:

2. Em **Event** anote a classe com **@Serdeable, @MappedEntity, @AllArgsConstructor e @NoArgsConstructor.**

3. Adicione os atributos e anote id com @Id e @GeneratedValue:
````java
@Id
@GeneratedValue
private String id;
private Order payload;
private String source;
private String status;
private List<History> eventHistory;
private LocalDateTime createdAt;
````

4. Crie os **Gets e Sets** de todos os atributos.

Em History anote a classe com **@Serdeable, @AllArgsConstructor e @NoArgsConstructor.**

1. Adicione os atributos:
````java
private String source;
private String status;
private String message;
private LocalDateTime createdAt;
````

2. Crie os **Gets e Sets** de todos os atributos.

3. Aqui já podemos criar a classe que vai salvar todos os eventos publicados, então em repository crie uma interface chamada **EventRepository:**

4. Anote ela com **@MongoRepository(databaseName= “orders-db”)** e estende a classe ao **CrudRepository<Event, String>**

## CRIAÇÃO DA CLASSE DE SERVIÇO DOS EVENTS

Já podemos criar a regra de negócio que vai salvar os dados no banco, então em service crie uma classe chamada **EventService:**

1. Anote a classe com **@Singleton**

2. Injeta o **EventRepository** e adiciona o @Inject nele

Crie um método que vai listar todos os eventos:
````java
public List<Event> findAll(){
    return eventRepository.findAll();
}
````

Crie um método que vai buscar os eventos por id:
````java
public Event findById(String id){
    return eventRepository.findById(id).orElseThrow(() -> 
        new RuntimeException("Event not found by ID."));
}
````

Crie um método que salvar todos os eventos:
````java
public Event save(Event event){
    return eventRepository.save(event);
}
````

Após isso, a classe EventService  deve ser injetado dentro de OrderService com o @Inject e deve criar o método para criar o payload para quando esse método for chamado dentro no endpoint de criação do pedido, para o producer mandar para o kafka:

````java
private Event createPayload(Order order){
    Event event = new Event();
    event.setId(order.getId());
    event.setPayload(order);
    event.setCreatedAt(LocalDateTime.now());
    eventService.save(event);
    return event;
}
````

Agora é só adicionar e injetar(@Inject) os atributos das classes do kafka, no caso o Producer, e criar o método que vai adicionar o pedido, salvar no banco e enviar para o kafka:
````java
public Order addOrder(Order order){
    Order orders =  orderRepository.save(order);
    producer.sendEvent(jsonUtil.toJson(createPayload(orders));
    return orders;
}
````
## CRIAÇÃO DA CLASSE CONTROLLER

Agora só falta o controller  que vai servir para mandar requisições nesses endpoints:

1. Crie a classe **EventController** em controller e anote ela com **@Controller(“event/”)**

2. Injeta o EventService e anota @Inject

3. Crie os métodos que vai trazer os serviços:
````java
@Get("/{id}")
public Event findById(@QueryValue String id){
    return eventService.findById(id);
}
@Get()
public List<Event> findAll(){
    return eventService.findAll();
}
````
Feito isso, podemos adicionar TODAS essas entidades de **models** em **dto** de todos os microsserviços. 

> **LEMBRAR** de tirar as anotações de persistência, pois dtos não precisar persistir, deixar anotado apenas **@Serdeable.**

## CRIAÇÃO DAS CLASSES ENUMS DO EVENTSOURCE E STATUS

Após isso, em (orchestrator-service) vamos precisar de algumas classes enum, então em enums adicione duas classes **EEventSource e EStatus:**

Dentro de **EEventSource**, adicione o nome de todos os micro serviços existentes:

    ORCHESTRATOR,
    PAYMENT_SERVICE

Dentro de **EStatus**, adicione todos os possíveis status. OBS: O EStatus deve estar em todos os microsserviços que recebem as mensagens:

    SUCCESS,
    ROLLBACK_PENDING,
    FAIL

Com isso, dentro de dto > Event e History vamos precisar fazer algumas alterações que seria mudar o tipo de dois atributos source e status e adicionar a anotação @Enumerated(EnumType.STRING) para que o banco de dados aceite o valor String em classes enums,  então faça as seguintes alterações que foram criadas recentemente:
````java
@Enumerated(EnumType.STRING)
private EEventSource source;
@Enumerated(EnumType.STRING)
private EStatus status;
````

E apenas dentro de **Event** que está dentro de dto vamos criar um método que vai garantir que toda vez que o atributo eventHistory estiver null, ele vai criar um ArrayList:
````java
public void addToHistory(History history){
    if(isEmpty(eventHistory)){
    eventHistory = new ArrayList<>();
    }
    eventHistory.add(history);
}
````
## CRIAÇÃO DA CLASSE DE CONVENÇÃO JSONUTIL

Pronto, com isso já temos o formato de como a mensagem vai ser enviada, agora temos que garantir que a mensagem seja convertida de String para Json, dentro de utils vamos criar uma Classe chamada JsonUtil que servir para converter as mensagens recebidas em todos os microsserviços vamos precisar dela:

1. Anote a classe com **@Singleton e @AllArgsConstructor**;

2. Injeta o ObjectMapper do pacote **io.micronaut.serde**;

3. @Inject no atributo;

4. Crie um método chamado toJson, esse método vai receber um objeto e vai transformá-lo em String:
````java
public String toJson(Object object){
    try{
        return objectMapper.writeValueAsString(object);
    } catch (Exception e){
        return "";
    }
}
````

5. Crie o método chamado toEvent, esse método vai receber o json do tipo string e vai converter para o objeto do tipo Event:
````java
public Event toEvent(String json){
    try{ 
        return objectMapper.readValue(json, Event.class);
    } catch (Exception e){
        return null;
    }
}
````

## CRIAÇÃO DA CLASSE PRODUCER NO ORCHESTRATOR-SERVICE

Contudo, dentro do diretório kafka no **orchestrator-service**, já podemos criar a classe de Producer:

Dentro de Producer:

1. Injeta KafkaProducer como atributo e adiciona @Inject

2. Adiciona o Logger para obter os log da aplicação:
````java
private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
````

3. Adiciona o método sendEvent que vai ser responsável por produzir a mensagem e enviar para o kafka:
````java
public void sendEvent(String payload, String topic){
    try {
        LOG.info("Sending event to topic {} with data {}", topic, payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, payload); kafkaProducer.send(record);
    } catch (Exception e) {
        LOG.error("Error trying to send data to topic {} with data {}", topic, payload, e);
    }
}
````
## CRIAÇÃO DA CLASSE HANDLER E SAGA EXECUTION CONTROLLER

Agora em **orchestrator-service**, dentro de **saga** vamos criar uma classe final chamada Handler, que ela basicamente vai servir para definir os passos que a aplicação vai seguir, com isso, vai ser criado uma matriz definindo os possíveis eventos, com os possíveis status e possíveis tópicos:

> **Exemplo:** Se o status de Orchestrator for SUCCESS, ele passa para o Payment_Service e se o Payment_Service for SUCESS ele passa para Finish_Success.
````java
private Handler(){}

public static final Object[][] HANDLER = {
    {ORCHESTRATOR, SUCCESS, PAYMENT_SUCCESS},
    {ORCHESTRATOR, FAIL, FINISH_FAIL},
    {PAYMENT_SERVICE, ROLLBACK_PENDING, PAYMENT_FAIL},
    {PAYMENT_SERVICE, FAIL, FINISH_FAIL}, 
    {PAYMENT_SERVICE, SUCCESS, FINISH_SUCCESS}
};

public static final int EVENT_SOURCE_INDEX = 0;
public static final int STATUS_INDEX = 1;
public static final int TOPIC_INDEX = 2;
````
Mas isso não vai funcionar sozinho, existe uma classe que vai fazer esse controle para que cada passo seja seguido corretamente, então ainda dentro de **saga** crie uma classe chamada **SagaExecutionController** e anota ela com **@Controller:**

1. Adicionar o Logger:
````java
private static final Logger LOG = LoggerFactory.getLogger(SagaExecutionController.class);
````

2. Criar um método privado de validação do source e do status, esse método vai servir para comparar o Event que já tem salvo com o Event recebido:
````java
private boolean isEventSourceAndStatusValid(Event event, Object[] row){
    var source = row[EVENT_SOURCE_INDEX];
    var status = row[STATUS_INDEX];
    return source.equals(event.getSource()) && status.equals(event.getStatus());
}
````

3. Criar um método que vai buscar esses tópicos e verificar se existe:
````java
public ETopic findTopicBySourceAndStatus(Event event){
    return (ETopic) Arrays.stream(HANDLER)
        .filter(row -> isEventSourceAndStatusValid(event, row)) 
        .map(i -> i[TOPIC_INDEX])
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Topic not found!"));
}
````

4. Criar um método que vai criar a saga:
````java
private String createSagaId(Event event){
    return format("TABLE ID: %s | EVENT ID %s", event.getPayload().getId(), event.getId());
}
````

5. Criar o método privado que vai controlar os logs da saga e direcionar onde cada tópico deve ir:
````java
private void logCurrentSaga(Event event, ETopic topic){
    var sagaId = createSagaId(event);
    EEventSource source = event.getSource();
    switch (event.getStatus()){
        case SUCCESS -> LOG.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}" , source, topic, sagaId);
        case ROLLBACK_PENDING -> LOG.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}" , source, topic, sagaId);
        case FAIL -> LOG.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}" , source, topic, sagaId);
    }
}
````

6. E por fim, criar o método que vai levar para o próximo tópico:
````java
public ETopic getNextTopic(Event event) {
    if (event.getSource() == null || event.getStatus() == null) {
        throw new RuntimeException("Source and status must be informed.");
    }
    var topic = findTopicBySourceAndStatus(event); 
    logCurrentSaga(event, topic);
    return topic;
}
````
## CRIAÇÃO DA CLASSE DE SERVIÇO

Assim, em **orchestrator-service** já podemos criar a classe de serviço que vai ser chamado de **OrchestratorService** que vai servir para atualizar os dados quando um tópico for mudado para outro:

1. Anote a classe com @Singleton e @AllArgsConstructor

2. Crie o Logger como foi feito em outras classes

3. Injeta três atributos das classes que foram criadas, **JsonUtil, Producer e SagaExecutionControler**

4. @Inject nos atributos

5. Crie o método getTopic que vai servir para enviar a mensagem para o próximo tópico:
````java
private ETopic getTopic (Event event){
return sagaExecutionController.getNextTopic(event);
}
````

6. Crie o método que vai criar o arraylist e adicionar os dados de source e status:
````java
private void addHistory(Event event, String message){
    History history = new History();       
    history.setSource(event.getSource()); 
    history.setStatus(event.getStatus()); 
    history.setMessage(message); 
    history.setCreatedAt(LocalDateTime.now()); 
    event.addToHistory(history);
}
````

7. Crie o método que vai enviar a mensagem produzida para o tópico:
````java
private void sendToProducerWithTopic(Event event, ETopic topic){
producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
}
````

8. Crie também os métodos que vai criar e alterar os dados dos tópicos quando for chamado:
````java
public void start(Event event){
    event.setSource(EEventSource.ORCHESTRATOR); 
    event.setStatus(EStatus.SUCCESS);
    ETopic topic = getTopic(event);
    LOG.info("STARTED!");
    addHistory(event, "Started!"); 
    sendToProducerWithTopic(event,topic);
}

public void finishSuccess(Event event){
    event.setSource(EEventSource.ORCHESTRATOR); 
    event.setStatus(EStatus.SUCCESS); 
    LOG.info("FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
    addHistory(event, "Finished successfully!");
}

public void finishFail(Event event){
    event.setSource(EEventSource.ORCHESTRATOR); 
    event.setStatus(EStatus.FAIL);
    LOG.info("FINISHED WITH ERRORS FOR EVENT {}", event.getId());
    addHistory(event, "Finished with errors!");
}
````

9. E por fim, o método que vai seguir para próxima saga:
````java
public void continueSaga(Event event) {
    ETopic topic = getTopic(event);
    LOG.info("SAGA CONTINUING FOR EVENT {}", event.getId());
    sendToProducerWithTopic(event,topic);
}
````
## CRIANDO CLASSE CONSUMER ORCHESTRATOR-SERVICE
Ainda em orchestrator-service, já podemos criar a classe **Consumer** que vai consumir todas as mensagens que a classe anterior produziu:

1. Anota a classe com **@KafkaListener(groupId = “${kafka.consumer.group-id}”)** , essa anotação vai apontar diretamente para o micro serviço que vai ser consumido.

2. Adiciona o Logger

3. Injeta OrchestratorService e JsonUltil e anota eles com @Inject

4. Crie os métodos que vai consumir as mensagens, cada um deles anotado com **@Topic(“${kafka.topic.topico-onde-a-mensagem-foi -enviada}”):**
````java
@Topic("${kafka.topic.start}")
public void consumerStartEvent(String payload){
    LOG.info("Receiving event {} from start topic" , payload);
    Event event = jsonUtil.toEvent(payload); 
    orchestratorService.start(event);
}

@Topic("${kafka.topic.orchestrator}")
public void consumerOrchestratorEvent(String payload){
    LOG.info("Receiving event {} from orchestrator topic" , payload); 
    Event event = jsonUtil.toEvent(payload); 
    orchestratorService.continueSaga(event);
}

@Topic("${kafka.topic.finish-success}")
public void consumerFinishSuccessEvent(String payload){
    LOG.info("Receiving event {} from finish-success topic" , payload); 
    Event event = jsonUtil.toEvent(payload); 
    orchestratorService.finishSuccess(event);
}

@Topic("${kafka.topic.finish-fail}")
public void consumerFinishFailEvent(String payload){
    LOG.info("Receiving ending notification event {} from finish-fail topic" , payload);
    Event event = jsonUtil.toEvent(payload); 
    orchestratorService.finishFail(event);
}
````
## CRIANDO CLASSE PRODUCER NO SERVICE-UM

Com isso, as outras classes também vão precisar do Producer, porém como o service-um não precisa consumir mensagens, então em service-um no pacote kafka, vamos criar apenas o Producer:

1. Injeta KafkaProducer como atributo e adiciona @Inject

2. Adiciona o Logger para obter os log da aplicação:
````java
private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
````

3. Adiciona o atributo onde vai ser enviado as mensagens:
````java
@Value("${kafka.topic.start}")
private String startTopic;
````

4. Adiciona o método sendEvent que vai ser responsável por produzir a mensagem e enviar para o kafka:
````java
public void sendEvent(String payload){
    try {
        LOG.info("Sending event to topic {} with data {}", startTopic, payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(startTopic, payload);
        kafkaProducer.send(record);
    } catch (Exception e) {
        LOG.error("Error trying to send data to topic {} with data {}", startTopic, payload, e);
    }
}
````

## CRIANDO ENUMS, MODELS E REPOSITORY DO SERVICE-DOIS

Agora vamos começar a trabalhar no service-dois:

1. Em enums, crie uma classe do tipo enum chamada **EPaymentStatus:**

2. Adicione essas constantes:

    PENDING,
    SUCCESS,
    REFUND

Em models, crie uma classe chamada Payment:

1. Anote ela com **@MappedEntity, @Serdeable, @AllArgsConstructor e @NoArgsConstructor**

2. Crie os atributos:
````java
private String id;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
private double totalAmount;
private EPaymentStatus status;
````

3. Adicione @Id em id e @Enumerated(EnumType.STRING) em status

4. Crie todos os gets e sets

5. Crie um método que vai mudar o status para PENDING e o createAt que vai atualizar o horário que foi enviado a mensagem:
````java
@PrePersist
public void prePersist(){
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
    status = EPaymentStatus.PENDING;
}
````

6. Crie o método que vai atualizar quando a mensagem for finalizada com sucesso:
````java
@PreUpdate
public void preUpdate(){
updatedAt = LocalDateTime.now();
}
````

7. Crie o PaymentRepository em repository da mesma forma que foi feito com os outros. 
> OBS: não esqueça de colocar a dependência do drive do mongodb
````xml
<dependency>
	<groupId>org.mongodb</groupId>
	<artifactId>mongodb-driver-sync</artifactId>
	<scope>runtime</scope>
</dependency>
````
## CRIANDO CLASSE PRODUCER DO SERVICE-DOIS

Crie o Producer que vai servir para produzir a mensagem para o orchestrator:

1. Injeta KafkaProducer como atributo e adiciona @Inject

2. Adiciona o Logger para obter os log da aplicação:
````java
private static final Logger LOG = LoggerFactory.getLogger(Producer.class);
````

3. Adiciona os atributo onde vai ser enviado as mensagens:
````java
@Value("${kafka.topic.orchestrator}")
private String orchestratorTopic;
````

4. Adiciona o método sendEvent que vai ser responsável por produzir a mensagem e enviar para o kafka:
````java
public void sendEvent(String payload){
    try {
        LOG.info("Sending event to topic {} with data {}", orchestratorTopic, payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(orchestratorTopic, payload);
        kafkaProducer.send(record);
    } catch (Exception e) {
        LOG.error("Error trying to send data to topic {} with data {}", orchestratorTopic, payload, e);
    }
}
````
## CRIANDO A CLASSE DE SERVIÇO DO SERVICE-DOIS

Agora em service, podemos criar o PaymentService:

1. Anote com @Singleton

2. Adicione o Logger

3. Injeta com @Inject os atributos JsonUtil, Producer e PaymentRepository

4. Crie um método de validação para verificar se o pagamento existe:
````java
public void checkCurrentValidation(Event event){
    if(paymentRepository.existsById(event.getPayload().getId())){
        throw new RuntimeException("There's another Id for this validation.");
    }
}
````

5. Crie um método para calcular o valor:
````java
private double calculateAmount(Event event){
    return event.getPayload().getProducts().stream() .map(product -> product.getPrice())
    .reduce(0.0, Double::sum);
}
````

6. Crie um método para criar um pagamento pendente:
````java
public void createPendingPayment(Event event){
    double totalAmount = calculateAmount(event);
    Payment payment = new Payment(); 
    payment.setId(event.getPayload().getId()); 
    payment.setTotalAmount(totalAmount); 
    paymentRepository.save(payment);
}
````

7. Crie um método para verificar se um pagamento é menor que 0.1:
````java
private static final Double MIN_AMOUNT_VALUE = 0.1;
// Adicione isso nos atributos.
private void validateAmount(double amount){
    if(amount < MIN_AMOUNT_VALUE){
        throw new RuntimeException("The minimum amount available is ".concat(MIN_AMOUNT_VALUE.toString()));
    }
}
````
8. Crie um método para atualizar o status de transação para sucesso e salvar no banco de dados:
````java
private void changePaymentToSuccess(Payment payment){
    payment.setStatus(EPaymentStatus.SUCCESS); paymentRepository.update(payment);
}
````
9. Crie um método para adicionar o histórico de transação(history):
````java
private void addHistory(Event event, String message){
    History history = new History(); 
    history.setSource(event.getSource()); 
    history.setStatus(event.getStatus()); 
    history.setMessage(message);
    history.setCreatedAt(LocalDateTime.now()); 
    event.addToHistory(history);
}
````
10. Crie um método para atualizar o histórico de transação(history) quando der FAIL:
````java
private void handleFailCurrentNotExecuted(Event event, String message){
    event.setStatus(String.valueOf(ROLLBACK_PENDING)); 
    event.setSource(“PAYMENT_SERVICE”); 
    addHistory(event, "Fail to realized payment: ".concat(message));
}
````
11. Crie um método para buscar por id uma transação:
````java
private Payment findById(Event event){
    return paymentRepository.findById(event.getPayload().getId()).orElseThrow(() -> new RuntimeException("Payment not found by Id. "));
}
````
12. Crie um método para atualizar o status de transação para reembolsar no caso de FAIL, caso ocorra uma transação indevida:
````java
private void changePaymentStatusToRefund(Event event){
    Payment payment = findById(event); 
    payment.setStatus(EPaymentStatus.REFUND); 
    paymentRepository.update(payment);
}
````
13. Crie um método para realizar e publicar o evento de reembolso:
````java
public void realizedRefund(Event event){
    event.setStatus(String.valueOf(FAIL)); event.setSource(“PAYMENT_SERVICE”);
    try{
        changePaymentStatusToRefund(event); addHistory(event, "Rollback executed for payment! ");
    }catch (Exception ex){
        addHistory(event, "Rollback not executed for payment! ".concat(ex.getMessage()));
    }
    producer.sendEvent(jsonUtil.toJson(event));
}
````

14. Crie um método para atualizar o status de transação para sucesso:
````java
private void handleSuccess(Event event){
    event.setStatus(String.valueOf(SUCCESS)); 
    event.setSource(“PAYMENT_SERVICE”); 
    addHistory(event, "Payment realized successfully");
}
````

15. E por fim, crie um método para realizar o pagamento e publicar no kafka:
````java
public void realizedPayment(Event event){
    try{ checkCurrentValidation(event);
        createPendingPayment(event);
        Payment payment = findById(event); 
        validateAmount(payment.getTotalAmount()); 
        changePaymentToSuccess(payment); 
        handleSuccess(event);
    }catch (Exception ex) {
        LOG.error("Error trying to make payment: " , ex);
        handleFailCurrentNotExecuted(event, ex.getMessage());
    }
    producer.sendEvent(jsonUtil.toJson(event));
}
````
## CRIANDO CLASSE CONSUMER DO SERVICE-DOIS

Agora podemos criar a classe Consumer que vai consumir todas as mensagens que a classe anterior enviou:

1. Anota a classe com **@KafkaListener(groupId = “${kafka.consumer.group-id}”)** , essa anotação vai apontar diretamente para o micro serviço que vai ser consumido.

2. Adiciona o Logger

3. Injeta PaymentService e JsonUltil e anota eles com @Inject

4. Crie os métodos que vai consumir as mensagens, cada um deles anotado com **@Topic(“${kafka.topic.topico-onde-a-mensagem-foi -enviada}”):**
````java
@Topic("${kafka.topic.payment-success}")
public void consumerPaymentSuccessEvent(String payload){
    LOG.info("Receiving success event {} from payment-success topic" , payload);
    Event event = jsonUtil.toEvent(payload);
    paymentService.realizedPayment(event);
}
@Topic("${kafka.topic.payment-fail}")
public void consumerPaymentFailEvent(String payload){
    LOG.info("Receiving rollback event {} from payment-fail topic" , payload);
    Event event = jsonUtil.toEvent(payload);
    paymentService.realizedRefund(event);
````













	
