package com.company.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.company.Messages.*;

import java.util.LinkedList;
import java.util.Queue;

public class QueueActor extends AbstractActor {

    private Queue<ActorRef> queue ;
    private int name;
    public QueueActor(int name){
        queue = new LinkedList<>();
        this.name = name;
    }

    public static Props props(int name){
        return Props.create(QueueActor.class, name);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NewCaller.class,this::putClientInQueue)
                .match(FreeOperator.class, this::connectClientAndOperaator)
                .build();
    }

    public void putClientInQueue(NewCaller message){
        //System.out.println("Client added" + getSender().toString() + " to queue");
        queue.add(getSender());
        getSender().tell(new ClientInQueue(),getSelf());
    }

    public void connectClientAndOperaator(FreeOperator message){
        /*ActorRef client = queue.peek();//or poll
        ServeCLient sc = new ServeCLient(getSender(), client)*/
        if(queue.isEmpty()){
            getSender().tell(new QueueIsEmpty(),getSelf());


        } else {
            ActorRef client = queue.peek();
            queue.remove(client);
            //System.out.println("Connect operator" + message.operatorId + " and client" + client.toString());
            ServeCLient sc = new ServeCLient(getSender(), client);
            client.tell(sc,getSelf());
            getSender().tell(sc,getSelf());
        }
    }

}
