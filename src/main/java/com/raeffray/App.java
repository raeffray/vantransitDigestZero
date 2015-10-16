package com.raeffray;

import java.lang.reflect.InvocationTargetException;

import com.raeffray.graph.handler.RouteNodeHandler;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
  
    	RouteNodeHandler handler = new RouteNodeHandler();
    	
    	handler.start();
        
    }
}
