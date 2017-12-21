package com.myproject.stockquotefetcher.actors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;

public class QuoteActor extends UntypedActor{

    @Override
    public void onReceive(Object message) throws Exception {
	 handleState((StandardMessage) message);	
    }

    private void handleState(StandardMessage message) {
	if (message.getName().equals(Events.START_FETCHING_QUOTE)) {
		  fetchQuote(message) ;
		}
    }

    private void fetchQuote(StandardMessage message) {
	Map <String, Object>params = message.getParams() ;
	String scripName = (String) params.get("scripName");
	String quoteServiceUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"+scripName+"%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
	String quote = callURL(quoteServiceUrl);
	
	Map <String, Object> newParams = new HashMap <String, Object>() ;
	newParams.put("quote", quote);
	newParams.put("scripName", scripName);
	sender().tell(new StandardMessage(Events.PARSE_JSON, newParams), getSelf());
    }
    
    /**
     * Copied from http://crunchify.com/java-url-example-getting-text-from-url/
     * @param myURL
     * @return URL response
     */
    public static String callURL(String myURL) {
	StringBuilder sb = new StringBuilder();
	URLConnection urlConn = null;
	InputStreamReader in = null;
	try {
	    URL url = new URL(myURL);
	    urlConn = url.openConnection();
	    if (urlConn != null)
		urlConn.setReadTimeout(60 * 1000);
	    if (urlConn != null && urlConn.getInputStream() != null) {
		in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
		BufferedReader bufferedReader = new BufferedReader(in);
		if (bufferedReader != null) {
        		int cp;
        		while ((cp = bufferedReader.read()) != -1) {
        		    sb.append((char) cp);
        		}
        		bufferedReader.close();
		}
	    }
	    in.close();
	} catch (Exception e) {
	    throw new RuntimeException("Exception while calling URL:"+ myURL, e);
	} 
	return sb.toString();
    }

    
}
