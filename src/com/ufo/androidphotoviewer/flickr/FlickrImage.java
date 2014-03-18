package com.ufo.androidphotoviewer.flickr;

import java.io.Serializable;
import java.util.Date;

/**
 * Representation of Flickr image data as returned by FlickrAPI,
 * Some fields have been simplified (media field has been simplified
 * to imageURL).
 * 
 * @author vlad
 *
 */
public class FlickrImage implements Serializable
{
	private static final long serialVersionUID = 8548936156861833185L;
	
	private String title;
	private String albumLink;
	private String imageURL;
	private String descriptionHTML;
	private Date publishDate;
	private String author;
	private String authorID;
	private String tags;
	

	public FlickrImage(){
		// Empty constructor
	}
	
	/**
	 * > Remove the "_m" at end of filename and return higher resolution image
	 * > HTTP protocol not HTTPS for quicker retrieval (no SSL handshake)
	 */
	public String getHDImageUrl()
	{
		if (imageURL != null){
			return "http" + imageURL.substring(5, imageURL.length() - 6) + ".jpg";
		} else {
			return null;
		}
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getDescriptionHTML() {
		return descriptionHTML;
	}
	public void setDescriptionHTML(String descriptionHTML) {
		this.descriptionHTML = descriptionHTML;
	}
	public Date getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthorID() {
		return authorID;
	}
	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getAlbumLink() {
		return albumLink;
	}
	public void setAlbumLink(String albumLink) {
		this.albumLink = albumLink;
	}
	
	
}
