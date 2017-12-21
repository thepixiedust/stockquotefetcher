package com.myproject.stockquotefetcher;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

public class Json {
    public static final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(Json.class);

    public static ObjectMapper getMapper(){
  	return mapper;
    }
    
    public static String stringify(Object object) {
  	String str = "";
  	try {
  	  str = mapper.writeValueAsString(object);
  	} catch (JsonProcessingException e) {
  	  // TODO Auto-generated catch block
  	  e.printStackTrace();
  	}
  	return str;
    }

    public static <T> T parse(String content, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
  	T obj = mapper.readValue(content, clazz);
  	return obj;
    }

    public static <T> T parseNoException(String content, Class<T> clazz) {
  	T obj = null;

  	try {
  	  obj = mapper.readValue(content, clazz);
  	} catch (IOException e) {
  	  logger.error("Error parsing object " , e);
  	}
  	return obj;
    }
    
    public static HashMap<String,String> parseStringMap(String content) throws JsonParseException, JsonMappingException, IOException {
  	return mapper.readValue(content, mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class));
    }
    
    public static MapType getStringMapType(ObjectMapper m) {
  	return m.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
    }
}
