package com.example.pneumIA.models;

public class TableModel {
	private String pathologie;
	private String observation;
	private double pourcentage;
	
	public TableModel() {
		super();
	}

	public TableModel(String pathologie, String observation, double pourcentage) {
		super();
		this.pathologie = pathologie;
		this.observation = observation;
		this.pourcentage = pourcentage;
	}

	public String getPathologie() {
		return pathologie;
	}

	public void setPathologie(String pathologie) {
		this.pathologie = pathologie;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public double getPourcentage() {
		return pourcentage;
	}

	public void setPourcentage(double pourcentage) {
		this.pourcentage = pourcentage;
	}
	
	
}
