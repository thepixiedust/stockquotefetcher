package com.myproject.stockquotefetcher.actors;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myproject.stockquotefetcher.CSVUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;

public class JobActor extends UntypedActor{

    private static final Logger logger = LoggerFactory.getLogger(JobActor.class);
    
    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof StandardMessage) {
		  handleState((StandardMessage) message);
	} else 
	    logger.debug("Unknown message received ", message.toString());
    }
    
    private void handleState(StandardMessage msg) {
	Events evt = msg.getName() ;
	//System.out.println("evt =" +evt);
	switch (evt) {
    	case FETCH_QUOTE:
    	  fetchQuote(msg) ;
    	  break ;
    	case PARSE_JSON:
    	  parseQuoteResponse(msg);
    	  break ;
    	case WRITE_TO_CSV: 
    	  writetocsv(msg);
    	  break ;
    	default: 
    	  break; 
	}	
    }
    
    /**
     * Fetches Quote by hitting Yahoo Finance API with the scrip name
     * @param msg
     */
    private void fetchQuote(StandardMessage msg) {
	Map <String, Object>params = msg.getParams() ;
	String scripName = (String) params.get("scripName");
	//logger.debug("scripName = "+scripName);
	ActorRef quoteActor = getContext().getChild("quote-actor");
	if(quoteActor==null){
	    quoteActor = getContext().actorOf(Props.create(QuoteActor.class).withRouter(new RoundRobinPool(20)), "quote-actor");
	}
	quoteActor.tell(new StandardMessage(Events.START_FETCHING_QUOTE, params), getSelf());
    }
    
    /**
     * Reference: https://examples.javacodegeeks.com/core-java/json/jackson/jackson-streaming-api-to-read-and-write-json-example/
     * Parses JSON Response from Yahoo finance API
     * @param msg
     */
    private void parseQuoteResponse(StandardMessage msg) {
	Map <String, Object>params = msg.getParams() ;
	ActorRef jsonparserActor = getContext().getChild("jsonparser-actor");
	if(jsonparserActor==null){
	    jsonparserActor = getContext().actorOf(Props.create(JSONParserActor.class).withRouter(new RoundRobinPool(20)), "jsonparser-actor");
	}
	jsonparserActor.tell(new StandardMessage(Events.START_PARSING_QUOTE, params), getSelf());
    }
    
    /**
     * Writes the retrieved quote to csv file
     * @param msg
     */
    private void writetocsv(StandardMessage message) {
	Map <String, Object>params = message.getParams() ;
	String scripName = (String) params.get("scripName");
	String LastTradePriceOnly = (String) params.get("LastTradePriceOnly");
	String OneyrTargetPrice = (String) params.get("OneyrTargetPrice");
	String YearHigh = (String) params.get("YearHigh");
	String YearLow = (String) params.get("YearLow");
	
	String csvFile = "/home/novix/stockquotes/quotes.csv";
	FileWriter writer = null;
	try {
	    writer = new FileWriter(csvFile, true);
	} catch (IOException e1) {
	    logger.error("Failed to create filewriter: "+e1);
	}
	try {
	    List<String> list = new ArrayList<>();
            list.add(scripName);
            list.add(LastTradePriceOnly);
            list.add(OneyrTargetPrice);
            list.add(YearHigh);
            list.add(YearLow);
            
            CSVUtils.writeLine(writer, list);            
            
	} catch (IOException e) {
	    logger.error("Failed to write to csv file: "+e);
	}
	try {
	    writer.flush();
	    writer.close();
	} catch (IOException e) {
	    logger.error("Failed to flush and close filewriter: "+e);
	}        
    }   
}
