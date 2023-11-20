package com.thelocalmarketplace.software;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

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
		weightDiscrepancy = new WeightDiscrepancy();
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
			System.out.println("Successfully started a session.");
		} else {
			System.out.println("\nA session has already been started, the system cannot start a new session "
					 + "while in an active session.");
		}
		this.isActive = true;
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

	Scanner scanner = new Scanner(System.in);

	public void promptToStartSession() throws IOException{
		int choice;
		scanner = new Scanner(System.in);
		System.out.println("Welcome!");
		System.out.println("Enter \"1\" to activate a session: ");
		choice = scanner.nextInt();
		if (choice == 1) {
			activate();
		}
		else {
			System.out.println("\n" + choice + " is not a valid input. Please try again.\n\n");
			System.out.println("Welcome!\n");
			System.out.println("Enter \"1\" to activate a session: \n");
			try {
				choice = scanner.nextInt();
				if (choice == 1) {
					activate();
				}
				else {
					System.out.println("\n" + choice + " is not a valid input. Please try again.\n\n");
				}
			} catch (InputMismatchException e) {
				System.out.println("Invalid entry, error occured. Please try again.\n");
//				e.printStackTrace();
			}
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
				+ "\t 4. Pay via Banknote\n"
				+ "\t 5. Pay via Debit\n"
				+ "\t 6. Exit\n"
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
}
