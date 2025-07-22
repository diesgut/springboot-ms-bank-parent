# Sistema Bancario - Arquitectura de Microservicios

Este proyecto implementa un sistema de gestión bancaria utilizando una arquitectura de microservicios desacoplada y resiliente, construida sobre el ecosistema de Spring Boot y Spring Cloud.

## Arquitectura del Sistema

El sistema está compuesto por varios microservicios y componentes de infraestructura que trabajan en conjunto. Cada uno tiene una responsabilidad única, siguiendo el principio de Responsabilidad Única (SRP) de SOLID a nivel de arquitectura.

| Módulo | Componente | Descripción |
| :--- | :--- | :--- |
| `ms-bank-parent` | Maven Parent | Proyecto padre que gestiona las dependencias y versiones comunes para todos los módulos. |
| **Infraestructura** | | |
| `eureka-server` | Service Discovery | Registra y localiza los microservicios en la red. Permite que los servicios se encuentren dinámicamente. |
| `spring-gateway` | API Gateway | Punto de entrada único para todas las peticiones de los clientes. Enruta el tráfico, aplica filtros de seguridad y maneja el balanceo de carga. |
| `config-server-native`| Config Server | Servidor de configuración centralizado. Externaliza la configuración de todos los microservicios para gestionarla en un solo lugar. |
| `spring-boot-admin` | Monitoring | Herramienta para monitorear y gestionar las aplicaciones Spring Boot en tiempo de ejecución. |
| **Dominio de Negocio** | | |
| `ms-customer` | Microservicio | Gestiona toda la información relacionada con los clientes (creación, consulta, actualización). |
| `ms-bank-account` | Microservicio | Gestiona las cuentas bancarias, balances y transacciones. |
| `ms-keycloak-adapter` | Security Adapter | Módulo o adaptador para integrar la seguridad con Keycloak, gestionando la autenticación y autorización (OAuth2/OIDC). |
| `bank-common`| Common Library | Módulo con clases de utilidad, DTOs o excepciones comunes compartidas entre los microservicios para evitar la duplicación de código (DRY). |

## Tecnologías Utilizadas

* **Lenguaje:** Java 21+
* **Frameworks:** Spring Boot, Spring Cloud
* **Build Tool:** Apache Maven
* **Contenedores:** Docker, Docker Compose
* **Seguridad:** Keycloak

---

## Puesta en Marcha (Getting Started)

Sigue estos pasos para levantar el entorno de desarrollo completo en tu máquina local.

### Prerrequisitos

Asegúrate de tener instalado lo siguiente:
* [JDK 17 o superior](https://www.oracle.com/java/technologies/downloads/) (Adoptium Temurin es una excelente opción).
* [Apache Maven](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/products/docker-desktop/) y [Docker Compose](https://docs.docker.com/compose/install/)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/diesgut/ms-bank-parent.git
cd ms-bank-parent