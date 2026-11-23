#NOTA: LA EVIDENCIA DE LA EJECUCION DE LAS PETICIONES Y GRAFANA SE ENCUENTRA EN EL ARCHIVO Proyecto Final.docx

# Customer Service — Starter del proyecto final (sin solución)

**Contiene un proyecto Java de un módulo, dependencias Maven, infraestructura de Keycloak/Prometheus/Grafana, y una clase `SecurityConfig` inicial. No contiene implementación de CRUD ni conversión de roles.**

## 1. Requisitos

- Windows + PowerShell, Docker Desktop funcionando.
- Java **21** y Maven (usa `mvn`, este starter no trae Maven Wrapper).
- Puertos locales libres: Java **8083**, Keycloak **8180**, Prometheus **9091**, Grafana **3001**.
- Internet para descargar imágenes y dependencias la primera vez.

## 2. Arranque de infraestructura (PowerShell desde esta carpeta)

```powershell
Copy-Item .env.example .env
# Las claves en .env son de demostración: cámbialas para un entorno compartido.
docker compose config
docker compose up -d
docker compose ps
```

En la primera importación pueden pasar varios segundos antes de que Keycloak habilite el realm.

```powershell
.\scripts\Verificar-Infra.ps1
```

| Servicio | URL local | Credenciales de demostración |
|---|---|---|
| Keycloak administración | http://localhost:8180/admin/ | admin / `.env` |
| Realm de Keycloak | http://localhost:8180/realms/customer-final | `customer-reader` o `customer-operator` |
| Prometheus | http://localhost:9091/targets | sin inicio de sesión, solo localhost |
| Grafana | http://localhost:3001 | admin / `.env` |
| Customer Service (Java) | http://localhost:8083 | JWT requerido para endpoints de negocio |

**Realm:** `customer-final` · **cliente PowerShell:** `customer-cli-lab` · **audience:** `customer-api`.

Usuarios de demostración:
- Reader: `customer-reader` / `Reader#Final2026!` — rol `CUSTOMER_READ`.
- Operator: `customer-operator` / `Operator#Final2026!` — roles `CUSTOMER_READ`, `CUSTOMER_WRITE`.

> Las contraseñas del realm están en texto claro **únicamente para la práctica local**. Nunca reutilizarlas para producción. Direct Access Grants está habilitado solo por razones didácticas; no es el flujo recomendado para clientes reales.

## 3. Arranque de Customer Service (otra terminal PowerShell)

```powershell
mvn clean package
mvn spring-boot:run
```

En el starter inicial, **no existen rutas de negocio**: son tarea del alumno. El health sí existe:

```powershell
Invoke-RestMethod http://localhost:8083/actuator/health
```

Verifica métricas:

```powershell
(Invoke-WebRequest http://localhost:8083/actuator/prometheus).StatusCode
```

En Prometheus: `up{job="customer-service"}` debe dar `1` después de arrancar Java y esperar algunos scrapes. En Grafana ya existe un datasource de Prometheus (UID `prometheus`); los paneles del proyecto final los crea el alumno.

## 4. Componentes suministrados y pendientes

| Componente | Incluido | Pendiente para el alumno |
|---|---|---|
| `pom.xml`, Java 21, Spring Boot 4.1.2 | Sí | No |
| WebFlux, JWT, Validation, Actuator, Micrometer | Dependencias declaradas | Utilizarlas en CRUD y seguridad |
| `CustomerServiceApplication` | Sí | No |
| `SecurityConfig` | Protección JWT genérica + Actuator público | Definir roles/convertidor y política por método |
| Modelo `Customer` / DTO de entrada | No | Sí |
| `CustomerCatalog` en memoria | No | Sí |
| 5 endpoints HTTP | No | Sí |
| Manejo de errores 400/404 | No | Sí |
| Keycloak realm, clientes, roles, usuarios | Sí | No modificar |
| Prometheus/Grafana + datasource | Sí | Validar métricas y elaborar vista básica |

**Paquete Java base:** `com.bancolombia.training.customers`. Subpaquetes: `model`, `catalog`, `web`, `security`.

La guía completa está incluida como `GUIA_PROYECTO_FINAL.md` y se ofrece también por separado.

## 5. Evitar conflictos con Lab 3 / Lab 4

- Keycloak de este proyecto escucha **8180**, no 8080.
- Prometheus usa **9091**, no 9090.
- Grafana usa **3001**, no 3000.
- Customer Service usa **8083**.

Si ya existe otro proceso en uno de estos puertos, detén ese proceso o cambia **coherentemente** la publicación y las URL de configuración.

## 6. Finalizar sin borrar datos

```powershell
docker compose down
```

`down` conserva los volúmenes de este proyecto. **No ejecutes `down -v`** salvo que quieras eliminar realm/importación y los datos locales de Prometheus y Grafana. Cambiar el JSON del realm no actualiza por sí solo un Keycloak que ya había importado el realm.

## 7. Nota de seguridad

Los puertos de la infraestructura se publican solo en `127.0.0.1` y las rutas `health/prometheus` se permiten sin JWT exclusivamente para el laboratorio local. No publiques estas interfaces ni el endpoint Prometheus en una red externa. Los endpoints de negocio deben protegerse por rol como indica la guía.
