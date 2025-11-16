# 🤖 Sistema Multi-Agente (JADE) para Aprobación de Tarjetas de Crédito

![Java 11](https://img.shields.io/badge/Java-11-blue.svg?logo=java&logoColor=white)
![JADE](https://img.shields.io/badge/Framework-JADE_4.6-green.svg?style=flat)

Este proyecto es una simulación de un Sistema Multi-Agente (MAS) que automatiza el flujo de aprobación de una solicitud de Tarjeta de Crédito (TDC). Utiliza el framework **JADE (Java Agent Development Environment)** para demostrar cómo agentes autónomos pueden colaborar para resolver un problema de negocio complejo.

---

## 📌 Problema Solucionado

El objetivo es modelar un proceso de negocio que requiere múltiples pasos de validación y lógica de decisión. En lugar de un sistema monolítico (una sola aplicación gigante), este enfoque utiliza agentes especializados, promoviendo:

* **Modularidad:** Cada agente tiene una única responsabilidad.
* **Escalabilidad:** Se pueden añadir nuevos agentes (ej. un `AgenteDetectorFraude`) sin modificar los existentes.
* **Desacoplamiento:** Los agentes se comunican asíncronamente y se descubren en tiempo de ejecución.

---

## 🏛️ Arquitectura y Diseño

El sistema implementa una **arquitectura jerárquica (Gerente-Especialista)**. Un `AgenteOficialCuenta` (Gerente) orquesta el flujo completo, mientras que 4 agentes especialistas ofrecen servicios.

La colaboración se logra mediante dos mecanismos clave de JADE:

1.  **Páginas Amarillas (Directory Facilitator - DF):** Los especialistas publican sus servicios (ej. `reporte-crediticio`).
2.  **Mensajería ACL (Agent Communication Language):** El Gerente busca en el DF el servicio que necesita y le envía un mensaje `REQUEST`. El especialista procesa la solicitud y responde con un `INFORM`.

### Agentes del Sistema

| Agente | Tipo | Rol y Servicio Publicado (en DF) |
| :--- | :--- | :--- |
| **`AgenteOficialCuenta`** | **Gerente** | **No publica servicios.** Orquesta el flujo completo. Es un *consumidor* de servicios. |
| `AgenteValidadorIdentidad` | Especialista | **Servicio:** `validacion-reniec`<br/>_Simula la validación de un DNI contra un registro nacional._ |
| `AgenteAnalistaBuro` | Especialista | **Servicio:** `reporte-crediticio`<br/>_Simula la consulta a un buró de crédito (como Infocorp/SBS) para obtener el score crediticio._ |
| `AgenteMotorReglas` | Especialista | **Servicio:** `evaluacion-riesgo`<br/>_Recibe los datos y aplica la lógica de negocio (ej. `score > 600`) para aprobar o rechazar._ |
| `AgenteNotificador` | Especialista | **Servicio:** `comunicacion-cliente`<br/>_Simula el envío de un correo electrónico al cliente con la decisión final._ |

---

## 📈 Diagrama de Flujo (PlantUML)

Este diagrama de secuencia ilustra la interacción completa entre los agentes.

<details>
<summary>Hacer clic para ver el código PlantUML</summary>

```plantuml
@startuml
' --- Título del Diagrama ---
title Flujo de Aprobación de TDC (Sistema Multi-Agente JADE)

' --- Definición de Participantes (Los Agentes) ---
actor "AgenteOficialCuenta\\n(Gerente)" as Gerente
database "Páginas Amarillas\\n(DF)" as DF
participant "AgenteValidador\\n(Especialista)" as Validador
participant "AgenteAnalistaBuro\\n(Especialista)" as Buro
participant "AgenteMotorReglas\\n(Especialista)" as Motor
participant "AgenteNotificador\\n(Especialista)" as Notificador

' === FASE 1: INICIALIZACIÓN (SETUP) ===
group Fase de Inicialización (setup)
Validador -> DF: register("validacion-reniec")
Buro -> DF: register("reporte-crediticio")
Motor -> DF: register("evaluacion-riesgo")
Notificador -> DF: register("comunicacion-cliente")
end

... 1000ms ...

' === FASE 2: FLUJO DE APROBACIÓN (INICIADO POR EL GERENTE) ===
group Flujo de Aprobación (Orquestado por Gerente)

' --- PASO 1: Validación de Identidad ---
Gerente -> DF: search("validacion-reniec")
DF --> Gerente: inform(AID: Validador)
Gerente -> Validador: **REQUEST** (DNI: "4567...")
activate Validador
Validador --> Gerente: **INFORM** ("VALIDADO")
deactivate Validador

' --- PASO 2: Consulta de Buró ---
Gerente -> DF: search("reporte-crediticio")
DF --> Gerente: inform(AID: Buro)
Gerente -> Buro: **REQUEST** (DNI: "4567...")
activate Buro
Buro --> Gerente: **INFORM** (Score: "545")
deactivate Buro

' --- PASO 3: Evaluación de Riesgo ---
Gerente -> DF: search("evaluacion-riesgo")
DF --> Gerente: inform(AID: Motor)
Gerente -> Motor: **REQUEST** (Contenido: "VALIDADO;545")
activate Motor
Motor --> Gerente: **INFORM** ("RECHAZADO")
deactivate Motor

' --- PASO 4: Notificación al Cliente ---
Gerente -> DF: search("comunicacion-cliente")
DF --> Gerente: inform(AID: Notificador)
Gerente -> Notificador: **REQUEST** (Contenido: "RECHAZADO")
activate Notificador
Notificador --> Gerente: **INFORM** ("CLIENTE_NOTIFICADO")
deactivate Notificador

' --- PASO 5: Fin del Proceso ---
Gerente -> Gerente: Proceso completado.\n(doDelete())
end

@enduml
