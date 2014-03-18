package com.ufo.androidphotoviewer.flickr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Wraps result list of FlickrImage objects into single object 
 * along with result set meta-data and the FlickrQueryCriteria
 * that produced this ResultSet. 
 * 
 * Some fields omitted from JSON result: "generator"
 * 
 * @author vlad
 *
 */
public class FlickrResultSet implements Serializable
{
	private static final long serialVersionUID = 1L;	
	
	private FlickrQueryCriteria queryCrtieria;
	private ArrayList<FlickrImage> images;
	private String title;
	private String link;
	private String description;
	private Date modified;
	

	public FlickrResultSet()
	{
		// Empty Constructor
	}
	
	
	
	public FlickrQueryCriteria getQueryCrtieria() {
		return queryCrtieria;
	}
	public void setQueryCrtieria(FlickrQueryCriteria queryCrtieria) {
		this.queryCrtieria = queryCrtieria;
	}
	public ArrayList<FlickrImage> getImages() {
		return images;
	}
	public void setImages(ArrayList<FlickrImage> images) {
		this.images = images;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
}
