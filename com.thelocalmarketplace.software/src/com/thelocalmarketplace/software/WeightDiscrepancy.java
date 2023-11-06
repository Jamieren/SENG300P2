package com.thelocalmarketplace.software;

import com.thelocalmarketplace.hardware.BarcodedProduct;

import java.math.BigDecimal;
/*
 * 
 * */

public class WeightDiscrepancy {

	
	private BarcodedProduct product;
	private BigDecimal weight;
	
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
}
	

