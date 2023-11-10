package com.thelocalmarketplace.software.AddItem;

import com.thelocalmarketplace.software.Session.Session;
/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */

public class AddItem {
    private Session session;
    private double totalBill;


    public AddItem(Session session){
        totalBill = 0.0; // initialize the total bill to 0
        this.session = session;
    }

    public void addItemToBill(String item, double price) {
        if (session.sessionActive()) {
            totalBill += price;
            System.out.println( "Added" + item + " ($" + price + ")");
        }
        else{
            System.out.println("Error: Session not active. Please start a session.");
        }
    }
        public double getTotalBill() {
            return totalBill;
        }
}






