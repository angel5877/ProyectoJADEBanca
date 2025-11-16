package com.banca.agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.domain.DFService;

public class AgenteMotorReglas extends Agent {

    protected void setup() {
        System.out.println("¡Agente Motor de Reglas " + getAID().getName() + " está listo!");

        // --- Registro en Páginas Amarillas (DF) ---
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("evaluacion-riesgo"); // Tipo de servicio
        sd.setName("JADE-motor-decision");

        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println(getAID().getName() + " registró el servicio 'evaluacion-riesgo'");
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
                        // Recibe: "ESTADO_DNI;SCORE" (ej: "VALIDADO;650")
                        String contenido = msg.getContent();
                        System.out.println(getAID().getName() + ": Recibí solicitud de evaluación: " + contenido);

                        // --- Lógica de Negocio (Simulación) ---
                        String[] datos = contenido.split(";");
                        String estadoDNI = datos[0];
                        int score = Integer.parseInt(datos[1]);

                        String decision;
                        if (estadoDNI.equals("VALIDADO") && score > 600) {
                            decision = "APROBADO;Linea:5000";
                        } else {
                            decision = "RECHAZADO";
                        }
                        // --- Fin Lógica ---

                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(decision);
                        myAgent.send(reply);

                        System.out.println(getAID().getName() + ": Respondí con decisión '" + decision + "'");
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