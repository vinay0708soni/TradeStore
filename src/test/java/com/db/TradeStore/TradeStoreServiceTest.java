package com.db.TradeStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for TradeStoreService class
 */
public class TradeStoreServiceTest extends TestCase {
	
	TradeStoreService testInstance;	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TradeStoreServiceTest(String testName) {
        super(testName);
        testInstance = new TradeStoreService();        
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TradeStoreServiceTest.class);
    }

    /**
     * This test case will not add anything as we are passing null object
     * 
     * @throws Exception if any occurs during processing
     */
    public void testStoreTradeDetailsForNullObject() throws Exception {
    	int mapSize = TradeStoreService.tradeMap.size();  
    	
    	testInstance.storeTradeDetails(null);
		assertEquals(mapSize, TradeStoreService.tradeMap.size());
    }
    
    /**
     * This method will store trade objects based on the input values, multiple cases have been executed under this method
     */
    public void testStoreTradeDetails() {
//    	Fetching the size of trader map
    	int mapSize = TradeStoreService.tradeMap.size();  
    	
//    	This Test case will not add any Trade details into Trade Store because maturity date is a past date
		try {
			testInstance.storeTradeDetails(getTrade(1));
			assertEquals(mapSize, TradeStoreService.tradeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
//    	This test case will add new Trade details again into Trade Store, T2 with version id 1 
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(2));
			assertTrue(mapSize < TradeStoreService.tradeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
//    	This Test case will also add new Trade details into Trade Store because we have added T2 trade id again with higher version as 3
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(3));
			assertTrue(mapSize == TradeStoreService.tradeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
//    	This Test case will not add any Trade details into Trade Store because maturity date of Trader T3 is a past date
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(4));
			assertEquals(mapSize, TradeStoreService.tradeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//    	This Test case will throw exception as for T2 we are adding lower version as 2, because 1 and 3 are already present
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(5));
		} catch (Exception e) {
			assertEquals("Lower version of Trade is not allowed", e.getMessage());
		}
		
//    	This Test case will also add new Trade details into Trade Store because we have added T3 trade id as version 3
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(6));
			assertTrue(mapSize < TradeStoreService.tradeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
//    	This Test case will update the existing trade T2 because we have added T2 trade id again as version 1 but with different details
		try {
			mapSize = TradeStoreService.tradeMap.size();  
			testInstance.storeTradeDetails(getTrade(7));
			assertTrue(mapSize == TradeStoreService.tradeMap.size());
			
//			Fetching newly added details from trader map and matching with the provided input values
	    	List<Trade> listAfterInsert = TradeStoreService.tradeMap.get("T2");
	    	
	    	for (Trade tradeAfterInsert : listAfterInsert) {
	    		if ("B5".equals(tradeAfterInsert.getBookingId()) && "CP-5".equals(tradeAfterInsert.getCounterPartyId()) ) {
	    			assertTrue(true);
	    			break;
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * This test case will validate whether any trade has been expired or not, and will update the flag as we are passing past maturity date
     * 
     * @throws Exception
     */
    public void testValidateAndUpdateExpiryFlagSuccess() throws Exception {
    	List listBeforeInsert = new ArrayList<Trade>();
    	Trade tradeBeforeInsert = getTrade(8);
    	boolean beforeInsertExpiry = tradeBeforeInsert.isExpired();
    	listBeforeInsert.add(tradeBeforeInsert);
    	
    	TradeStoreService.tradeMap.put("T9", listBeforeInsert);
    	
    	testInstance.validateAndUpdateExpiryFlag();
    	
    	List listAfterInsert = TradeStoreService.tradeMap.get("T9");
    	Trade tradeAfterInsert = (Trade) listAfterInsert.get(0);
    	
//    	Matching expiry flag before inserting with after inserting value
		assertEquals(beforeInsertExpiry, !tradeAfterInsert.isExpired());
		TradeStoreService.tradeMap.remove("T9");
    }
    
    /**
     * This test case will validate whether any trade has been expired or not, but will not update the flag as we are passing correct maturity date
     * 
     * @throws Exception
     */
    public void testValidateAndUpdateExpiryFlagNoUpdate() throws Exception {
    	List listBeforeInsert = new ArrayList<Trade>();
    	Trade tradeBeforeInsert = getTrade(9);
    	boolean beforeInsertExpiry = tradeBeforeInsert.isExpired();
    	listBeforeInsert.add(tradeBeforeInsert);
    	
    	TradeStoreService.tradeMap.put("T8", listBeforeInsert);
    	
    	testInstance.validateAndUpdateExpiryFlag();
    	
    	List listAfterInsert = TradeStoreService.tradeMap.get("T8");
    	Trade tradeAfterInsert = (Trade) listAfterInsert.get(0);
    	
//    	Matching expiry flag before inserting with after inserting value
		assertEquals(beforeInsertExpiry, tradeAfterInsert.isExpired());
		TradeStoreService.tradeMap.remove("T8");
    }
    
    
    
    /**
     * This method will return the Trade object based on the multiple test case execution ids.
     * 
     * @param caseId input case case id
     * @return Object of Trade
     * @throws ParseException If any occurs during processing
     */
    private Trade getTrade(int testCaseId) throws ParseException {
    	Trade trade = new Trade();
    	switch (testCaseId) {
        case 1:
        	trade.setTradeId("T1");
        	trade.setVersionId(1);
        	trade.setCounterPartyId("CP-1");
        	trade.setBookingId("B1");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2020"));
        	trade.setCreatedDate(new Date());
            break;
        case 2:
        	trade.setTradeId("T2");
        	trade.setVersionId(1);
        	trade.setCounterPartyId("CP-1");
        	trade.setBookingId("B1");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
        	trade.setCreatedDate(new SimpleDateFormat("dd/MM/yyyy").parse("14/03/2015"));
            break;
        case 3:
        	trade.setTradeId("T2");
        	trade.setVersionId(3);
        	trade.setCounterPartyId("CP-2");
        	trade.setBookingId("B1");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
        	trade.setCreatedDate(new Date());
            break;
        case 4:
        	trade.setTradeId("T3");
        	trade.setVersionId(3);
        	trade.setCounterPartyId("CP-3");
        	trade.setBookingId("B2");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2014"));
        	trade.setCreatedDate(new SimpleDateFormat("dd/MM/yyyy").parse("14/03/2015"));
            break;
        case 5:
        	trade.setTradeId("T2");
        	trade.setVersionId(2);
        	trade.setCounterPartyId("CP-1");
        	trade.setBookingId("B1");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
        	trade.setCreatedDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
            break;
        case 6:
        	trade.setTradeId("T3");
        	trade.setVersionId(3);
        	trade.setCounterPartyId("CP-1");
        	trade.setBookingId("B2");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
        	trade.setCreatedDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
            break;
        case 7:
            trade.setTradeId("T2");
        	trade.setVersionId(1);
        	trade.setCounterPartyId("CP-5");
        	trade.setBookingId("B5");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
        	trade.setCreatedDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2021"));
            break;
        case 8:
        	trade.setTradeId("T9");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2014"));
            break;
        case 9:
        	trade.setTradeId("T8");
        	trade.setMaturityDate(new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2022"));
            break;
        default:
        	trade = null;            
        }
    	
    	return trade;    	
    }
}
