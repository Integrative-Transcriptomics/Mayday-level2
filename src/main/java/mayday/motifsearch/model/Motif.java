package mayday.motifsearch.model;

import java.awt.Color;

import mayday.core.structures.linalg.matrix.DoubleMatrix;

public class Motif {
    private String ID;
    private DoubleMatrix PSWM;
    private double significanceValue;
    private String name;
    private Color color = Color.LIGHT_GRAY;//LIGHT_GRAY is standard color

    public Motif(String id, DoubleMatrix bindingMatrix) {
	super();
	this.PSWM = bindingMatrix;
	ID = id;
	/*if no significance is set, the motif gets 0.5 as significance*/
	this.significanceValue = 0.5;
    }

    public double getSignificanceValue() {
	return significanceValue;
    }


    public void setSignificanceValue(double significanceValue) {
	this.significanceValue = significanceValue;
    }


    public String getName() {
	return name;
    }


    public Color getColor() {
	return color;
    }


    public void setColor(Color color) {
	this.color = color;
    }

    public void setName(String name) {
	this.name = name;
    }


    public DoubleMatrix getPSWM() {
	return PSWM;
    }


    public String getID() {
	return ID;
    }

    public int getLength(){
	return this.PSWM.ncol();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((ID == null) ? 0 : ID.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final Motif other = (Motif) obj;
	if (ID == null) {
	    if (other.ID != null)
		return false;
	}
	else if (!ID.equals(other.ID))
	    return false;
	return true;
    }

    @Override
    public String toString(){
	return "name: " + this.getName() + " "
	+ "ID: " + this.getID()+ " "
	+ "length: " + this.getLength()+ " " 
	+ "significance value: " + this.getSignificanceValue();
    }


}
