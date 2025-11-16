package com.banca.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;

public class AgenteValidadorIdentidad extends Agent {

    protected void setup() {
        System.out.println("¡Agente Validador " + getAID().getName() + " está listo!");

        // --- Registro en Páginas Amarillas (DF) ---
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("validacion-reniec"); // Tipo de servicio
        sd.setName("JADE-validacion-identidad"); // Nombre del servicio

        dfd.addServices(sd); // Añade el servicio a la descripción

        try {
            DFService.register(this, dfd); // Registra el agente en el DF
            System.out.println(getAID().getName() + " registró el servicio 'validacion-reniec'");
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
                        // Recibe: DNI (ej: "12345678")
                        String dni = msg.getContent();
                        System.out.println(getAID().getName() + ": Recibí solicitud para validar DNI: " + dni);

                        // --- Lógica de Negocio (Simulación) ---
                        String resultado = "VALIDADO";
                        // --- Fin Lógica ---

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM); // Es un INFORM (respuesta)
                        reply.setContent(resultado);
                        myAgent.send(reply);

                        System.out.println(getAID().getName() + ": Respondí '" + resultado + "'");
                    }
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        // Des-registro del DF antes de morir
        try {
            DFService.deregister(this);
            System.out.println(getAID().getName() + " se des-registró del DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agente Validador " + getAID().getName() + " terminando.");
    }
}