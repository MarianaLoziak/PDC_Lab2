package com.company;

import akka.actor.ActorSystem;

public class Main {

    public void main(String[] args){
        ActorSystem akkaSystem = ActorSystem.create("my-system");
    }
}
