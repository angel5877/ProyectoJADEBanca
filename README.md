🤖 Sistema Multi-Agente (JADE) para Aprobación de Tarjetas de Crédito

Este proyecto es una simulación de un Sistema Multi-Agente (MAS) que automatiza el flujo de aprobación de una solicitud de Tarjeta de Crédito (TDC). Utiliza el framework JADE (Java Agent Development Environment) para demostrar cómo agentes autónomos pueden colaborar para resolver un problema de negocio complejo.

📌 Problema Solucionado

El objetivo es modelar un proceso de negocio que requiere múltiples pasos de validación y lógica de decisión. En lugar de un sistema monolítico (una sola aplicación gigante), este enfoque utiliza agentes especializados, promoviendo:

Modularidad: Cada agente tiene una única responsabilidad.

Escalabilidad: Se pueden añadir nuevos agentes (ej. un AgenteDetectorFraude) sin modificar los existentes.

Desacoplamiento: Los agentes se comunican asíncronamente y se descubren en tiempo de ejecución.

🏛️ Arquitectura y Diseño

El sistema implementa una arquitectura jerárquica (Gerente-Especialista). Un AgenteOficialCuenta (Gerente) orquesta el flujo completo, mientras que 4 agentes especialistas ofrecen servicios.

La colaboración se logra mediante dos mecanismos clave de JADE:

Páginas Amarillas (Directory Facilitator - DF): Los especialistas publican sus servicios (ej. reporte-crediticio).

Mensajería ACL (Agent Communication Language): El Gerente busca en el DF el servicio que necesita y le envía un mensaje REQUEST. El especialista procesa la solicitud y responde con un INFORM.

Agentes del Sistema

Agente

Tipo

Rol y Servicio Publicado (en DF)

AgenteOficialCuenta

Gerente

No publica servicios. Orquesta el flujo completo. Es un consumidor de servicios.

AgenteValidadorIdentidad

Especialista

Servicio: validacion-reniec



Simula la validación de un DNI contra un registro nacional.

AgenteAnalistaBuro

Especialista

Servicio: reporte-crediticio



Simula la consulta a un buró de crédito (como Infocorp/SBS) para obtener el score crediticio.

AgenteMotorReglas

Especialista

Servicio: evaluacion-riesgo



Recibe los datos y aplica la lógica de negocio (ej. score > 600) para aprobar o rechazar.

AgenteNotificador

Especialista

Servicio: comunicacion-cliente



Simula el envío de un correo electrónico al cliente con la decisión final.

📈 Diagrama de Flujo (PlantUML)

Este diagrama de secuencia ilustra la interacción completa entre los agentes.

<details>
<summary>Hacer clic para ver el código PlantUML</summary>

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


</details>

🚀 Cómo Ejecutar el Proyecto

Este proyecto requiere una configuración específica del entorno debido a las dependencias de JADE.

1. Prerrequisitos

Java Development Kit (JDK) 11: Obligatorio. El framework JADE 4.6.0 no es compatible con versiones modernas de Java (17+).

IntelliJ IDEA (o cualquier IDE de Java).

Archivos JADE: Descargar JADE (v4.6.0 o similar) desde el sitio oficial.

2. Configuración en IntelliJ IDEA

Clonar este repositorio.

Abrir el proyecto en IntelliJ.

Añadir las bibliotecas de JADE:

Ir a File -> Project Structure... -> Libraries.

Hacer clic en + (Añadir) -> Java.

Navegar hasta la carpeta lib/ de tu descarga de JADE y seleccionar jade.jar y commons-codec.jar.

Asegurarse de que el SDK del Proyecto esté configurado en JDK 11.

3. Crear la Configuración de Ejecución

No puedes ejecutar los agentes directamente. Debes usar la clase jade.Boot.

Ir a Run -> Edit Configurations....

Hacer clic en + (Añadir) y seleccionar Application.

Configurar los campos:

Name: Lanzar Plataforma JADE

JRE: Asegurarse de que esté seleccionado Java 11.

Main class: jade.Boot

Program arguments: (Copiar y pegar esta línea completa)

-gui Gerente:com.banca.agentes.AgenteOficialCuenta;Validador:com.banca.agentes.AgenteValidadorIdentidad;Buro:com.banca.agentes.AgenteAnalistaBuro;Motor:com.banca.agentes.AgenteMotorReglas;Notificador:com.banca.agentes.AgenteNotificador


4. ¡Ejecutar!

Presiona el botón "Run" (▶). Verás dos cosas:

GUI del RMA (JADE): Una ventana que muestra a los 5 agentes (Gerente, Validador, etc.) vivos.

Consola (IntelliJ): Verás el log completo de la ejecución, mostrando cómo los agentes se registran y cómo el Gerente coordina el flujo paso a paso.

📊 Resultado Esperado (Log de Consola)

La consola mostrará el flujo de comunicación, demostrando que el sistema funciona:

... (Agentes se registran en el DF) ...
¡Agente Gerente Gerente@... iniciando!
(Pasan 5 segundos)
Gerente@...: Iniciando nueva solicitud para DNI: 45678901
Gerente@...: Buscando servicio 'validacion-reniec'...
Gerente@...: Enviando REQUEST a Validador@...
Validador@...: Recibí solicitud para validar DNI: 45678901
Validador@...: Respondí 'VALIDADO'
Gerente@...: Recibí respuesta de Validador: VALIDADO
Gerente@...: Buscando servicio 'reporte-crediticio'...
Gerente@...: Enviando REQUEST a Buro@...
Buro@...: Recibí solicitud de buró para DNI: 45678901
Buro@...: Respondí con score '545'
Gerente@...: Recibí respuesta de Buró: Score 545
Gerente@...: Buscando servicio 'evaluacion-riesgo'...
Gerente@...: Enviando REQUEST a Motor@...
Motor@...: Recibí solicitud de evaluación: VALIDADO;545
Motor@...: Respondí con decisión 'RECHAZADO'
Gerente@...: Recibí decisión final: RECHAZADO
Gerente@...: Buscando servicio 'comunicacion-cliente'...
Gerente@...: Enviando REQUEST a Notificador@...
Notificador@...: Recibí solicitud de notificación: RECHAZADO
Notificador@...: ENVIANDO CORREO A CLIENTE: Su solicitud fue RECHAZADO
Gerente@...: Recibí confirmación: CLIENTE_NOTIFICADO
====== PROCESO DE SOLICIDUD COMPLETADO ======
Agente Gerente Gerente@... terminando.


💡 Posibles Mejoras Futuras

Conectar los agentes a APIs reales (RENIEC, Infocorp, un servicio de email).

Añadir un AgenteDetectorFraude como un paso adicional en el flujo.

Crear una interfaz web simple que envíe un mensaje ACL al AgenteOficialCuenta para iniciar el proceso.
