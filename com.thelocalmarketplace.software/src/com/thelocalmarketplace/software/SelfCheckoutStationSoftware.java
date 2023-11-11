package com.thelocalmarketplace.software;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

import powerutility.PowerGrid;

/*
 * SessionSimulation class contains the main method
 * Controls all the software and runs the simulation of a SelfCheckoutStation Session
 * */

public class SelfCheckoutStationSoftware {

	private static SelfCheckoutStationBronze selfCheckoutStation;

	private static SelfCheckoutStationSoftware sessionSimulation;

	private static TheLocalMarketPlaceDatabase database;
	
	private static Session session;

	private static Scanner scanner;
	
	WeightDiscrepancy discrepancy;

	public void promptEnterToContinue(){

		System.out.println("Welcome!");
		System.out.println("Press \"ENTER\" to continue");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printMenu() {
		if(session != null && session.getOrderItem().size() != 0) {
			System.out.println("\n============================\n"
								+ "Order Items:");
			int i = 1;
			for(BarcodedItem bi : session.getOrderItem()) {
				System.out.println("\t   " + i + ") " + bi.getBarcode() + " : " + bi.getMass().inGrams() + " gramms");
				i++;
			}
			System.out.println("Total due: " + session.getAmountDue());
		}
		
		System.out.print("\n============================\n"
				+ "Choose option:\n"
				+ "\t 1. Activate Session\n"
				+ "\t 2. Add Item\n"
				+ "\t 3. Pay via Coin\n"
				+ "\t-1. Exit\n"
				+ "Choice: ");
	}

	public static void main(String[] args) {

		sessionSimulation = new SelfCheckoutStationSoftware();
		
		scanner = new Scanner(System.in);	
		
		SelfCheckoutStationBronze.configureCoinDenominations(new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")});

		selfCheckoutStation = new SelfCheckoutStationBronze();
		selfCheckoutStation.plugIn(PowerGrid.instance());
		selfCheckoutStation.turnOn();
		
		database = TheLocalMarketPlaceDatabase.getInstance();


		sessionSimulation.promptEnterToContinue();

		//Ready for more commands from customer
		
		sessionSimulation.printMenu();
		int choice = scanner.nextInt();

		boolean loop = true;
		while(loop) {
			if(session != null && session.getWeightDiscrepancy() != null && session.hasWeightDiscrepancy()) {
				System.out.print("\n============================\n"
								 + "Weight Discrepancy has been detected\n"
								 + "Product: " + session.getWeightDiscrepancy().getProduct().getDescription() + "caused discrepancy\n"
								 + "\tHas weight " + session.getWeightDiscrepancy().getWeight() + ", was expecting " + session.getTotalExpectedWeight() + "\n\n"
								 + "\t 1. Add/Remove item\n"
								 + "\t 2. Do-Not-Bag Request\n"
								 + "\t 3. Attendant Approval\n"
								 + "\t-1. Exit\n"
								 + "Choice: ");
				int wChoice = scanner.nextInt();
				switch (wChoice) {
				case 1:
					System.out.println("Item has been added/removed from bagging area.");
					session.setNoWeightDiscrepancy();
					sessionSimulation.printMenu();
					choice = scanner.nextInt();
					break;
				case 2:
					System.out.println("No-Bag-Request has been activated.");
					session.setNoWeightDiscrepancy();
					sessionSimulation.printMenu();
					choice = scanner.nextInt();
					break;
				case 3:
					System.out.println("Attendant has approved weight discrepancy.");
					session.setNoWeightDiscrepancy();
					sessionSimulation.printMenu();
					choice = scanner.nextInt();
					break;
				}
			} else {
				switch(choice) {
				case 1: //Activate Session
					sessionSimulation.activateSession();
					break;
				case 2: //Add Item
					System.out.print("Enter barcode to add: ");
					BigDecimal barcodeInput = scanner.nextBigDecimal();
	
					String barcodeInputString = barcodeInput.toString();
	
					int i = 0;
					Numeral[] barcodeNumeral = new Numeral[barcodeInputString.length()];
					for(char c : barcodeInputString.toCharArray()) {
						barcodeNumeral[i] = Numeral.valueOf(Byte.valueOf(String.valueOf(c)));
						i++;
					}
					Barcode barcode = new Barcode(barcodeNumeral);
					sessionSimulation.scanBarcodedProduct(barcode);
					break;
				case 3: //Pay Via Coin
					sessionSimulation.payViaCoin();
					break;
				case -1: //Exit
					System.out.println("Exiting System");
					loop = false;
					System.exit(0);
					break;
				}
				if(!session.hasWeightDiscrepancy()) {
					sessionSimulation.printMenu();
					choice = scanner.nextInt();
				}
			}
		}

		scanner.close();
	}
	
	public void activateSession() {
		session = Session.getInstance();
		if(session.isActive()) {
			System.out.println("A session has already been started, the system cannot start a new session "
							 + "while in an active session.");
		} else {
			session.activate();
			System.out.println("Successfully started a session.");
		}
	}


	public void scanBarcodedProduct(Barcode barcode) {
		
		//3. Determines the characteristics (weight and cost) of the product associated with the barcode.
		BarcodedProduct product = database.getBarcodedProductFromDatabase(barcode);
		if(product != null) {
			System.out.println("The barcode (" + barcode + ") is for " + product.getDescription());
			//5. Signals to the Customer to place the scanned item in the bagging area
			System.out.print("Please place item in the bagging Area (Yes/No): ");
			scanner.nextLine();
			
			String choice = scanner.nextLine().toUpperCase();
			switch(choice) {
			case "YES":
				//4. Updates the expected weight from the bagging area.
				BarcodedItem item = new BarcodedItem(product.getBarcode(), new Mass(product.getExpectedWeight()));
				selfCheckoutStation.baggingArea.addAnItem(item);
				session.newOrderItem(item);
				System.out.println(product.getDescription() + " was added to bagging area");
				
				session.addTotalExpectedWeight(product.getExpectedWeight());
				session.addAmountDue(product.getPrice());
				Mass totalExpectedMass = new Mass(session.getTotalExpectedWeight());

				try {
					int diff = totalExpectedMass.inGrams().compareTo(selfCheckoutStation.baggingArea.getCurrentMassOnTheScale().inGrams());
					if(diff != 0) {
						System.out.println("Test: " + totalExpectedMass + "/" + session.getTotalExpectedWeight() + " : " + selfCheckoutStation.baggingArea.getCurrentMassOnTheScale().inGrams());
						session.setWeightDiscrepancy(product, selfCheckoutStation.baggingArea.getCurrentMassOnTheScale().inGrams());
						System.out.println("Weight discrepancy detected");
					}
				} catch (OverloadedDevice e) {
					
				}
				break;
			case "NO":
				System.out.println(product.getDescription() + " was not added to bagging area");
				break;
			default:
				System.out.println("Invalid option. " + product.getDescription() + " not added to bagging area");
			}
		} else {
			System.out.println("Invalid Barcode");
		}
	}
	
	
	public void payViaCoin() {
		if(session.getAmountDue() != 0) {
			ArrayList<BigDecimal> denoms = (ArrayList<BigDecimal>) selfCheckoutStation.coinDenominations;
			System.out.println("Choose denomination of coin being inserted:");
			for(BigDecimal denom : denoms) {
				System.out.println("\t" + denom);
			}
			System.out.print("Denomination: ");
			BigDecimal denom = scanner.nextBigDecimal();

			while(denom.compareTo(new BigDecimal("-1")) != 0 && session.getAmountDue() > 0) {
				if(denoms.contains(denom)) {
					session.subAmountDue(denom.intValue());
					if(session.getAmountDue() <= 0) {
						System.out.println("Fully paid amount");
						session.getOrderItem().clear();
						return;
					}
					System.out.println("Amount due remaining : " + session.getAmountDue());
				} else {
					System.out.println("Invalid Denomination amount, please try again");
				}
				System.out.println("Choose denomination of coin being inserted:");
				for(BigDecimal denom2 : denoms) {
					System.out.println("\t" + denom2);
				}
				System.out.print("Denomination: ");
				denom = scanner.nextBigDecimal();
			}
		} else {
			System.out.println("No amount due");
		}
	}
	public void handleBulkyItem() { 
		System.out.println("Enter barcode to exempt: ");
		
		//Scan item to be exempted
		BigDecimal bulkyBarcodeInput = scanner.nextBigDecimal();
		String bulkyBarcodeInputString = bulkyBarcodeInput.toString();

		int l = 0;
		Numeral[] bulkyBarcodeNumeral = new Numeral[bulkyBarcodeInputString.length()];
		for(char c : bulkyBarcodeInputString.toCharArray()) {
			bulkyBarcodeNumeral[l] = Numeral.valueOf(Byte.valueOf(String.valueOf(c)));
			l++;
		}
		Barcode bulkyBarcode = new Barcode(bulkyBarcodeNumeral);
		//get the characteristics of the item, primarily weight is needed to equate the expected.
		BarcodedProduct product = database.getBarcodedProductFromDatabase(bulkyBarcode);
		Double productWeight = product.getExpectedWeight();
		
		// 5. Reduces the expected weight in the bagging area by the expected weight of the item
		session.addTotalExpectedWeight(-productWeight);
	}
}