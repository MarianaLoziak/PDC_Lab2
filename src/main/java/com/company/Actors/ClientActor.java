package com.company.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.company.Messages.*;

import java.time.Duration;

public class ClientActor extends AbstractActor {
    public int clientId;
    private ActorRef operator;
    private ActorRef queue;

    public ClientActor(int clientId, ActorRef queue){
        this.clientId = clientId;
        this.queue = queue;
        makeCall();
    }

    public static Props props(int clientId, ActorRef queue){
        return Props.create(ClientActor.class,clientId,queue);
    }

    @Override
    public String toString(){
        return  Integer.toString(clientId);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ServeCLient.class, this::handleCall)
                .match(OperatorEndCall.class, this::hangUp)
                .match(ClientInQueue.class, this::waitOnLine)
                .build();
    }

    public void makeCall(){
        System.out.println("Call by client " + clientId);
        queue.tell(new NewCaller(clientId), getSelf());
    }

    public void handleCall(ServeCLient message){
        operator = message.operator;
        System.out.println("Client " + clientId + " speaking");

    }

    public void waitOnLine(ClientInQueue message){
        System.out.println("Client " + clientId + " waiting");
        ActorSystem system = getContext().getSystem();
        system.scheduler().scheduleOnce(
                Duration.ofSeconds(2),
                () -> {
                    if(operator == null)
                        endAndRecall();
                },
                system.getDispatcher()
        );
    }

    public void endAndRecall(){
        queue.tell(new ClientRecall(clientId), getSelf());
        ActorSystem system = getContext().getSystem();
        system.scheduler().scheduleAtFixedRate(
                Duration.ofSeconds(1),
                Duration.ofSeconds(3),
                () -> {
                    System.out.println("Recall by client" + clientId);
                    makeCall();
                },
                system.getDispatcher()
        );
    }

    public void hangUp(OperatorEndCall message){
        System.out.println("Operator hang up - client" + clientId);
        operator = null;
        ActorSystem system = getContext().getSystem();
        system.stop(getSelf());
    }
}
