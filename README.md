# Cognitive Services - YouTube, Translate & TTS con Spring Boot
**Servicios Cognitivos de Rapid API**, este proyecto integra tres APIs:  
- **YouTube Info & Download API**: obtiene información y descarga de videos.  
- **Deep Translate API**: traduce texto entre múltiples idiomas.  
- **Streamlined Edge TTS API**: convierte texto en voz (Text-to-Speech).  

Mediante Spring Boot y WebFlux, el proyecto consume estas APIs de manera reactiva y escalable, con soporte para MongoDB como base de datos NoSQL.


## **1. Cognitive Services**
- Rapid API - YouTube Info & Download  
- Rapid API - Deep Translate  
- Rapid API - Streamlined Edge TTS  


## **2. Spring Boot**
<img src="https://upload.wikimedia.org/wikipedia/commons/4/44/Spring_Framework_Logo_2018.svg" align="right" style="height:60px; width: 200px"/>
- Java: JDK 17  
- IDE: IntelliJ IDEA | Visual Studio Code | Codespace  
- Maven: Apache Maven  
- Frameworks: Spring Boot + WebFlux  


## **3. Maven Dependencias**


### Dependencias principales
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.0.2</version>
</dependency>

