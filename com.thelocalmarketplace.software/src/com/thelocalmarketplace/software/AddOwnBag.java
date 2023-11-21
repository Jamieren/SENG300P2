package com.thelocalmarketplace.software;

/*SENG 300 Project Iteration 2

@author Akashdeep Grewal 30179657
@author Amira Wishah 30182579
@author Ananya Jain 30196069
@author Danny Ly 30127144
@author Hillary Nguyen 30161137
@author Johnny Tran 30140472 
@author Minori Olguin 30035923
@author Rhett Bramfield 30170520
@author Wyatt Deichert 30174611
@author Zhenhui Ren 30139966
@author Adrian Brisebois 30170764
*/

import com.jjjwelectronics.Mass;

import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

public class AddOwnBag {
	
	private boolean addedBag = false;
	private final int WEIGHT_LIMIT = 1000;
	private WeightDiscrepancy discrepancy = new WeightDiscrepancy();
	private Session session;
	private ElectronicScaleBronze bronzeBaggingArea;
	private Bag bag;
	private Mass weight;

	
	public void setWeight(Mass weight) {
		if (weight == null) {throw new NullPointerSimulationException("The bag must have a mass."); }
		else if (weight.compareTo(Mass.ZERO)<0) {throw new InvalidArgumentSimulationException("Weight of the bag must be greater than zero."); }
		else if (weight.compareTo(new Mass(WEIGHT_LIMIT)) > WEIGHT_LIMIT) {discrepancy.setDiscrepancy(true); }
		else {this.weight = weight; }
	}
	
	public void setAddedBag(double weight) {
		if (weight > 0.0) {addedBag = true; }
		else {addedBag = false; }
	}

	public Mass getWeight() {
		return weight;
	}
	
	public boolean getAddedBag() {
		return addedBag;
	}

}
