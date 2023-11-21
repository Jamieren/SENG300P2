package com.thelocalmarketplace.software;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

public class Bag extends Item {
	
	Mass mass = new Mass(0);

	protected Bag(Mass mass) {
		super(mass);
	}
	
	public void setMass(Mass mass) {
		if (mass == null) {throw new NullPointerSimulationException("The bag must have a mass."); }
		else if (mass.compareTo(Mass.ZERO)<0) {throw new InvalidArgumentSimulationException("Weight of the bag must be greater than zero."); }
		else {this.mass = mass; }
	}
}
