package com.banca.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;

public class AgenteNotificador extends Agent {

    protected void setup() {
        System.out.println("¡Agente Notificador " + getAID().getName() + " está listo!");

        // --- Registro en Páginas Amarillas (DF) ---
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("comunicacion-cliente"); // Tipo de servicio
        sd.setName("JADE-envio-correo");

        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getAID().getName() + " registró el servicio 'comunicacion-cliente'");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // --- Fin del Registro ---

        // Comportamiento para escuchar solicitudes
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        // Recibe: La decisión final (ej: "APROBADO;Linea:5000")
                        String decision = msg.getContent();
                        System.out.println(getAID().getName() + ": Recibí solicitud de notificación: " + decision);

                        // --- Lógica de Negocio (Simulación) ---
                        // Simulamos el envío de un correo
                        System.out.println(getAID().getName() + ": ENVIANDO CORREO A CLIENTE: Su solicitud fue " + decision);
                        String resultado = "CLIENTE_NOTIFICADO";
                        // --- Fin Lógica ---

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(resultado);
                        myAgent.send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println(getAID().getName() + " se des-registró del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}