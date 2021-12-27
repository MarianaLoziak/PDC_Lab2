package com.company.Messages;

import akka.actor.ActorRef;


public class ServeCLient {
    public ActorRef operator;
    public ActorRef client;

    public ServeCLient(ActorRef operator, ActorRef client) {
        this.operator = operator;
        this.client = client;
    }
}
