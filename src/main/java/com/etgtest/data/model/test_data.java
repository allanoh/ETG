package com.etgtest.data.model;

public class test_data {
	
	private Long id;
	private String name;
	private String description;
	private String city;
	private int rating;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public test_data(Long id, String name, String description, String city, int rating) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.rating = rating;
	}
	public test_data()
	{
		
	}
	
	@Override
	public String toString() {
		return "Data [id=" + id + ", name=" + name + ", description=" + description + ", date=" + city + ", rating="
				+ rating + "]";
	}
	
	

}
