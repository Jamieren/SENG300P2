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
