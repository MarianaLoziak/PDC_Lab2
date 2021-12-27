package com.company;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.company.AKKA.ClientThread;
import com.company.Actors.ClientActor;
import com.company.Actors.OperatorActor;
import com.company.Actors.QueueActor;

import java.util.Random;

public class Main {

    public static void main(String[] args){
        ActorSystem akkaSystem = ActorSystem.create("call-center-system");

        ActorRef queue = akkaSystem.actorOf(QueueActor.props(1));

        ActorRef op_fst = akkaSystem.actorOf(OperatorActor.props(1,queue));
        ActorRef op_scd = akkaSystem.actorOf(OperatorActor.props(2,queue));
        ActorRef op_thd = akkaSystem.actorOf(OperatorActor.props(3,queue));

        Thread clients = new Thread(()->
        {
            Random r = new Random();
            for (int j = 0; j < 20; j++) {
                try {
                    Thread.sleep(r.nextInt(3000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                akkaSystem.actorOf(ClientActor.props(j, queue), "client" +j);
            }
        });
        clients.start();
    }
}
