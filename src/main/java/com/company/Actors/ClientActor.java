package com.company.Actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.company.Messages.NewCaller;

public class ClientActor extends AbstractActor {
    public int clientId;
    private ActorRef operator;
    private ActorRef queue;

    public ClientActor(int clientId, ActorRef queue){
        this.clientId = clientId;
        this.queue = queue;
        //TODO connect to queue
    }

    @Override
    public Receive createReceive() {
        return null;
    }

    public void makeCall(){
        queue.tell(new NewCaller(clientId), getSelf());
    }
}
