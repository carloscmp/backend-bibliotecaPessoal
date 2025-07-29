package com.markDev.backend_biblioteca_springboot.service.googlebooks.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VolumeInfo {
	private String title;
	private List<String> authors;
	private String language;
	private String description;
	private int pageCount;
	private String publishedDate;
	private ImageLinks imageLinks;
}