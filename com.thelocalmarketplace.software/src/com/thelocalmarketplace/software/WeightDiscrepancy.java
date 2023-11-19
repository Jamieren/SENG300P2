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
	
	public WeightDiscrepancy () {
		discrepancy = false;
	}
	
	public void setBarcodedProduct(BarcodedProduct product) {
		this.product = product;
	}
	
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	
    public void setDiscrepancy(Boolean discrepancy) {
    	this.discrepancy = discrepancy;
    }
	
	public BarcodedProduct getDiscrepancyProduct() {
		return product;
	}

	public BigDecimal getDiscrepancyWeight() {
		return weight;
	}
    
    public boolean getDiscrepancy() {
    	return discrepancy;
    }

}
	

