package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.BarcodedProduct;

import java.math.BigDecimal;
/*
 * 
 * */

public class WeightDiscrepancy {

	private BarcodedProduct product;
	private BigDecimal weight;
	private boolean discrepancy;
	
	public WeightDiscrepancy (BarcodedProduct product, BigDecimal weight) {
		this.product = product;
		this.weight = weight;
	}
	
	public BarcodedProduct getProduct() {
		return product;
	}

	public BigDecimal getWeight() {
		return weight;
	}
	
    public void setWeightDiscrepancy(Boolean discrepancy) {
    	discrepancy = true;
    }
    
    public void setNoWeightDiscrepancy() {
    	discrepancy = true;
    }
    
    public boolean hasWeightDiscrepancy() {
    	return discrepancy = false;
    }
    
    public boolean getWeightDiscrepancy() {
    	return discrepancy;
    }

}
	

