package com.thelocalmarketplace.software.WeightDiscrepancy;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;

/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */

public class WeightDiscrepancy  {
					 
	private boolean discrepancyDetected = false;
	private SelfCheckoutStation station;
	
	// constructor for a given WeightDiscrepancy object
	// @param
	// - stationToBeWatched: the station to be checked for weight discrepancies.
	public WeightDiscrepancy(SelfCheckoutStation stationToBeWatched) {
		station = stationToBeWatched;
	}
	
	// isBlocked will determine whether the scale itself is blocked at a given moment in time.
		// other methods (Add Item, Scan, Pay with Coin) should check if the system is blocked prior
		// to initiating their functions until
		//
		// RETURNS: T/F if there is a present weight discrepancy
		
	
	public boolean isBlocked() {
        return discrepancyDetected; 
    }
	
	/*
	 * checkWeight compares the current mass in the baggingArea to the actual mass. If they are
	 * not equal, discrepancyDetected turns true.
	 * 
	 * This method will be automatically called if an ElectronicScale object announces a weight
	 * change. Alternatively, it can be called upon a WeightDiscrepancy object to be checked otherwise.
	 * 
	 * mass parameter is the expected mass on the scale.
	 */
	
	public void checkWeight(Mass mass) {
		
		//if current mass on scale does NOT equal the expected mass
		try {
			if (!station.baggingArea.getCurrentMassOnTheScale().equals(mass)) {
				System.out.println("WEIGHT DISCREPANCY DETECTED!");
				
				discrepancyDetected = true;
			}
			else {
				discrepancyDetected = false;
			}
		} catch (OverloadedDevice e) {
			//do nothing!
		}
	}
	
	/*
	 * attendantOverride can be called to change the discrepancyDetected status to FALSE, eradicating the
	 * discrepancy until another is detected.
	 */
	public void attendantDiscrepancyOverride() {
		discrepancyDetected = false;
	}
	
}
