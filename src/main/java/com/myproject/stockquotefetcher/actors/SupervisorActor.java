package com.myproject.stockquotefetcher.actors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myproject.stockquotefetcher.CSVUtils;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class SupervisorActor extends UntypedActor {

    private static ActorRef jobActor = null ;
    private static final Logger logger = LoggerFactory.getLogger(SupervisorActor.class);

    @Override
    public void onReceive(Object message) throws Exception {
	// TODO Auto-generated method stub
	if (message instanceof StandardMessage) {
	  handleState((StandardMessage)message) ;
	} 
	else {
	  logger.warn("Unknown message received ", message.toString());
	}
    }  
    
    private void handleState(StandardMessage msg) {
	Events evt = msg.getName() ;

	switch (evt) {
	case APPLICATION_INIT:
	  createOutputcsvfile();  
	  readScrips() ;
	  break;
	default: 
	  break; 
	}	
    }
    
    private void createOutputcsvfile() {
	String csvFile = "/home/novix/stockquotes/quotes.csv";
	FileWriter writer = null;
	try {
	    //open file in write mode
	    writer = new FileWriter(csvFile, false);
	    //csv file header
	    CSVUtils.writeLine(writer, Arrays.asList("ScripName", "LastTradePriceOnly", "OneyrTargetPrice", "YearHigh", "YearLow"));
	    writer.flush();
	    writer.close();
	} catch (IOException e) {
	   logger.error("Failed to create output csv file: "+e); 
	}	
    }

    /**
     * CSVUtils: Ref: https://www.mkyong.com/java/how-to-export-data-to-csv-file-java/
     * Reads scrips from the input csv file
     */
    private void readScrips() {
		
	//Read all stocks from the input txt file
	String scripsFile = "/home/novix/stockquotes/Stocks.txt";
	String scripName = "";
	try {    
            BufferedReader br = new BufferedReader(new FileReader(scripsFile));
	    while ((scripName = br.readLine()) != null) {
		//System.out.println("line = "+scripName);
		if(jobActor == null) {
		    jobActor = getContext().actorOf(Props.create(JobActor.class), "job-actor");
		}
		Map <String, Object> params = new HashMap<String, Object>() ;
		params.put("scripName", scripName);
		jobActor.tell(new StandardMessage(Events.FETCH_QUOTE, params), getSelf());
	    }
	    
	} catch (FileNotFoundException e) {
	    logger.error("Input file not found: "+e);
	} catch (IOException e) {
	    logger.error("IO exception while opening input file: "+e);
	}
    }
}
