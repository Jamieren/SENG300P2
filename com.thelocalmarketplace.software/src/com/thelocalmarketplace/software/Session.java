package com.thelocalmarketplace.software;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;

public class Session {

	private static Session instance = null;
	private boolean isActive = false;
	private static ArrayList<BarcodedItem> orderItems;
	private static double totalExpectedWeight;
	private static double amountDue;
	private static WeightDiscrepancy weightDiscrepancy;
	
	private Session() {
		//Instantiate data
		orderItems = new ArrayList<BarcodedItem>();
		totalExpectedWeight = 0;
		amountDue = 0;
		weightDiscrepancy = null;
	}
	
	public static Session getInstance() {
		if(instance == null) {
			instance = new Session();
		}
		return instance;
	}
	
	public boolean isActive() {
        return isActive;
    }

    public void activate() {
		if(isActive()==false) {
			System.out.println("A session has already been started, the system cannot start a new session "
							 + "while in an active session.");
		} else {
			isActive = true;
			System.out.println("Successfully started a session.");
		}
    }

    public void deactivate() {
        isActive = false;
    }
	
    public ArrayList<BarcodedItem> getOrderItem() {
    	if(orderItems == null) {
    		throw new NullPointerException();
    	}
    	return orderItems;
    }
    
    public void newOrderItem(BarcodedItem item) {
    	orderItems.add(item);
    }
    public void removeOrderItem(BarcodedItem item) {
    	orderItems.remove(item);
    }
    public BarcodedItem findItem(Barcode barcode) {
    	for(BarcodedItem item: orderItems) {
    		if(item.getBarcode() == barcode) {
    			return item;
    		}
    	}
    	return null;
    }
    
    public double getTotalExpectedWeight() {
    	return totalExpectedWeight;
    }
    
    public void addTotalExpectedWeight(double weight) {
    	totalExpectedWeight += weight;
    }
    public void subtractTotalExpectedWeight(double weight) {
    	totalExpectedWeight -= weight;
    }
    
    public double getAmountDue() {
    	return amountDue;
    }
    
    public void addAmountDue(double amount) {
    	amountDue += amount;
    }
    
    public void subAmountDue(double amount) {
    	amountDue -= amount;
    }
    
	public void promptEnterToContinue(){

		System.out.println("Welcome!");
		System.out.println("Press \"ENTER\" to activate a session: ");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printMenu() {
		if(getOrderItem().size() != 0) {
			System.out.println("\n============================\n"
								+ "Order Items:");
			int i = 1;
			for(BarcodedItem bi : getOrderItem()) {
				System.out.println("\t   " + i + ") " + bi.getBarcode() + " : " + bi.getMass().inGrams() + " gramms");
				i++;
			}
			System.out.println("Total due: " + getAmountDue());
		}
		
		System.out.print("\n============================\n"
				+ "Choose option:\n"
				+ "\t 1. Activate Session\n"
				+ "\t 2. Add Item\n"
				+ "\t 3. Pay via Coin\n"
				+ "\t 4. Exit\n"
				+ "Choice: ");
	}
         
    public void weightDiscrepancyMessage() {
		System.out.print("\n============================\n"
				 + "Weight Discrepancy has been detected\n"
				 + "Product: " + weightDiscrepancy.getDiscrepancyProduct().getDescription() + "caused discrepancy\n"
				 + "\tHas weight " + weightDiscrepancy.getDiscrepancyWeight() + ", was expecting " + getTotalExpectedWeight() + "\n\n"
				 + "\t 1. Add/Remove item\n"
				 + "\t 2. Do-Not-Bag Request\n"
				 + "\t 3. Attendant Approval\n"
				 + "\t4. Exit\n"
				 + "Choice: ");
    }
    
//	int choice = 0;
//	if (session == null) {
//		session.printMenu();
//		choice = scanner.nextInt();
//		while (choice != 1) {
//			if (choice == 2) {
//				System.out.println("Cannot add an item while session is not active.\n");
//				session.printMenu();
//				choice = scanner.nextInt();
//			}
//			else if (choice == 3) {
//				System.out.println("Cannot pay while a session is not active.\n");
//				session.printMenu();
//				choice = scanner.nextInt();
//			}
//			else if (choice == 4) {
//				System.out.println("Cannot quit session while a session is not active.\n");
//				session.printMenu();
//				choice = scanner.nextInt();
//			}
//			else {
//				System.out.println("Not a valid entry, please select an option from the menu.\n");
//				session.printMenu();
//				choice = scanner.nextInt();
//			}
//		}
//		session = Session.getInstance();
//	}
    
    
}
