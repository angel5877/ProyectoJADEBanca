package com.banca.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;

public class AgenteAnalistaBuro extends Agent {

    protected void setup() {
        System.out.println("¡Agente Analista de Buró " + getAID().getName() + " está listo!");

        // --- Registro en Páginas Amarillas (DF) ---
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("reporte-crediticio"); // Tipo de servicio
        sd.setName("JADE-consulta-buro");

        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getAID().getName() + " registró el servicio 'reporte-crediticio'");
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
                        System.out.println(getAID().getName() + ": Recibí solicitud de buró para DNI: " + dni);

                        // --- Lógica de Negocio (Simulación) ---
                        // Simulamos un score aleatorio entre 400 y 800
                        int score = 400 + (int)(Math.random() * 401);
                        String resultado = String.valueOf(score);
                        // --- Fin Lógica ---

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(resultado);
                        myAgent.send(reply);

                        System.out.println(getAID().getName() + ": Respondí con score '" + resultado + "'");
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