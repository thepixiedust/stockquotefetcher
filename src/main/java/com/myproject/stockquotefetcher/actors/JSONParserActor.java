package com.myproject.stockquotefetcher.actors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import akka.actor.UntypedActor;

public class JSONParserActor extends UntypedActor{

    private static final Logger logger = LoggerFactory.getLogger(JSONParserActor.class);
    @Override
    public void onReceive(Object message) throws Exception {
	handleState((StandardMessage) message);
	
    }

    private void handleState(StandardMessage message) {
	if (message.getName().equals(Events.START_PARSING_QUOTE)) {
		  parseQuote(message) ;
		}
    }

    private void parseQuote(StandardMessage message) {
	Map <String, Object>params = message.getParams() ;
	String scripName = (String) params.get("scripName");
	String quote = (String) params.get("quote");
	Map <String, Object> newParams = new HashMap <String, Object>() ;
	try {
	    JsonFactory factory = new JsonFactory();
	    JsonParser  parser  = factory.createParser(quote);
	    while(!parser.isClosed()){
		JsonToken jsonToken = parser.nextToken();
		if(JsonToken.FIELD_NAME.equals(jsonToken)){
		    String item = parser.getCurrentName();
		    //TODO: Replace with switch-case
		    if("LastTradePriceOnly".equals(item)){
			jsonToken = parser.nextToken();
			String LastTradePriceOnly = parser.getValueAsString();
			if(LastTradePriceOnly == null)
			    newParams.put("LastTradePriceOnly", "-1");
			else
			    newParams.put("LastTradePriceOnly", LastTradePriceOnly);			 
		    } else if("OneyrTargetPrice".equals(item)){
			jsonToken = parser.nextToken();
			String OneyrTargetPrice = parser.getValueAsString();
			if(OneyrTargetPrice == null)
			    newParams.put("OneyrTargetPrice", "-1");
			else
			    newParams.put("OneyrTargetPrice", OneyrTargetPrice);
		    } else if("YearHigh".equals(item)){
			jsonToken = parser.nextToken();
			String YearHigh = parser.getValueAsString();
			if(YearHigh == null)
			    newParams.put("YearHigh", "-1");
			else 
			    newParams.put("YearHigh", YearHigh);
		    } else if("YearLow".equals(item)){
			jsonToken = parser.nextToken();
			String YearLow = parser.getValueAsString();
			if(YearLow == null)
			    newParams.put("YearLow","-1");
			else 
			    newParams.put("YearLow", YearLow);
		    } 		    
		}		    
	    }	    
	    newParams.put("scripName", scripName);
	    sender().tell(new StandardMessage(Events.WRITE_TO_CSV, newParams), getSelf());
	} catch (IOException e) {
	  logger.error("JSON Parsing error for quote data. Error: ", e );
	}	
    }
    
}
