package com.thelocalmarketplace.software;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.CardReaderGold;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerBronze;
import com.jjjwelectronics.scanner.BarcodeScannerSilver;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

import powerutility.PowerGrid;

/*
 * SessionSimulation class contains the main method
 * Controls all the software and runs the simulation of a SelfCheckoutStation Session
 * */

public class SelfCheckoutStationSoftware {

	private static SelfCheckoutStationBronze selfCheckoutStationBronze;

	private static SelfCheckoutStationSilver selfCheckoutStationSilver;

	private static SelfCheckoutStationGold selfCheckoutStationGold;
	
	private static SelfCheckoutStationSoftware sessionSimulation;

	private static TheLocalMarketPlaceDatabase database;
	
	private static Session session;

	private static Scanner scanner;
	
	private static ElectronicScaleBronze bronzeBaggingArea;
	
	private static ElectronicScaleBronze bronzeScanningArea;

	private static ElectronicScaleSilver silverScale;
	
	private static ElectronicScaleGold goldScale;
	
	private static BarcodeScannerBronze bronzeMainScanner;
	
	private static BarcodeScannerSilver silverMainScanner;

	private static BarcodeScannerBronze bronzeHandheldScanner;
	
	private static CardReaderGold goldCardReader;

	private static ReceiptPrinterBronze bronzePrinter;
	
	private static WeightDiscrepancy discrepancy;





	public static void main(String[] args) {

		sessionSimulation = new SelfCheckoutStationSoftware();
		
		scanner = new Scanner(System.in);	
		
		SelfCheckoutStationBronze.configureCoinDenominations(new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")});

		selfCheckoutStationBronze = new SelfCheckoutStationBronze();
		selfCheckoutStationBronze.plugIn(PowerGrid.instance());
		selfCheckoutStationBronze.turnOn();
		
		database = TheLocalMarketPlaceDatabase.getInstance();

		session.promptEnterToContinue();

		//Ready for more commands from customer
		
		session.printMenu();
		int choice = scanner.nextInt();

		boolean receieptPrinted = false;
		while(receieptPrinted == false) {
			if(session != null && discrepancy.hasWeightDiscrepancy()) {
				session.weightDiscrepancyMessage();
				int weightChoice = scanner.nextInt();
				while (weightChoice != 4) {
					if (weightChoice == 1) {
						System.out.println("Item has been added/removed from bagging area.");
						discrepancy.setNoWeightDiscrepancy();
						if (discrepancy.hasWeightDiscrepancy()==false) {
							weightChoice = 4;
						}
					}
					else if (weightChoice == 2) {
						System.out.println("No-Bag-Request has been activated.");
						discrepancy.setNoWeightDiscrepancy();
						if (discrepancy.hasWeightDiscrepancy()==false) {
							weightChoice = 4;
						}
					}
					else if (weightChoice == 3) {
						System.out.println("Attendant has approved weight discrepancy.");
						discrepancy.setNoWeightDiscrepancy();
						if (discrepancy.hasWeightDiscrepancy()==false) {
							weightChoice = 4;
						}
					}
					else {
						System.out.print("\n============================\n"
								 + "Invalid selection, please try again.\n");
						session.weightDiscrepancyMessage();
						weightChoice = scanner.nextInt();
					}
				}
				session.printMenu();
				choice = scanner.nextInt();
			} 
			if (choice == 1) { //Activate Session
				session.activate();
				
			}
			else if (choice == 2) { //Add Item
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
				
			}
			else if (choice == 3) { //Pay Via Coin
				sessionSimulation.payViaCoin();
//				break;
			}
			else if (choice == 4) { //Exit
				System.out.println("Exiting System");
				receieptPrinted = true;
				System.exit(0);
			}
			if(!discrepancy.hasWeightDiscrepancy()) {
				session.printMenu();
				choice = scanner.nextInt();
			}
		}
		scanner.close();
	}
	
	
	public void removeItem(Barcode barcode) {
		// Assumption is customer has already scanned item, then decided they did not want it anymore, so item is already part of session list and bagging area list.
		// If customer tries to remove item before having any items.
		BarcodedProduct product = database.getBarcodedProductFromDatabase(barcode);
		if (session.getOrderItem() == null) {
			System.out.println("No order has been scanned! Can't remove something that is not there.");
		}
		//Items have been previously added
		else {
			// Find item in list
			BarcodedItem itemToRemove = session.findItem(barcode);
			//Item doesn't exist in user current session
			if(itemToRemove == null) {
				System.out.println("The item does not exist in your order");
			}
			// Item exist, remove from session list and bagging obj list
			else {
				selfCheckoutStation.baggingArea.removeAnItem(itemToRemove);
				session.removeOrderItem(itemToRemove);
				session.subtractTotalExpectedWeight(product.getExpectedWeight());
				// Need to adjust discrepancy check, how does this code implement it?
			}
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
				selfCheckoutStationBronze.baggingArea.addAnItem(item);
				session.newOrderItem(item);
				System.out.println(product.getDescription() + " was added to bagging area");
				
				session.addTotalExpectedWeight(product.getExpectedWeight());
				session.addAmountDue(product.getPrice());
				Mass totalExpectedMass = new Mass(session.getTotalExpectedWeight());

				try {
					int diff = totalExpectedMass.inGrams().compareTo(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
					if(diff != 0) {
						System.out.println("Test: " + totalExpectedMass + "/" + session.getTotalExpectedWeight() + " : " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
						discrepancy.setWeightDiscrepancy(true);
									//product, bronzeBaggingArea.getCurrentMassOnTheScale().inGrams()
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
			ArrayList<BigDecimal> denoms = (ArrayList<BigDecimal>) selfCheckoutStationBronze.coinDenominations;
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
}