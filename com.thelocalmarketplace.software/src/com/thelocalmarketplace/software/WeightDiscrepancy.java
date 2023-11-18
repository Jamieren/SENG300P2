package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.BarcodedProduct;

import java.math.BigDecimal;
/*

 * @author Akashdeep Grewal 30179657
 * @author Amira Wishah 30182579
 * @author Ananya Jain 30196069
 * @author Danny Ly 30127144
 * @author Hillary Nguyen 30161137
 * @author Johnny Tran 30140472 
 * @author Minori Olguin 30035923
 * @author Rhett Bramfield 30170520
 * @author Wyatt Deichert 30174611
 * 
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
	

