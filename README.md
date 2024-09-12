## Tecnologias Utilizadas

- **Java 21**  

- **Micronaut**  

- **Docker**

- **Apache Kafka**

- **Prometheus**

- **Grafana**  

## Diagrama UML

<img src="./image/service-um.png" alt="Descrição da Imagem" width="720"/>

# COMO CRIAR UM MICRO SERVIÇO EM MICRONAUT:

Este tutorial é para programadores já familiarizados com desenvolvimento de backend java.

Entre nesse link: https://micronaut.io/launch

Adicione a versão mais recente do framework micronaut, escolha a linguagem Java, 
escolha a versão mais recente do java se preferir, escolha um nome para o projeto, 
escolha um nome para o pacote, escolha uma ferramenta de construção pode ser Gradle ou Maven o que preferir, 
escolha a estrutura de teste JUnit, clique em Features/Dependências, adicione as dependências que preferir, 
depois é só clicar em Generate Project/Gerar Projeto. Com isso, vai baixar um arquivo em zip, é só extrair.

Crie uma pasta para ser a pasta principal para adicionar o módulos dos projetos, 
abre a pasta que você criou, mas CUIDADO: Quando a pasta é extraída ela cria uma pasta duplicada: 
Exemplo: service-um > service-um…, a pasta que deve ser arrastada é a segunda pasta. 
Com isso, já podemos abrir a IDE, vai aparecer uma notificação do maven, 
é só clicar em Load para carregar as dependências do projeto.
