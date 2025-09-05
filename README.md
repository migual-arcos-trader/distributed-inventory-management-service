# Distributed Inventory Management Service

## Descripción

Sistema optimizado para la gestión de inventario distribuido en una cadena de tiendas minoristas. Este servicio aborda
problemas de consistencia, latencia y disponibilidad mediante una arquitectura moderna basada en principios de diseño
sólidos.

## Arquitectura

### Diseño Técnico

El sistema sigue una **Arquitectura Hexagonal** con los siguientes componentes principales:

- **Capa de Dominio**: Contiene la lógica de negocio central y entidades del inventario
- **Capa de Aplicación**: Coordina los casos de uso y define puertos
- **Capa de Infraestructura**: Implementa adaptadores para persistencia, APIs web y configuraciones

### Decisiones Arquitectónicas Clave

1. **Arquitectura Hexagonal**: Aislamiento del dominio de negocio para mayor mantenibilidad y testabilidad
2. **Doble Stack Web**: Soporte para Web MVC y WebFlux para diferentes escenarios de concurrencia
3. **Eventual Consistency**: Priorización de disponibilidad sobre consistencia fuerte para operaciones de inventario
4. **Retry Patterns**: Mecanismos de reintento con backoff exponencial para operaciones distribuidas
5. **Observabilidad**: Integración con Spring Boot Actuator y métricas personalizadas

## Stack Tecnológico

- **Java 17**: LTS con features modernas
- **Spring Boot 3.5.5**: Framework principal
- **Spring Data JPA**: Persistencia y repositorios
- **H2 Database**: Base de datos en memoria para prototipo
- **WebFlux**: Programación reactiva para alta concurrencia
- **Lombok**: Reducción de boilerplate code
- **Springdoc OpenAPI**: Documentación automática de APIs
- **Spring Boot Actuator**: Observabilidad y monitoreo

## Principios Aplicados

- ✅ **SOLID Principles**: Separación de responsabilidades, inversión de dependencias
- ✅ **Clean Code**: Código legible y mantenible
- ✅ **Hexagonal Architecture**: Aislamiento del dominio
- ✅ **Reactive Programming**: Manejo eficiente de concurrencia
- ✅ **Unit Testing**: Cobertura de pruebas unitarias
- ✅ **Integration Testing**: Pruebas de integración
- ✅ **Design Patterns**: Factory, Strategy, Adapter, etc.
- ✅ **Security**: Validaciones y sanitización de datos
- ✅ **Retry/Backoff**: Patrones de resiliencia
- ✅ **Modularity**: Estructura modular y desacoplada

## API Endpoints Principales

### Gestión de Inventario

- `GET /api/v1/inventory/{productId}` - Consultar stock disponible
- `POST /api/v1/inventory/update` - Actualizar stock (compra/venta/reposición)
- `GET /api/v1/inventory/store/{storeId}` - Consultar inventario por tienda

### Monitoreo y Salud

- `GET /actuator/health` - Salud del sistema
- `GET /actuator/metrics` - Métricas de la aplicación
- `GET /v3/api-docs` - Documentación OpenAPI
- `GET /swagger-ui.html` - UI interactiva de APIs

## Configuración

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+ o superior
- IDE con soporte para Lombok

### Instalación

1. Clonar el repositorio
2. Ejecutar `mvn clean install`
3. La aplicación estará disponible en `http://localhost:8080`

### Configuración de Base de Datos

La aplicación utiliza H2 Database en memoria con:

- URL: `jdbc:h2:mem:inventorydb`
- Console: `http://localhost:8080/h2-console`
- Credenciales: (sa/sin password)

## Estructura del Proyecto

```        
distributed-inventory-management-service/
├── src/main/java/com/meli/distributed_inventory_management_service/
│   ├── application/                                       # Capa de aplicación (Casos de uso)
│   │   ├── dto/                                           # Data Transfer Objects
│   │   ├── port/                                          # Puertos (interfaces hacia el exterior)
│   │   └── service/                                       # Servicios de aplicación
│   ├── domain/                                            # Capa de dominio (núcleo del negocio)
│   │   ├── exception/                                     # Excepciones de dominio
│   │   │   ├── ConcurrentUpdateException.java
│   │   │   ├── InsufficientStockException.java
│   │   │   ├── InventoryException.java
│   │   │   └── StockValidationException.java
│   │   ├── model/                                         # Entidades de dominio
│   │   │   ├── EventStatus.java
│   │   │   ├── InventoryItem.java
│   │   │   ├── InventoryUpdateEvent.java
│   │   │   ├── Product.java
│   │   │   ├── Store.java
│   │   │   └── UpdateType.java
│   │   ├── repository/                                    # Interfaces de repositorio
│   │   │   ├── InventoryEventRepository.java
│   │   │   └── InventoryRepository.java
│   │   └── service/                                       # Servicios de dominio
│   │       └── InventoryService.java
│   ├── infrastructure/                                    # Capa de infraestructura (adaptadores)
│   │   ├── config/                                        # Configuraciones
│   │   │   ├── security/                                  # Seguridad
│   │   │   │   ├── SecurityConfig.java                    
│   │   │   │   ├── JwtUtil.java                          
│   │   │   │   └── JwtAuthConverter.java                 
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── R2dbcConfig.java
│   │   │   └── RetryConfig.java
│   │   ├── persistence/                                   # Adaptadores de persistencia
│   │   │   ├── entity/                                    # Entidades de persistencia
│   │   │   │   └── InventoryEntity.java
│   │   │   ├── mapper/                                    # Mappers
│   │   │   │   └── InventoryMapper.java
│   │   │   └── repository/                                # Implementaciones de repositorios
│   │   │       ├── ReactiveInventoryJpaRepository.java
│   │   │       └── SpringDataInventoryRepository.java     
│   │   └── web/                                           # Adaptadores web
│   │       ├── controller/                                # Controllers
│   │       │   ├── AuthController.java                    
│   │       │   └── InventoryController.java               
│   │       ├── dto/                                       # DTOs de API
│   │       │   ├── AuthRequestDTO.java                    
│   │       │   ├── AuthResponseDTO.java                   
│   │       │   └── InventoryRequestDTO.java               
│   │       ├── mapper/                                    # Mappers API
│   │       └── exception/                                 # Manejo de excepciones HTTP
│   └── DistributedInventoryManagementServiceApplication.java
├── src/main/resources/
│   ├── application.properties                             # Configuración principal
│   ├── schema.sql                                         # Esquema de base de datos
│   └── data.sql                                           # Datos iniciales
└── src/test/java/                                         # Pruebas
    ├── domain/
    │   ├── model/
    │   │   ├── InventoryItemMother.java
    │   │   └── InventoryItemTest.java
    │   └── service/
    │       └── InventoryServiceTest.java
    └── infrastructure/
        ├── config/
        │   ├── database/
        │   │   ├── TestContainersConfig.java
        │   │   ├── TestDatabaseConfig.java
        │   │   └── TestMapperConfig.java
        │   └── security/
        │       ├── AuthObjectMother.java
        │       ├── JwtAuthConverterTest.java
        │       ├── JwtObjectMother.java
        │       ├── JwtUtilTest.java
        │       ├── SecurityConfigIntegrationTest.java
        │       └── SecurityTestConstants.java
        ├── persistence/
        │   ├── entity/
        │   │   └── InventoryEntityMother.java
        │   ├── mapper/
        │   │   ├── EntityTestFactory.java
        │   │   ├── InventoryMapperTest.java
        │   │   └── MapperTestConstants.java
        │   └── repository/
        │       ├── IntegrationTestsConstants.java
        │       ├── SpringDataInventoryRepositoryIntegrationTest.java
        │       ├── SpringDataInventoryRepositoryNativeIntegrationTest.java
        │       └── TestDataFactory.java
        └── web/
            └── controller/
                ├── AuthControllerIntegrationTest.java
                └── AuthObjectMother.java
```

## Decisiones de Diseño

### Consistencia vs Disponibilidad

Para este escenario distribuido, se prioriza la **disponibilidad sobre consistencia fuerte**, implementando:

- **Eventual Consistency**: Las tiendas se sincronizan periódicamente
- **Conflict Resolution**: Mecanismos de resolución de conflictos en actualizaciones concurrentes
- **Retry Mechanisms**: Reintentos automáticos para operaciones fallidas

### Manejo de Concurrencia

- **Optimistic Locking**: Para prevenir race conditions en updates
- **Transactional Boundaries**: Delimitación clara de transacciones
- **Reactive Streams**: Para manejo eficiente de peticiones concurrentes
