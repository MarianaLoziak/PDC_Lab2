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
import java.util.concurrent.ThreadLocalRandom;

public class OperatorActor extends AbstractActor {

    public int operatorId;
    private ActorRef client;
    private ActorRef queue;

    public OperatorActor(int id, ActorRef queue){
        this.operatorId = id;
        this.queue = queue;
        //TODO create message about free operator
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
        /*Random random = new Random();
        try {
            Thread.sleep(random.nextInt(10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        ActorSystem system = getContext().getSystem();
        long delay = ThreadLocalRandom.current().nextLong(2000, 10000);
        system.scheduler().scheduleOnce(Duration.ofMillis(delay),
                this::hangUpPhone,
                system.dispatcher());

    }

    public void hangUpPhone(){
        client = null;
        new OperatorEndCall(operatorId);

    }
}
