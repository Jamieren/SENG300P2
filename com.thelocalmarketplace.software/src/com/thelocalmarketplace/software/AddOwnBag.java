package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.Scanner;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;

import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

public class AddOwnBag {
	
	private boolean addedBag = false;
	private final int WEIGHT_LIMIT = 1000;
	private WeightDiscrepancy discrepancy = new WeightDiscrepancy();
	private SelfCheckoutStationBronze bronzeStation;
	private Scanner scanner;
	private Session session;
	private ElectronicScaleBronze bronzeBaggingArea;
	private Bag bag;
	private Mass weight;
	private final String YES = "YES";
	private final String NO = "NO";
	
	public void setWeight(Mass weight) {
		if (weight == null) {throw new NullPointerSimulationException("The bag must have a mass."); }
		else if (weight.compareTo(Mass.ZERO)<0) {throw new InvalidArgumentSimulationException("Weight of the bag must be greater than zero."); }
		else if (weight.compareTo(new Mass(WEIGHT_LIMIT)) > WEIGHT_LIMIT) {discrepancy.setDiscrepancy(true); }
		else {this.weight = weight; }
	}
	
	public void setAddedBag(double weight) {
		if (weight > 0.0) {addedBag = true; }
	}

	public Mass getWeight() {
		return weight;
	}
	
	public boolean getAddedBag() {
		return addedBag;
	}

	public void addOwnBag() throws OverloadedDevice {
		
		SelfCheckoutStationBronze.resetConfigurationToDefaults();
		
//		scanner = new Scanner(System.in);
		PowerGrid.engageUninterruptiblePowerSource();

		bronzeStation = new SelfCheckoutStationBronze();
		bronzeBaggingArea = new ElectronicScaleBronze();
		bronzeBaggingArea.plugIn(PowerGrid.instance());
		bronzeBaggingArea.turnOn();
		
		session = new Session();
		bag = new Bag(weight);
		double bagWeight = weight.inMicrograms().doubleValue();
		double difference = 0;
		Mass totalExpectedMass = new Mass(0.0);
		
//		String addBags = scanner.nextLine().toUpperCase();
//		if (addBags.equals(YES)) {
		//4. Updates the expected weight from the bagging area.
		if (addedBag == false) {
			bronzeBaggingArea.addAnItem(bag);
			addedBag = true;
			System.out.println("Your bag was added to the bagging area. No discrepancy detected.");
			session.addTotalExpectedWeight(bagWeight);
			totalExpectedMass = new Mass(session.getTotalExpectedWeight());
			System.out.println("Expected Weight: " + totalExpectedMass.inGrams() + "OnBaggingArea: " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams() );
			difference = totalExpectedMass.inGrams().compareTo(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
			System.out.println(difference);
			}
		if (difference != 0) {
				System.out.println("Test: " + totalExpectedMass.inGrams() + "/" + session.getTotalExpectedWeight() + " : " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
				discrepancy.setDiscrepancy(true);
				System.out.println("Weight discrepancy detected");
		}

	}
	public void bagNotDetected() {
		throw new InvalidArgumentSimulationException("Invalid option. Could not detect a bag added to the bagging area.");
	}
}