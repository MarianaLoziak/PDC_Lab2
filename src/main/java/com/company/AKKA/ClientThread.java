package com.company.AKKA;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.company.Actors.ClientActor;

import java.util.Random;

public class ClientThread extends Thread{
    private ActorSystem system;
    private ActorRef queue;

    public ClientThread(ActorRef queue, ActorSystem system){
        this.queue = queue;
        this.system = system;
    }

    @Override
    public void run() {
        int i = 0;
        Random r = new Random();
        while (true) {
            try {
                i++;
                Thread.sleep(r.nextInt(3000));
                system.actorOf(ClientActor.props(i, queue));
            } catch (InterruptedException e){}
        }
    }
}
