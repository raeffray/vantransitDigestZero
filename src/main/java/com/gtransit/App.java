package com.gtransit;

import com.gtransit.graph.handler.TransitNodeTreeCreator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
  
    	TransitNodeTreeCreator creator = new TransitNodeTreeCreator();
    	
    	creator.create();
        
    }
}
