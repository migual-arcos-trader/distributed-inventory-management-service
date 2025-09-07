# 🚀 Distributed Inventory Management Service - Guía de Ejecución

## 🛠 Ejecución Maven + Docker

### Prerrequisitos
- Java 17
- Maven 3.6+
- Docker 20.10+
- Docker Compose 2.0+

```bash
# Construcción del JAR ejecutable ... ...
mvn clean package
```
```bash
# Construcción y ejecución con Docker Compose ...
docker-compose up --build
```
```bash
# Limpieza completa (containers, volúmenes, imágenes) ...
docker-compose down -v --rmi all
```

## 🛠 Ejecución local con Maven (Desarrollo)

### Prerrequisitos
- Java 17
- Maven 3.6+

### Comandos Maven para construir el JAR
```bash
# Compilar y ejecutar la aplicación localmente
mvn clean package spring-boot:run
```

```bash
# Construcción del JAR ejecutable ...
mvn clean package
```

```bash
# Construir omitiendo tests
mvn clean package -DskipTests
```

```bash
# Ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

```bash
# Solo compilar sin ejecutar
mvn clean compile
```

## 📦 Ejecución con Docker (Recomendado para producción)

### Prerrequisitos
- Docker 20.10+
- Docker Compose 2.0+

### Comandos de ejecución
```bash
# Construcción y ejecución con Docker Compose
docker-compose up --build
```

```bash
# Ejecutar en segundo plano
docker-compose up -d --build
```

```bash
# Ver logs de la aplicación
docker-compose logs -f
```

```bash
# Detener la aplicación
docker-compose down
```

```bash
# Limpiar completamente (containers, volúmenes, imágenes)
docker-compose down -v --rmi all
```

## 🌐 URLs de la aplicación

### Una vez ejecutada de forma local, accede a:
- **🔌 API REST**: http://localhost:8080/api
- **📚 Swagger UI**: http://localhost:8080/swagger-ui
- **📊 H2 Console**: http://localhost:8080/h2-console
- **❤️ Health Check**: http://localhost:8080/actuator/health
- **📝 API Docs**: http://localhost:8080/api-docs

### Configuración H2 Console:
- **JDBC URL**: `jdbc:h2:mem:///inventorydb`
- **User**: `sa`
- **Password**: (dejar vacío)

## 🔐 Autenticación JWT

### Obtener token de acceso
```bash
curl -X POST http://localhost:8080/api/auth/login \\
-H "Content-Type: application/json" \\
-d '{"username":"admin", "password":"password"}'
```

### Usar token en requests
```bash
curl -X GET http://localhost:8080/api/inventory \\
-H "Authorization: Bearer <TU_TOKEN_JWT>"
```

## 🐛 Troubleshooting

### Puerto ocupado
```bash
# Ver procesos usando puerto 8080
lsof -i :8080

# En Windows:
netstat -ano | findstr :8080

# Matar proceso específico
kill -9 <PID>
```

### Problemas de Maven
```bash
# Limpiar cache de Maven
mvn clean

# Forzar descarga de dependencias
mvn clean compile -U

# Verificar estructura del proyecto
mvn validate
```

### Problemas de Docker
```bash
# Ver containers en ejecución
docker ps
```

```bash
# Ver logs de un container específico
docker logs distributed-inventory-management-service
```

```bash
# Ejecutar comandos dentro del container
docker exec -it distributed-inventory-management-service /bin/sh
```

```bash
# Limpiar recursos Docker no utilizados
docker system prune -a
```

## 🔧 Variables de entorno configurables

### Variables para Docker
```bash
SPRING_PROFILES_ACTIVE=docker
SPRING_R2DBC_URL=r2dbc:h2:mem:///inventorydb
SPRING_R2DBC_USERNAME=sa
SPRING_R2DBC_PASSWORD=
JWT_SECRET=mySuperSecretKeyForJWTWithAtLeast256BitsLength
JWT_EXPIRATION=3600000
SERVER_PORT=8080
```

## 📊 Comandos útiles de verificación

```bash
# Verificar que la aplicación está respondiendo
curl http://localhost:8080/actuator/health

# Ver información de la aplicación
curl http://localhost:8080/actuator/info

# Ver métricas
curl http://localhost:8080/actuator/metrics

# Listar endpoints disponibles
curl http://localhost:8080/actuator/mappings
```

## 🎯 Perfiles de Spring Boot disponibles

- **default**: Desarrollo local con H2 en memoria
- **docker**: Ejecución en contenedor Docker
- **test**: Para ejecución de tests

---

**📝 Nota**: Para desarrollo local usa el perfil `default`, para producción en Docker usa el perfil `docker`.