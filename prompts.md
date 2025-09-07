# 📝 Prompts Utilizados con DeepSeek AI

## 🎯 **Prompts Principales de Arquitectura**

### Prompt 1: Enunciado Inicial del Proyecto
```
Hola necesito que te comportes como un experto del desarrollo de software y arquitectura de software, tengo una prueba para aspirar a un cargo senior, yo ya he construido gran parte del microservicio. entonces necesito que no hagas nada si no que solo tengas en tu memoria el enunciado y lo que se solicita ya que posteriormente te haré un resumen de lo que llevo avanzado y tu me comenzarás a preguntar de las clases que se necesiten para seguir con el paso a paso de la construcción

El enunciado es el siguiente

[Texto completo del enunciado con todos los requisitos técnicos...]
```

### Prompt 2: Resumen del Estado Actual
```
📋 RESUMEN COMPLETO DEL CHAT - Distributed Inventory Management Service

🎯 ESTADO ACTUAL DEL PROYECTO:
✅ COMPLETADO:
Arquitectura Hexagonal implementada correctamente
Sistema de Autenticación JWT con seguridad WebFlux
InventoryService con lógica de negocio completa
Base de datos H2 con datos de prueba
Pruebas unitarias y de integración de seguridad
Endpoints básicos de inventory implementados

🚀 EN PROGRESO:
Controllers REST completos para inventory
Manejo de concurrencia con Optimistic Locking
Patrón Retry con backoff exponencial

[Estructura completa del proyecto y detalles técnicos...]
```

## 🔧 **Prompts de Desarrollo Técnico**

### Prompt 3: Configuración de Seguridad JWT
```
Ahora tengo mi controller y necesito que que funcione con la parte de seguridad

[Mostré el código de InventoryController y AuthController]

Al realizar el curl tengo error 401

curl --location 'http://localhost:8080/api/inventory' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGci...' \
--data '{
"productId": "prod-1",
"storeId": "store-1",
"currentStock": 100,
"reservedStock": 0,
"minimumStockLevel": 10,
"maximumStockLevel": 500
}'

Que información de clases necesitas para ver el error más a fondo?
```

### Prompt 4: Dockerización del Proyecto
```
El problema se ha solucionado pero como solo tengo unas horas para entregar, necesito dejar la mejora de los endpoints en pausa por un momento y comenzar a preparar la entrega, por eso necesito la dockerizar el proyecto así que por favor dime que información necesitas para comenzar este paso
```

### Prompt 5: Resolución de Problemas con Docker
```
He realizado los cambios como me has dicho e incluso te los comparto pero aun sale el error

[Compartí los archivos Dockerfile, docker-compose.yml, application-docker.properties]

Sigue saliendo error a pesar de los cambios

[Mostré el error completo del log de Docker]
```

### Prompt 6: Debug de Configuración Docker
```
pero si el path que usa es jdbc:h2:file:data/inventorydb;USER=sa, la parte de USER=sa la está concatenando de alguna manera y no se como si los properties y los archivos de docker no lo concatenan
```

### Prompt 7: Entendimiento de Profiles Spring
```
Antes de seguir con el resto de puntos que me recomendaste y recuérdamelos para el futuro, en el run.md puedo poner instrucciones de mvn para construir el jar?
Si me vas a compartir el run.md por favor coloca caracters de escaape a los ```
```

## 🏗️ **Arquitectura y Patrones Implementados**

### Principios y Patrones Aplicados:
```
- Arquitectura Hexagonal (Puertos y Adaptadores)
- Principios SOLID
- Clean Code
- Programación Reactiva con WebFlux
- JWT Authentication
- Optimistic Locking para concurrencia
- Retry Pattern con backoff exponencial
- Test Unitarios y de Integración
- Seguridad con Spring Security WebFlux
- MapStruct para mapeo de objetos
- Lombok para reducción de boilerplate
```

### Stack Tecnológico:
```
- Java 17
- Spring Boot 3.5.5
- Spring WebFlux
- R2DBC (Database Reactivo)
- H2 Database
- JWT Authentication
- Spring Security OAuth2 Resource Server
- MapStruct
- Lombok
- Spring Retry
- Springdoc OpenAPI
```

## 📊 **Estrategia de Desarrollo**

### Metodología de Trabajo:
```
1. Análisis del enunciado y requisitos
2. Diseño de arquitectura hexagonal
3. Implementación por capas (domain, application, infrastructure)
4. Configuración de seguridad JWT
5. Implementación de endpoints reactivos
6. Dockerización del proyecto
7. Documentación completa
8. Pruebas y validación
 ```

### Decisiones Técnicas Clave:
```
- WebFlux sobre Web MVC para mejor escalabilidad
- R2DBC para operaciones de base de datos reactivas
- H2 en memoria para desarrollo y testing
- JWT para autenticación stateless
- Arquitectura hexagonal para desacoplamiento
- Variables de entorno para configuración Docker
- Multi-stage Docker build para optimización
```

## 🚀 **Proceso de Construcción Paso a Paso**

### Fase 1: Arquitectura y Configuración Inicial
```
- Configuración de Spring Boot 3.5.5 con Java 17
- Estructura de paquetes hexagonal
- Configuración de dependencias Maven
- Setup de base de datos H2 con R2DBC
- Configuración de seguridad básica
```

### Fase 2: Implementación del Dominio
```
- Entidades de dominio: InventoryItem, Product, Store
- Servicios de dominio con lógica de negocio
- Repositorios y ports
- Excepciones de dominio personalizadas
- Patrones de concurrencia
```

### Fase 3: Infraestructura y Web
```
- Controllers reactivos
- DTOs y mappers
- Configuración de seguridad JWT
- Filters de autenticación
- Manejo de excepciones global
```

### Fase 4: Dockerización y Deployment
```
- Configuración de Dockerfile multi-stage
- Docker Compose para orquestación
- Variables de entorno para configuración
- Volumes para persistencia de datos
- Health checks para monitoring
```

## 📋 **Lecciones Aprendidas**

### Desafíos Resueltos:
```
1. Configuración de WebFlux Security con JWT
2. Integración de R2DBC con H2
3. Dockerización con Spring Profiles
4. Manejo de paths de H2 en contenedores
5. Configuración de variables de entorno entre sistemas
 ```

### Soluciones Implementadas:
```
- Uso de H2 en memoria para simplificar Docker
- Configuración explícita de profiles Spring
- Variables de entorno para sobreescritura
- Logs detallados para debugging
- Documentación completa de configuración
```

## 🎯 **Resultado Final**

### Microservicio Entregable:
```
- ✅ Arquitectura hexagonal completa
- ✅ Autenticación JWT funcional
- ✅ API REST reactiva documentada
- ✅ Base de datos H2 configurada
- ✅ Dockerizado y ejecutable
- ✅ Tests unitarios y de integración
- ✅ Documentación técnica completa
- ✅ Configuración para desarrollo y producción
```

---

**📅 Fecha de Desarrollo**: Septiembre 2025  
**⏰ Tiempo Total**: Varias sesiones de desarrollo  
**🎓 Nivel**: Senior Software Architecture  
**🏆 Objetivo**: Prueba técnica para posición senior