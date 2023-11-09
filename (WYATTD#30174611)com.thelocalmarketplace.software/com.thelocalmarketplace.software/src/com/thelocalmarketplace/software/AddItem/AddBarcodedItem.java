package com.thelocalmarketplace.software.AddItem;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.software.Session.Session;
import com.thelocalmarketplace.software.WeightDiscrepancy.WeightDiscrepancy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */


/***
 * This class represents a component for adding barcoded items to a self check-out system
 * It allows for registration fo barcoded items and adding barcodes to the station when certain conditions are met
 */
public class AddBarcodedItem  {
    private Session session;
    private SelfCheckoutStation stationToBeWatched = new SelfCheckoutStation();
    private WeightDiscrepancy weightDiscrepancy = new WeightDiscrepancy(stationToBeWatched);
    // initialize the map to associate barcodes with barcoded items
    private Map<Barcode, BarcodedItem> BARCODED_ITEMS;
    // List to store scanned barcodes
    public List<Barcode> barcodes;

    /***
     * Constructor to create an instance of the AddBarcodedItem class
     * @param session session representing the stations state
     * @param weightDiscrepancy the weight discrepancy monitor
     * @param barcodeScanner barcode scanner for scanning items
     */
    public AddBarcodedItem(Session session, WeightDiscrepancy weightDiscrepancy, IBarcodeScanner barcodeScanner){
        this.session = session;
        this.weightDiscrepancy = weightDiscrepancy;
        // initialize the BARCODED_ITEMS map as a hashmap
        this.BARCODED_ITEMS = new HashMap<>();
        this.barcodes = new ArrayList<>();
    }

    /***
     * Register a barcoded item with a barcode
     * @param item the barcoded item to be registered
     */
    public void registerBarcodedItem(BarcodedItem item){
        BARCODED_ITEMS.put(item.getBarcode(), item);
    }

    /***
     * Get the size of registered barcoded items
     * @return the number of registered barcoded items
     */
    public int getBAR_ITEMSSize() {
        return	BARCODED_ITEMS.size();
    }

    /***
     * Get the barcoded item associated with a specific barcode
     * @param barcode the barcode  to look up
     * @return the barcoded item associated with the provided barcode
     */
    public BarcodedItem getBAR_ITEMSBarcode(Barcode barcode) {
        return	BARCODED_ITEMS.get(barcode);

    }

    /***
     * Add a barcode to the list if specified conditions are met
     * @param barcodeScanner for scanning the barcode
     * @param barcode the barcode to be added
     * @throws SimulationException if any simulation - related error occurs
     * @throws NullPointerException if the provided barcode is null
     * @throws InvalidArgumentSimulationException if any of the required conditions are not met
     */
    public void addBarcode(IBarcodeScanner barcodeScanner, Barcode barcode) throws SimulationException, NullPointerException {
        if (barcode == null) {
            throw new NullPointerException("No barcode");
        } else if (BARCODED_ITEMS.isEmpty()) {
            // when barcode is not found in BARCODED_ITEMS
            throw new InvalidArgumentSimulationException("No barcoded item found");
        } else if (!session.sessionActive()) {
            throw new InvalidArgumentSimulationException("The session is not started");
        } else if (weightDiscrepancy.isBlocked()) {
            throw new InvalidArgumentSimulationException("The station is blocked");
        } else {
            BarcodedItem item = BARCODED_ITEMS.get(barcode);
            barcodes.add(barcode);
            weightDiscrepancy.checkWeight(item.getMass());
        }
    }
}


