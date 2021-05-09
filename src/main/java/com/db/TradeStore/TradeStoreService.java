package com.db.TradeStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TradeStoreService {
	
	/**
	 * This hashMap will store all trade details, used HashMap to eliminate the need of any Database 
	 */
	public static volatile HashMap<String, List<Trade>> tradeMap = new HashMap<String, List<Trade>>();
	
	/**
	 * This constant will contain the exception message;
	 */
	private static final String EXCEPTION_MSG = "Lower version of Trade is not allowed";
	
	/*
	 * Created this static block so that an automatic schedular will be created and
	 * will be invoked everyday/24 hrs interval, which will validate and update
	 * trade expiry flag if the maturity date of the same is less than the current date.
	 */
	static {		
		Runnable runnable = new Runnable() {
			public void run() {
				validateAndUpdateExpiryFlag();
			}
		};
		
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.DAYS);
	}
	
	/**
	 * This method will store the trade details into tradeMap object
	 * 
	 * @param trade input Trade object
	 * @throws Exception If the lower version is being received by the store it
	 */
	public void storeTradeDetails(Trade trade) throws Exception {
		if (null != trade) {
			
//			Validating the trade maturity/expiry date
			if (isTradeExpired(trade.getMaturityDate())) {
				return;			
			}
			
//			Fetching trade id
			String tradeId = trade.getTradeId();
			
//			Fetching trade version id
			int tradeVersionId = trade.getVersionId();
			
//			Fetching List of Trade object based of the given trade id
			List<Trade> tradeList = tradeMap.get(tradeId);
			
//			If trade list is null then add into trade store
			if(null == tradeList || tradeList.isEmpty()) {
				tradeList = new ArrayList<Trade>();
				tradeList.add(trade);
			} else {
//				If the version is already present for this trade then override the details
				boolean isSameVersionIdFound = false;
				for (int i = 0; i < tradeList.size(); i++) {
					if (tradeVersionId == (tradeList.get(i)).getVersionId() ) {
						tradeList.remove(i);
						tradeList.add(i, trade);
						isSameVersionIdFound = true;
						break;
					}
				}
				
//				And if no version matching found and the lower version is being received by the store then throwing an exception.
				if (!isSameVersionIdFound && tradeVersionId < (tradeList.get(tradeList.size()-1)).getVersionId() ) {
					throw new Exception(EXCEPTION_MSG);
				} else if (!isSameVersionIdFound) {
//					And if no version matching found and version is greater then add to the store.
					tradeList.add(trade);					
				}
			}
			tradeMap.put(tradeId, tradeList);
		}
	}
	
	/**
	 * This method will validate whether the trade is expired or not
	 * 
	 * @param tradeDate input trade date
	 * @return true or false
	 */
	private static boolean isTradeExpired(Date maturityDate) {
		return (new Date()).after(maturityDate);
	}
	
	/**
	 * This method will first validate and then update the Expiry Flag for the trade
	 * @param tradeMap input trade map object
	 */
	public static void validateAndUpdateExpiryFlag () {
		for (String key : tradeMap.keySet()) {
			for (Trade trade : tradeMap.get(key)) {
				if (isTradeExpired(trade.getMaturityDate())) {
					trade.setExpired(true);
				}
			}
		}
	}
}
