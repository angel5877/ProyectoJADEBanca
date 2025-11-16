package com.banca.agentes;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;

public class AgenteOficialCuenta extends Agent {

    // Datos de la solicitud que se está procesando
    private String dniCliente = "45678901"; // DNI simulado
    private String estadoDNI;
    private String scoreBuro;

    // Estado del flujo de trabajo (nuestra máquina de estados)
    // 0 = inicio (validar DNI)
    // 1 = DNI validado (consultar buró)
    // 2 = Buró consultado (evaluar riesgo)
    // 3 = Riesgo evaluado (notificar cliente)
    // 4 = Cliente notificado (fin)
    private int step = 0;

    protected void setup() {
        System.out.println("¡Agente Gerente " + getAID().getName() + " iniciando!");

        // Inicia el flujo después de 5 segundos (para dar tiempo a que los especialistas se registren)
        addBehaviour(new WakerBehaviour(this, 5000) {
            protected void onWake() {
                System.out.println(getAID().getName() + ": Iniciando nueva solicitud para DNI: " + dniCliente);
                iniciarPasoValidacion();
            }
        });

        // Comportamiento cíclico para recibir las RESPUESTAS (INFORMs)
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {

                    // Procesar la respuesta según el paso en el que estemos
                    switch (step) {
                        case 1: // Esperando respuesta del Validador
                            estadoDNI = msg.getContent();
                            System.out.println(getAID().getName() + ": Recibí respuesta de Validador: " + estadoDNI);
                            if (estadoDNI.equals("VALIDADO")) {
                                iniciarPasoBuro(); // Siguiente paso
                            } else {
                                System.out.println(getAID().getName() + ": DNI RECHAZADO. Fin del proceso.");
                                // (Aquí podríamos ir al paso de notificación de rechazo)
                            }
                            break;

                        case 2: // Esperando respuesta del Buro
                            scoreBuro = msg.getContent();
                            System.out.println(getAID().getName() + ": Recibí respuesta de Buró: Score " + scoreBuro);
                            iniciarPasoEvaluacion(); // Siguiente paso
                            break;

                        case 3: // Esperando respuesta del Motor de Reglas
                            String decision = msg.getContent();
                            System.out.println(getAID().getName() + ": Recibí decisión final: " + decision);
                            iniciarPasoNotificacion(decision); // Siguiente paso
                            break;

                        case 4: // Esperando respuesta del Notificador
                            System.out.println(getAID().getName() + ": Recibí confirmación: " + msg.getContent());
                            System.out.println("====== PROCESO DE SOLICITUD COMPLETADO ======");
                            myAgent.doDelete(); // El Gerente termina su trabajo
                            break;
                    }

                } else {
                    block();
                }
            }
        });
    }

    // --- Funciones de inicio de cada paso (Búsqueda en DF y envío de REQUEST) ---

    private void iniciarPasoValidacion() {
        step = 1;
        System.out.println(getAID().getName() + ": Buscando servicio 'validacion-reniec'...");
        enviarMensaje("validacion-reniec", dniCliente);
    }

    private void iniciarPasoBuro() {
        step = 2;
        System.out.println(getAID().getName() + ": Buscando servicio 'reporte-crediticio'...");
        enviarMensaje("reporte-crediticio", dniCliente);
    }

    private void iniciarPasoEvaluacion() {
        step = 3;
        System.out.println(getAID().getName() + ": Buscando servicio 'evaluacion-riesgo'...");
        String contenido = estadoDNI + ";" + scoreBuro;
        enviarMensaje("evaluacion-riesgo", contenido);
    }

    private void iniciarPasoNotificacion(String decisionFinal) {
        step = 4;
        System.out.println(getAID().getName() + ": Buscando servicio 'comunicacion-cliente'...");
        enviarMensaje("comunicacion-cliente", decisionFinal);
    }


    // --- Función HELPER para buscar en DF y enviar mensaje ---
    private void enviarMensaje(String tipoServicio, String contenidoMsg) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio);
        template.addServices(sd);

        try {
            // Buscar agentes en el DF que coincidan con la plantilla
            DFAgentDescription[] result = DFService.search(this, template);

            if (result.length > 0) {
                AID[] destinatarios = new AID[result.length];
                for (int i = 0; i < result.length; i++) {
                    destinatarios[i] = result[i].getName(); // Obtiene el AID del agente
                }

                // Preparar el mensaje REQUEST
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(destinatarios[0]); // Enviamos solo al primero que encontramos
                request.setContent(contenidoMsg);

                System.out.println(getAID().getName() + ": Enviando REQUEST a " + destinatarios[0].getName() + "...");
                send(request);

            } else {
                System.out.println(getAID().getName() + ": ERROR. No se encontró ningún agente con el servicio: " + tipoServicio);
                // (Aquí el agente debería manejar el error, reintentar, o cancelar)
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void takeDown() {
        System.out.println("Agente Gerente " + getAID().getName() + " terminando.");
    }
}