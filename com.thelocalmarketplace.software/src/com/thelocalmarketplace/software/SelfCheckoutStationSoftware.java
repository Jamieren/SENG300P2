package com.thelocalmarketplace.software;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.InputMismatchException;
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
import com.tdc.banknote.BanknoteValidator;
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

	private BigDecimal[] banknoteDenominations = new BigDecimal[] {new BigDecimal("5.0"), new BigDecimal("10.0"), new BigDecimal("20.0")};
	private BigDecimal[] coinDenominations = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")};
	private Currency currency = Currency.getInstance("CAD");


//	private int banknoteStorageUnitCapacity = 10;


	BanknoteValidator banknoteValidator = new BanknoteValidator(currency, banknoteDenominations);


	public static void main(String[] args) {

		sessionSimulation = new SelfCheckoutStationSoftware();
		
		scanner = new Scanner(System.in);	
			
		SelfCheckoutStationBronze.resetConfigurationToDefaults();

		

//		SelfCheckoutStationBronze.configureCoinStorageUnitCapacity(10);
//		SelfCheckoutStationBronze.configureCoinTrayCapacity(20);
//		SelfCheckoutStationBronze.configureCoinDispenserCapacity(20);

		selfCheckoutStationBronze = new SelfCheckoutStationBronze();
		selfCheckoutStationBronze.plugIn(PowerGrid.instance());
		selfCheckoutStationBronze.turnOn();
		
		bronzeBaggingArea = new ElectronicScaleBronze();
		PowerGrid.engageUninterruptiblePowerSource();
		bronzeBaggingArea.plugIn(PowerGrid.instance());
		bronzeBaggingArea.turnOn();

		
		database = new TheLocalMarketPlaceDatabase();
		session = Session.getInstance();
		discrepancy =  new WeightDiscrepancy();
		
		int choice = 0;
		
		while (session.isActive() == false) {
			try {
				session.promptToStartSession();
			} catch (InputMismatchException | IOException e) {
				System.out.println("Invalid entry, error occured. Please try again.\n");

//				e.printStackTrace();

			}
		}

		//Ready for more commands from customer
		
		session.printMenu();
		choice = scanner.nextInt();

		boolean receiptPrinted = false;
		while(receiptPrinted == false) {
			if(session != null && discrepancy.getDiscrepancy()) {
				session.weightDiscrepancyMessage();
				int weightChoice = scanner.nextInt();
				while (weightChoice != 4) {
					if (weightChoice == 1) {
						System.out.println("Item has been added/removed from bagging area.");
						discrepancy.setDiscrepancy(false);
						weightChoice = 4;
					}
					else if (weightChoice == 2) {
						System.out.println("No-Bag-Request has been activated.");
						discrepancy.setDiscrepancy(false);
						weightChoice = 4;
					}
					else if (weightChoice == 3) {
						System.out.println("Attendant has approved weight discrepancy.");
						discrepancy.setDiscrepancy(false);
						weightChoice = 4;
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
				receiptPrinted = true;
				System.exit(0);
			}
			else if (choice == 4) { //Pay Via Banknnote
				//PayViaBanknote.payViaBanknote();
				receiptPrinted = true;
				System.exit(0);
			}
			else if (choice == 5) { //Pay Via Debit
				sessionSimulation.payViaDebit();
				receiptPrinted = true;
				System.exit(0);
			}
			
			else if (choice == 6) { //Exit

				System.out.println("Exiting System");
				receiptPrinted = true;
				System.exit(0);
			}
			if(discrepancy.getDiscrepancy() == false) {
				session.printMenu();
				choice = scanner.nextInt();
			}
		}
		scanner.close();
	}
	
	


	public void removeItem(Barcode barcode) {
		// Assumption is customer has already scanned item, then decided they did not want it anymore, so item is already part of session list and bagging area list.
		// Barcode product should exist in database since they scanning into database.
		BarcodedProduct product = database.getBarcodedProductFromDatabase(barcode);
		
		// If customer tries to remove item before having any items.
		if (session.getOrderItem().isEmpty()) {
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
				selfCheckoutStationBronze.baggingArea.removeAnItem(itemToRemove);
				session.removeOrderItem(itemToRemove);
				session.subtractTotalExpectedWeight(product.getExpectedWeight());
				
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
				bronzeBaggingArea.addAnItem(item);
				Mass totalExpectedMass = new Mass(session.getTotalExpectedWeight());

				try {
					System.out.println("Expected Weight: " + totalExpectedMass.inGrams() + "OnBaggingArea: " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams() );
					int diff = totalExpectedMass.inGrams().compareTo(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
					System.out.println(diff);
					if(diff != 0) {
						System.out.println("Test: " + totalExpectedMass.inGrams() + "/" + session.getTotalExpectedWeight() + " : " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
						discrepancy.setDiscrepancy(true);
									//product, bronzeBaggingArea.getCurrentMassOnTheScale().inGrams()
						System.out.println("Weight discrepancy detected");
					}
				} catch (OverloadedDevice e) {
					
				}
				break;
				case "NO":
					// Process bulky item
					handleBulkyItem(product);
					
					// Process the item as if it was being added to bagging area. Weight reduction in handleBulkyItem should make this work without a wDiscrepancy
					BarcodedItem exemptItem = new BarcodedItem(product.getBarcode(), new Mass(product.getExpectedWeight()));
					session.newOrderItem(exemptItem);
					// Reallocate expected weight as if item was added.
					session.addTotalExpectedWeight(product.getExpectedWeight());
					
					// Check for discrepancy.
					Mass expectedMass = new Mass(session.getTotalExpectedWeight());
					try {
						int diff = expectedMass.inGrams().compareTo(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
						if(diff != 0) {
							System.out.println("Test: " + expectedMass + "/" + session.getTotalExpectedWeight() + " : " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
							discrepancy.setDiscrepancy(true);
							System.out.println("Weight discrepancy detected");
						}
					} catch (OverloadedDevice e) {
						// do nothing or else
					}
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
	
	public void payViaDebit() {
		boolean paymentGood = false;
		PayDebitSwipe payment = new PayDebitSwipe();
		payment.payByDebit();
		if(paymentGood) {
			System.out.println("Payment successful! Amount Due: 0");
			//session.printReceipt();
		}else {
			System.out.println("Payment was unsuccessful\n");
			System.out.println("Please try again or choose a different payment method\n");
		}
	}
	
	public void handleBulkyItem(BarcodedProduct toBeExempted) { 
		Double productWeight = toBeExempted.getExpectedWeight();
		
		// 3. [SIMULATE] Signals to the Attendant that a no-bagging request is in progress.
		// 4. Signals to the System that the request is approved.
		System.out.println("Bagging exemption approved.");
		
		// 5. Reduces the expected weight in the bagging area by the expected weight of the item
		if ((session.getTotalExpectedWeight() - productWeight) >= 0) { // if the difference >= 0, remove the weight
			session.addTotalExpectedWeight(-productWeight); 
		} else { // set to 0 if difference < 0.
			session.addTotalExpectedWeight(-session.getTotalExpectedWeight());
		}
		
		System.out.println(toBeExempted.getDescription() + " was not added to bagging area");
	}
	
	
	
	
	
	// Getters For Testing Purposes
	public void initSelfStationBronze() {
		
		this.selfCheckoutStationBronze = new SelfCheckoutStationBronze();
		this.selfCheckoutStationBronze.plugIn(PowerGrid.instance());
		this.selfCheckoutStationBronze.turnOn();
	}
//	public void initDatabase() {
//		this.database = TheLocalMarketPlaceDatabase.getInstance();
//	}
	public void initSession() {
		this.session = Session.getInstance();
	}
	public SelfCheckoutStationBronze getSelfStationBronze() {
		return this.selfCheckoutStationBronze;
	}
	public TheLocalMarketPlaceDatabase getDatabase() {
		return this.database;
	}
	public Session getSession() {
		return this.session;
	}

	public void setDataBase(TheLocalMarketPlaceDatabase database) {
		this.database = database;
	}

}