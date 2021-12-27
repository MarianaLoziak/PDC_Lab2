package com.company.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.company.Messages.*;

import java.util.LinkedList;
import java.util.Queue;

public class QueueActor extends AbstractActor {

    private Queue<ActorRef> queue ;
    public QueueActor(){
        queue = new LinkedList<>();
    }

    public static Props props(){
        return Props.create(QueueActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewCaller.class,this::putClientInQueue)
                .match(FreeOperator.class, this::connectClientAndOperaator)
                .build();
    }

    public void putClientInQueue(NewCaller message){
        queue.add(getSender());
        getSender().tell(new ClientInQueue(),getSelf());
    }

    public void connectClientAndOperaator(FreeOperator message){
        if(queue.isEmpty()){
            getSender().tell(new QueueIsEmpty(),getSelf());


        } else {
            ActorRef client = queue.peek();
            queue.remove(client);
            ServeCLient sc = new ServeCLient(getSender(), client);
            client.tell(sc,getSelf());
            getSender().tell(sc,getSelf());
        }
    }

}
