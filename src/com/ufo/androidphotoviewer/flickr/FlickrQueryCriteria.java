package com.ufo.androidphotoviewer.flickr;

import java.io.Serializable;


/**
 * Flickr search criteria object.
 * 
 * Will return a query URL which will be used by the FlickrQueryEngine
 * to query and produce a Query ResutSet. This URL is generated based on 
 * constructed criteria. 
 * 
 * @author vlad
 *
 */
public class FlickrQueryCriteria implements Serializable
{
	private static final long serialVersionUID = -2239058277895497111L;
	
	// For demonstration of class functionality
	public enum Stream {
	    PUBLIC1, PUBLIC2 
	}
	
	// Generated based on other criteria provided such as feed type ext.
	private String requestUrl;
	
	
	public FlickrQueryCriteria(Stream stream)
	{
		if (stream == Stream.PUBLIC1) {
			// This is for demonstration, expendable class in the future to provide more search criteria
			this.requestUrl = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=2";
		} else {
			this.requestUrl = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=2";
		}
	}
	
	
	public String getRequestUrl() {
		return requestUrl;
	}
	
}
