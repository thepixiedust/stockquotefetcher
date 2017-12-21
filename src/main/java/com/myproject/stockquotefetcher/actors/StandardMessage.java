package com.myproject.stockquotefetcher.actors;

import java.util.Map;

public class StandardMessage {
    private Events name;
    
    private Map<String,Object> params;
    
    public StandardMessage(Events name) {
  	this.name = name;
    }
    
    public StandardMessage(Events name, Map<String,Object> params) {
  	this.name = name;
  	this.params=params;
    }
    
    public Events getName() {
      return name;
    }

    public Map<String,Object> getParams() {
  	return params;
    }
    
    public static StandardMessage getEvent(Events event) {
  	return new StandardMessage(event);
    }
}
