package com.myproject.stockquotefetcher;

import com.myproject.stockquotefetcher.actors.Events;
import com.myproject.stockquotefetcher.actors.StandardMessage;
import com.myproject.stockquotefetcher.actors.SupervisorActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class App 
{
    public static final ActorSystem actorSystem = ActorSystem.create("actor-system");
    public static void main( String[] args ) 
    {
	final ActorRef supervisor = actorSystem.actorOf(Props.create(SupervisorActor.class), "supervisor-actor");
	supervisor.tell(new StandardMessage(Events.APPLICATION_INIT), null);	
    }
}
