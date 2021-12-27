package com.company.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.company.Messages.FreeOperator;
import com.company.Messages.OperatorEndCall;
import com.company.Messages.QueueIsEmpty;
import com.company.Messages.ServeCLient;

import java.time.Duration;
import java.util.Random;

public class OperatorActor extends AbstractActor {

    public int operatorId;
    private ActorRef client;
    private ActorRef queue;

    public OperatorActor(int id, ActorRef queue){
        this.operatorId = id;
        this.queue = queue;
        QueueIsEmpty message = new QueueIsEmpty();
        waitingForCients(message);
    }

    public static Props props(int operatorId, ActorRef queue){
        return Props.create(OperatorActor.class,operatorId,queue);
    }
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(QueueIsEmpty.class, this::waitingForCients)
                .match(ServeCLient.class, this::pickUpPhone).build();

    }

    public void waitingOnLine(){
        if(client == null){
            queue.tell(new FreeOperator(operatorId), getSelf());
        }
    }

    public void waitingForCients(QueueIsEmpty message){
        if(client == null){
            ActorSystem system = getContext().getSystem();
            system.scheduler().scheduleAtFixedRate(
                    Duration.ofSeconds(1),
                    Duration.ofSeconds(3),
                    () -> {
                        waitingOnLine();
                    },
                    system.getDispatcher()
            );
        }
    }
    public void pickUpPhone(ServeCLient message){
        client = message.client;
        System.out.println("Client " + client.toString() + " speaking with operator " + operatorId );
        ActorSystem system = getContext().getSystem();
        Random r = new Random();
        system.scheduler().scheduleOnce(
                Duration.ofMillis(r.nextInt(10000)),
                this::hangUpPhone,
                system.dispatcher());

    }

    public void hangUpPhone(){
        client.tell(new OperatorEndCall(operatorId),getSelf());
        client = null;

        QueueIsEmpty message = new QueueIsEmpty();
        waitingForCients(message);
    }
}
