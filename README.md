# APÊNDICE III - TUTORIAL: INTEGRANDO MICROMETER AO MICRONAUT PARA APRESENTAR MÉTRICAS DE DESEMPENHO COM PROMETHEUS E GRAFANA
## INTRODUÇÃO
Este tutorial tem como objetivo ensinar como integrar o Micrometer ao Micronaut, mostrando todas as configurações e dependências necessárias para que a aplicação funcione corretamente, tanto localmente quanto em containers. Além disso, ele explora como monitorar as métricas de desempenho usando dashboards gráficos. O tutorial aborda a adição das dependências essenciais para o Micrometer e Prometheus, a configuração do Prometheus para coletar e exibir métricas, e um passo a passo para visualizar esses dados no Prometheus e Grafana, facilitando o acompanhamento do estado da aplicação.

## ADICIONANDO AS DEPENDÊNCIAS NECESSÁRIAS
O micrometer é uma biblioteca do java que permite coletar métricas de desempenho para monitorar as aplicações, seja ela capacidade de memória, threads, CPU, etc.

Para começar, vamos precisar configurar dependências do micrometer que vão permitir que as coletas de dados sejam feitas. 

Então no pom.xml da aplicação acrescentamos na seção de dependências:
````xml
<dependency>
  <groupId>io.micronaut.micrometer</groupId>
  <artifactId>micronaut-micrometer-core</artifactId>
</dependency>
<dependency>
  <groupId>io.micronaut</groupId>
  <artifactId>micronaut-management</artifactId>
</dependency>
````

Após carregar as dependências, é só rodar o projeto e testar se elas vão aparecer, usando `localhost:[port]/metrics`, com isso vai aparecer um **JSON** com o nome de todas as métricas que podem ser usadas, e para testar uma como exemplo pode usar a: 
    
    localhost:[port]/metrics/executor

Para melhorar ainda mais a aplicação e coletar algumas outras métricas importantes vamos adicionar a biblioteca do Prometheus. O Prometheus é compatível com o micrometer, ele pode ser acessado pelo grafana e assim os dados coletados e várias métricas monitoradas pelo Prometheus podem ser visualizados de forma gráfica através de dashboards.
Ainda no pom.xml podemos adicionar a dependência do Prometheus:
```xml
<dependency>
    <groupId>io.micronaut.micrometer</groupId>
    <artifactId>micronaut-micrometer-registry-prometheus</artifactId>
</dependency>
````
## CONFIGURAÇÃO DO PROMETHEUS
Feito isso, devemos configurar o resources/application.properties/yaml:

PARA **PROPERTIES**:
````properties
micronaut.export.prometheus.enabled= true
micronaut.export.prometheus.step= PT1M
micronaut.export.prometheus.descriptions= true
endpoints.prometheus.sensitive= false
````

> `micronaut.export.prometheus.enabled` Isso permite que o sistema colete e monitore todo sistema.
> `micronaut.export.prometheus.step(Padrão ISO-8601)` Permite que os dados sejam coletados de 1 em 1 minuto.
> ``micronaut.export.prometheus.descriptions`` Isso é para facilitar a compreensão das métricas e saber o que cada uma mede.
> `endpoints.prometheus.sensitive` Isso torna ele sensível publicamente se no caso fosse **true** precisaria de autenticação.

PARA **YAML**:
````yaml
micronaut:
  export:
    prometheus:
      enabled: true
      step: PT1M
      descriptions: true
endpoints:
  prometheus:
    sensitive: false
````
Com isso ele já deve estar funcionando, agora na raiz do projeto no mesmo local onde fica o docker-compose, devemos criar um pacote config e dentro do pacote um arquivo chamado **prometheus.yml** que servir para configurar o prometheus para o uso da interface, então dentro de prometheus.yml :
````yaml
global:
scrape_interval: 5s
scrape_configs:
- job_name: "serviceum"
  metrics_path: /prometheus
  static_configs:
- targets: ["ipconfig:8082"] 
````
**PROJETO RODANDO LOCALMENTE:** No lugar de ipconfig o que deve ser adicionado é o ip do host da sua máquina.

Para descobrir o ip: deve abrir o CMD e digitar ipconfig e procure pelo endereço IPv4 do Adaptador de Rede sem fio Wi-Fi, com isso é só colocar o numero do ip no lugar do ipconfig no prometheus.yml.
````shell
ipconfig
````

Com isso, é necessario rodar essas aplicações em containers, então é importante seguir o **"TUTORIAL BÁSICO DOCKER E DOCKERFILE:"**

Caso ja esteja rodando em um container, basta substituir o ipconfig pelo o nome do container, no caso o meu está service-um.

## ENTENDENDO INTERFACE GRÁFICA DO PROMETHEUS E GRAFANA

Feito isso é so seguir esse passo a passo:

Deve verificar se o Prometheus está com o state **UP**:
1. Acesse o link: localhost:9090, para entrar no WebSite do Prometheus.
2. Click em Status > Targets, com isso deve aparecer todos os microsserviços configurados no **prometheus.yml** e o State UP em todos
3. Caso o State não esteja 'UP', deverá rever as configurações do **prometheus.yml**


Já no Grafana deve-se seguir esses passos:
1. Entrar no link localhost:3000, que é o WebSite do Grafana

2. Fazer login com usuário: admin , e senha: admin, se pedir pra criar uma senha é só clicar em skip

Dentro do Grafana vamos conectar ao Prometheus, então:
1. Vai em Menu ao lado de Home > Connections > Add new connections e na barra de busca procura por Prometheus e procura por add new data source.
2. Em Connection > Prometheus Server URL: http://prometheus:9090
> Esse link é o nome do container do docker e a porta
3. Depois é só **Save e Test**

Agora vamos procurar um dashboard interessante de fácil análise:

1. Entre do site oficial do grafana: https://grafana.com/
2. Lá no rodapé do site procure por dashboards ou nesse link: https://grafana.com/grafana/dashboards/
3. Na barra de busca procure por JVM(Micrometer)
4. Em Get this dashboard,  procure por Import the dashboard template e copie o ID:
5. Com isso, de volta ao site, vá em Home > Dashboards > Create Dashboard > Import Dashboard, é só colocar o ID e clicar em Load
6. Select a Prometheus Data source,  escolha o prometheus e clique em Import.

Com isso, podemos seguir para o [ultimo passo](https://github.com/RavikFerreira/ProjectTCC/edit/feature/docker/README.md), em que vamos aprender um pouco mais sobre como criar os containers em todos os serviços!
