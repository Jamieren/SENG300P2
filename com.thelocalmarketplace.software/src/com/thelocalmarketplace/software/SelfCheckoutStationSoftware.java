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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.card.CardReaderBronze;
import com.jjjwelectronics.card.CardReaderGold;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleGold;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerBronze;
import com.jjjwelectronics.scanner.BarcodeScannerSilver;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinSlot;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;

import powerutility.NoPowerException;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

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
	
	private static CardReaderBronze bronzeCardReader;

	private static ReceiptPrinterBronze bronzePrinter;
	
	private static WeightDiscrepancy discrepancy;
	
	private static CoinSlot coinSlot;
	
	private static Coin insertedCoin;
	
	private static CoinDispenserBronze bronzeDispenser;

	private static CoinDispenserGold goldDispenser;
	
	private final static String YES = "YES";
	
	private final static String NO = "NO";

	private BigDecimal[] banknoteDenominations = new BigDecimal[] {new BigDecimal("5"), new BigDecimal("10"), new BigDecimal("20")};
	private BigDecimal[] coinDenominations = new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")};
	private Currency currency = Currency.getInstance("CAD");

//	private int banknoteStorageUnitCapacity = 10;

	BanknoteValidator banknoteValidator = new BanknoteValidator(currency, banknoteDenominations);


	public static void main(String[] args) {

		sessionSimulation = new SelfCheckoutStationSoftware();
		
		scanner = new Scanner(System.in);	
		
		SelfCheckoutStationSoftware.bronzeHandheldScanner = new BarcodeScannerBronze(); 
		SelfCheckoutStationSoftware.bronzeMainScanner = new BarcodeScannerBronze();
			
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
			}
		}

		//Check if the customer has their own bags
		
		boolean waitingForValidInput = true;
		String addBagChoice = null;
		String addBagToBaggingArea = null;
		AddOwnBag addBag = new AddOwnBag();
		Bag bag;
		Mass bagMass = new Mass(1.1);
		double microGramConversion = 1000000;

		
		while (waitingForValidInput) {
			System.out.println("\nWould you like to use your own bags? Enter Yes or No: \n");
			try {
				addBagChoice = scanner.nextLine().toUpperCase();
				if (!addBagChoice.equals(YES) && !addBagChoice.equals(NO)) {
					System.out.println("Please try again or enter No to cancel.\n");
				}
			}
			catch (InputMismatchException e) {
				System.out.println("Invalid entry, error occured. Please try again or enter No to cancel.\n");
			}
			if (addBagChoice.equals(YES)) { waitingForValidInput = false; }
			else if (addBagChoice.equals(NO)) { waitingForValidInput = false; }
			else { waitingForValidInput = true; }
		}	
		
		waitingForValidInput = true;
		while (waitingForValidInput == true) {
			System.out.println("\nEnter mass of bag in grams: \n");
			try {
				
				bagMass = new Mass(scanner.nextDouble());				
				
				if (bagMass.compareTo(Mass.ZERO)==-1) {
					System.out.println("Bag mass must be positive. Please try again or enter No to cancel.\n");
				}
				else if (bagMass.compareTo(Mass.ZERO)==0) {
					System.out.println("Bag mass cannot be zero. Please try again or enter No to cancel.\n");
				}
				else if (bagMass.compareTo(Mass.ZERO)==1) {
					addBag.setWeight(bagMass);
					waitingForValidInput = false;
					break;
				}
			}
			catch (InputMismatchException e) {
				System.out.println("\nInvalid entry, error occured. Please try again.\n");
			}
		}
		
		// initalize variables to add bags to scale
		bag = new Bag(bagMass);
		addBag.setAddedBag(0);
		double bagWeight = bagMass.inMicrograms().doubleValue()/microGramConversion;
		BigDecimal difference = new BigDecimal(0.0);
		scanner.nextLine();

		while (addBag.getAddedBag() == false) {
			Mass totalExpectedMass = new Mass(0.0);
			System.out.println("Please place your bag in the bagging Area. Enter Yes or No: \n");	
			try {
				addBagToBaggingArea = scanner.nextLine().toUpperCase();

				if (addBagToBaggingArea.equals(YES)) {
					bronzeBaggingArea.addAnItem(bag);
					session.addTotalExpectedWeight(bagWeight);
					totalExpectedMass = new Mass(session.getTotalExpectedWeight());
					difference = totalExpectedMass.inGrams().subtract(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
					if (new Mass(difference).compareTo(bronzeBaggingArea.getSensitivityLimit())==-1) {
						addBag.setAddedBag(bagWeight);
						System.out.println("Your bag was added to the bagging area. No discrepancy detected.");
						break;
					} 
					if (!(new Mass(difference).compareTo(bronzeBaggingArea.getSensitivityLimit())==-1)) {
							System.out.println("Weight discrepancy detected.");
							discrepancy.setDiscrepancy(true);
					}
					
					if (addBag.getAddedBag() && bagWeight == 0) {
						throw new InvalidArgumentSimulationException("Invalid option. Could not detect a bag added to the bagging area.");
					}
				}
				else if (addBagToBaggingArea.equals(NO)) {
					break;
				}
				else if (!addBagToBaggingArea.equals(YES) || !addBagToBaggingArea.equals(NO)) {
					System.out.println("Please try again or enter No to cancel.\n");
				}
			}	
			catch (InputMismatchException | OverloadedDevice e) {
				System.out.println("Invalid entry, error occured. Bag too heavy. Please try again or enter No to cancel.\n");
			}
		}
		
		
		//Ready for more commands from customer
		
//		session.printMenu();
//		choice = scanner.nextInt();

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
				//clients choose how they want to add item 
	            System.out.println("Select Scanner:");
	            System.out.println("1. Handheld Scanner");
	            System.out.println("2. Main Scanner");
	            System.out.print("Your choice: ");
	            int scannerChoice = scanner.nextInt();

	            if (scannerChoice == 1) {
	                // Process item with handheld scanner
	                sessionSimulation.addItemWithHandheldScanner();
	                
	            } else if (scannerChoice == 2) {
	                // Process item with main scanner
	            	
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
			
		} else {
	        System.out.println("Invalid scanner choice.");
		}
				
			}
			else if (choice == 3) { //Pay Via Coin
				payWithCoinControl();
				//break;
			}
			
			else if (choice == 4) { //Pay Via Banknnote
				PayViaBanknote.payViaBanknote();
				receiptPrinted = true;
				System.exit(0);
			}
			else if (choice == 5) { //Pay Via Debit
				sessionSimulation.payViaDebit();
				receiptPrinted = true;
				System.exit(0);
			}

			else if (choice == 6) { //Pay Via Credit
				sessionSimulation.payViaCredit();
				receiptPrinted = true;
				System.exit(0);
			}
			
			else if (choice == 7) { //Remove Item
				if(session.getOrderItem().size() == 0) {
					System.out.println("Cannot remove from empty session list");
					
				}
				else {
					
					System.out.print("Enter barcode to remove: ");
					BigDecimal barcodeInput = scanner.nextBigDecimal();
					
					String barcodeInputString = barcodeInput.toString();

					int i = 0;
					Numeral[] barcodeNumeral = new Numeral[barcodeInputString.length()];
					for(char c : barcodeInputString.toCharArray()) {
						barcodeNumeral[i] = Numeral.valueOf(Byte.valueOf(String.valueOf(c)));
						i++;
					}
					Barcode barcode = new Barcode(barcodeNumeral);
					sessionSimulation.removeItem(barcode);	
					System.out.println("Successfully removed item, currently in session list: " + session.getOrderItem());
				}
			}
			else if (choice == 8) { //Exit
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
	

	public void addItemWithHandheldScanner() {
		
		//sicne there's no actual product is scanned, we are runing the simulaiton
		//using seperated method for future modification
		
	    System.out.println("Please enter the barcode number with handheld scanner");
	    
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
			case YES:
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
				case NO:
					session.addTotalExpectedWeight(product.getExpectedWeight());
					// Process bulky item
					handleBulkyItem(product);
					
					// Process the item as if it was being added to bagging area. Weight reduction in handleBulkyItem should make this work without a wDiscrepancy
					BarcodedItem exemptItem = new BarcodedItem(product.getBarcode(), new Mass(product.getExpectedWeight()));
					session.newOrderItem(exemptItem);
					// Reallocate expected weight as if item was added.
					session.addAmountDue(product.getPrice());
					
					Mass totalExpectedMass1 = new Mass(session.getTotalExpectedWeight());

					try {
						System.out.println("Expected Weight: " + totalExpectedMass1.inGrams() + "OnBaggingArea: " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams() );
						int diff = totalExpectedMass1.inGrams().compareTo(bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
						System.out.println(diff);
						if(diff != 0) {
							System.out.println("Test: " + totalExpectedMass1.inGrams() + "/" + session.getTotalExpectedWeight() + " : " + bronzeBaggingArea.getCurrentMassOnTheScale().inGrams());
							discrepancy.setDiscrepancy(true);
										//product, bronzeBaggingArea.getCurrentMassOnTheScale().inGrams()
							System.out.println("Weight discrepancy detected");
						}
					} catch (OverloadedDevice e) {
						//do nothing
					}
					break;
					default:
					System.out.println("Invalid option. " + product.getDescription() + " not added to bagging area");
			}
		} else {
			System.out.println("Invalid Barcode");
		}
	}
	
	private static Coin quarter, loonie, toonie, dime, nickel;
	
	public static void payWithCoinControl() {
        // turn this into a control method for pay with coin later
        String coinDenom = "something";
        ArrayList<Coin> coinsList = new ArrayList<>();;
        
        if (session.getAmountDue()<=0) {
            System.out.println("No amount due");
        }
        else {
        // changes here to input coins:
            while (!coinDenom.equals("done")) {
                System.out.print("\nInsert coins (nickel, dime, quarter, loonie, toonie),\npress 'done' to exit: ");
                coinDenom = scanner.nextLine();

                if (coinDenom.equals("nickel")) {
                    coinsList.add(nickel);
                }
                else if (coinDenom.equals("dime")) {
                    coinsList.add(dime);
                }
                else if (coinDenom.equals("quarter")) {
                    coinsList.add(quarter);
                }
                else if (coinDenom.equals("loonie")) {
                    coinsList.add(loonie);
                }
                else if (coinDenom.equals("toonie")) {
                    coinsList.add(toonie);
                }
                else if (coinDenom.equals("done")) {
                    break;
                }
                else {
                    System.out.print("\nInvalid coin input");
                }
            }
            sessionSimulation.payWithCoin(coinsList);
//                break;
            
        }
    }
    
    public void payWithCoin(ArrayList<Coin> coins) {
        // this could be anything but we could set it to 1000
        int coinCapacity = 1000;
        
        //add in denoms itself since wasnt working with other code
        ArrayList<BigDecimal> denoms = new ArrayList<>();
        denoms.add(new BigDecimal("0.05"));
        denoms.add(new BigDecimal("0.10"));
        denoms.add(new BigDecimal("0.25"));
        denoms.add(new BigDecimal("1"));
        denoms.add(new BigDecimal("2"));

//        System.out.print("Session amount need to pay:" + session.getAmountDue() + "\n");
        if (session.getAmountDue() != 0) {
        
//             for(BigDecimal denom : denoms) {
//                System.out.println("\t" + denom);
//            }
            // This is to compare the value we put in to the total amount we get in session
            for (Coin coin : coins) {
                if (denoms.contains(coin.getValue())) {
                    bronzeDispenser = new CoinDispenserBronze(coinCapacity);

                    session.subAmountDue(coin.getValue().doubleValue());
//                    System.out.print("session get amount after coin insert:" + session.getAmountDue() + "\n");

                    if (session.getAmountDue() == 0) {
                        System.out.println("Fully paid amount");
                        session.getOrderItem().clear();
                    } else if (session.getAmountDue() < 0) {
                        System.out.println("Amount paid over, change return");
                        session.getOrderItem().clear();
                        double returnChange = -(session.getAmountDue());
                        
                        returnChange();
                        System.out.print("Change returned: " + returnChange);
                        return;
                    }
                } else if (coin.getValue()== null) {
                   throw new NullPointerException();
                	//
                }
                else {
                	System.out.println("Invalid Denomination amount, please try again");
                }
            }
        } else {
            System.out.println("No amount due");
        }
    }
    
    public void returnChange()  {
		List<BigDecimal> denoms = Arrays.asList(
                new BigDecimal("5.0"), new BigDecimal("10.0"), new BigDecimal("20.0"));
		
		BanknoteDispenserGold banknoteDispenser = new BanknoteDispenserGold();
		BanknoteDispenserBronze banknoteDispenserBronze = new BanknoteDispenserBronze();
		
		
		
		
		// converts change into banknotes until coins are necessary
		double changeDue = -(session.getAmountDue());
		
		int changeInt = (int)changeDue * 100;
		
		Banknote twenty = new Banknote(Currency.getInstance("CAD"), denoms.get(2));
        Banknote ten = new Banknote(Currency.getInstance("CAD"), denoms.get(1));
        Banknote five = new Banknote(Currency.getInstance("CAD"), denoms.get(0));
        
        if(changeInt >= 2000) {
        	int twentyCount = (changeInt / 2000);
        	changeInt = (changeInt / 2000); 
        	for(int i = twentyCount; i > 0; i --) {
        		try {
					banknoteDispenser.receive(twenty);
					try {
						banknoteDispenser.emit();
						changeInt = (changeInt / 2000); 
						changeDue -= 20;
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        }
        
        if (changeInt >= 1000) {
        	int tenCount = (changeInt / 1000);
        	
        	for(int i = tenCount; i > 0; i --) {
        		try {
					banknoteDispenser.receive(ten);
					try {
						banknoteDispenser.emit();
						changeInt = (changeInt / 1000);
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					changeDue -= 10;
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        if (changeInt >= 500) {
        	int fiveCount = (changeInt / 500);
        	
        	for(int i = fiveCount; i > 0; i --) {
        		try {
					banknoteDispenser.receive(five);
					try {
						banknoteDispenser.emit();
						changeInt = changeInt / 500;
						changeDue -= 0.25;
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					changeDue -= 5;
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
		
		int coinCapacity = 1000;
		
		
		
		
		bronzeDispenser = new CoinDispenserBronze(coinCapacity);
		goldDispenser = new CoinDispenserGold(coinCapacity);
		// these are used as a sub until i can figure out how to access specified denomination of gold and bronze dispensers
		BigDecimal denomValueBronze = new BigDecimal("0.25");
		BigDecimal denomValueGold = new BigDecimal("1");
		
		Coin loonie = new Coin(denomValueGold);
		Coin quarter = new Coin(denomValueBronze);
		
		if(changeInt >= 100) {
			int loonieCount = changeInt / 100;
			
			
			for(int i = loonieCount; i > 0; i --) {
        		try {
					goldDispenser.receive(loonie);
					try {
						goldDispenser.emit();
						changeInt = changeInt / 100;
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					changeDue -= 0.05;
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		try {
					bronzeDispenser.receive(loonie);
					try {
						bronzeDispenser.emit();
						changeInt = changeInt / 100;
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
			
			
		}
		
		if(changeInt >= 25) {
			int quarterCount = changeInt / 25;
			
			
			for(int i = quarterCount; i > 0; i --) {
				try {
					bronzeDispenser.receive(quarter);
					try {
						bronzeDispenser.emit();
						changeInt = changeInt / 25;
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					goldDispenser.receive(quarter);
					
					changeDue -= 0.25;
					try {
						if(goldDispenser.size() > 0){
						goldDispenser.emit();
						changeInt = changeInt / 25;
						}
					} catch (NoCashAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}
	public void payViaDebit() {
		boolean paymentGood = false;
		SelfCheckoutStationSoftware.bronzeCardReader = new CardReaderBronze();
		SelfCheckoutStationSoftware.bronzeCardReader.plugIn(PowerGrid.instance());
		SelfCheckoutStationSoftware.bronzeCardReader.turnOn();
		
		PayDebitSwipe payment = new PayDebitSwipe();
		
		if(bronzeCardReader.isPoweredUp()) {
			paymentGood = payment.payByDebit();
		}else {
			System.out.println("Card reader is off");
			throw new NoPowerException();
		}
		
		if(paymentGood) {
			System.out.println("Payment successful! Amount Due: 0");
		}else {
			System.out.println("Payment was unsuccessful");
			System.out.println("Please try again or choose a different payment method\n");
			this.getSession().printMenu();
		}
	}
	
	public void payViaCredit() {
		if(session.getAmountDue() == 0) {
			System.out.println("Fully paid amount");
		}
		else {
			PayViaCredit payment = new PayViaCredit();
			Boolean res = payment.CreditSwipe(database);
			if(res) {
				System.out.println("Payment successful! Amount Due: 0");
			}
			else {
				System.out.println("Payment was unsuccessful\n");
				System.out.println("Please try again or choose a different payment method\n");
				}
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
