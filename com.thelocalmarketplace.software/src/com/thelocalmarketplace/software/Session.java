package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
/*
 * StartSession controls whether the SelfCheckoutStation is in an active state ready for customer interaction 
 * */

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
        isActive = true;
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
    
    public double getTotalExpectedWeight() {
    	return totalExpectedWeight;
    }
    
    public void addTotalExpectedWeight(double weight) {
    	totalExpectedWeight += weight;
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
    
    public void setWeightDiscrepancy(BarcodedProduct product, BigDecimal weight) {
    	weightDiscrepancy = new WeightDiscrepancy(product, weight);
    }
    
    public void setNoWeightDiscrepancy() {
    	weightDiscrepancy = null;
    }
    
    public boolean hasWeightDiscrepancy() {
    	return weightDiscrepancy != null;
    }
    
    public WeightDiscrepancy getWeightDiscrepancy() {
    	return weightDiscrepancy;
    }

}
