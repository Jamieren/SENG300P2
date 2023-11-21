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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

public class PayViaBanknote {

    private BanknoteDispensationSlot banknoteDispensationSlot;

    public PayViaBanknote(SelfCheckoutStationBronze selfCheckoutStationBronze, Session session, BanknoteDispensationSlot banknoteDispensationSlot) {
        this.selfCheckoutStationBronze = selfCheckoutStationBronze;
        this.session = session;
        this.banknoteDispensationSlot = banknoteDispensationSlot;
        this.scanner = new Scanner(System.in); // Initialize the scanner
    }

    public BigDecimal payViaBanknote(Banknote banknote) {
    private static SelfCheckoutStationBronze selfCheckoutStationBronze;
    private static Session session;
    private static Scanner scanner;

    public static void payViaBanknote() {

        if (session.getAmountDue() != 0) {
            List<BigDecimal> denoms = Arrays.asList(
                    new BigDecimal("5.0"), new BigDecimal("10.0"), new BigDecimal("20.0"));

            System.out.println("Choose denomination of banknote being inserted:");
            for (BigDecimal denom : denoms) {
                System.out.println("\t" + denom);
            }

            System.out.print("Denomination: ");
            BigDecimal denom = scanner.nextBigDecimal();
            BigDecimal totalPaid = BigDecimal.ZERO;

            while (denom.compareTo(BigDecimal.valueOf(-1)) != 0 && session.getAmountDue() > 0) {
                double denomAsDouble = denom.doubleValue();

                if (denoms.contains(denom)) {
                    session.subAmountDue(denomAsDouble);
                    totalPaid = totalPaid.add(denom);

                    if (session.getAmountDue() <= 0) {
                        if (session.getAmountDue() < 0) {
                            double change = Math.abs(session.getAmountDue());
                            System.out.println("Fully paid amount, change: " + change);
                        } else {
                            System.out.println("Fully paid amount");
                        }
                        session.getOrderItem().clear();
                        return;
                    }
                    System.out.println("Amount due:" + session.getAmountDue());
                } else {
                    System.out.println("Invalid denomination amount, please try again");
                }

                System.out.println("Choose denomination of banknote being inserted:");
                for (BigDecimal denom2 : denoms) {
                    System.out.println("\t" + denom2);
                }
                System.out.print("Denomination: ");
                denom = scanner.nextBigDecimal();
            }

            if (totalPaid.compareTo(BigDecimal.valueOf(session.getAmountDue())) > 0) {
                BigDecimal change = totalPaid.subtract(BigDecimal.valueOf(session.getAmountDue()));
                System.out.println("Change to return: " + change);
            }
        } else {
            System.out.println("No amount due");
        }
    }
}
