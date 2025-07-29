package com.markDev.backend_biblioteca_springboot.service.googlebooks.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class GoogleBooksResponse {
	private List<VolumeItem> items;
}