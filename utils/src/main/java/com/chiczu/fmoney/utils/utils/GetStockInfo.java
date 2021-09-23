package com.chiczu.fmoney.utils.utils;

import com.chiczu.fmoney.utils.entity.DayStockInfo;
import com.chiczu.fmoney.utils.entity.MonthStockInfo;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class GetStockInfo {
	
	Logger logger = LoggerFactory.getLogger(GetStockInfo.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	// 輸入西洋年月與股票代號,獲取該月各日資料(以日為單位)
	public List<DayStockInfo> getStockInfoByDay(String yearAndMonth, String stockNo){

		String date = yearAndMonth+"01";
		String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date="+date+"&stockNo="+stockNo;
		ArrayList<DayStockInfo> dayStockList = new ArrayList<>();
		try{
			//
			Connection.Response resp = Jsoup.connect(url)
                    .userAgent(Constants.USER_AGENT)
                    .timeout(30 * 1000)
                    .ignoreContentType(true)
                    .execute();
			String respStr = resp.body();
			logger.info("41 getStockInfoByDAY respStr== "+respStr.isEmpty());
			Gson gson = new Gson();
			Map map = gson.fromJson(respStr, Map.class);
			// 自返回的資料中,提取須要的部份
			List dataList = (ArrayList)map.get("data");
			if(dataList == null){
				logger.info(Thread.currentThread().getName()+"--> getStockInfoByDay 47 null= "+date+", stockNo= "+stockNo);
				logger.info("48 respStr=> "+respStr);
				getStockInfoByDay(yearAndMonth,stockNo);
			}
			int length = dataList.size();
			for(int i = 0;i < length;i++){
				ArrayList data = (ArrayList)dataList.get(i);
				// 獲取交易日期,並轉化成須求字串格式,如:1100501
				String dateStr = (String)data.get(0);
				String[] dateArr = dateStr.split("/");
				int yearInt = Integer.parseInt(dateArr[0])+1911;
				String year = String.valueOf(yearInt);
				  // 轉成西洋年日期
				dateStr = year+dateArr[1]+dateArr[2];
				// 獲取成交量,並轉化成long格式
				String amount = (String)data.get(1);
				amount = amount.replace(",","");
				long amountLong = Long.parseLong(amount);

				// 獲取收盤價,並轉成double格式
				String price = (String)data.get(6);
				double priceDouble = Double.parseDouble(price);

				// 將資料封裝入dayStockInfo
				DayStockInfo dayStockInfo = new DayStockInfo();
				dayStockInfo.setDate(dateStr);
				dayStockInfo.setAmount(amountLong);
				dayStockInfo.setPrice(priceDouble);
				dayStockInfo.setStockNo(stockNo);
				// 將dayStockInfo添加到dayStockList
				dayStockList.add(dayStockInfo);
			}
        }catch(IOException ioe){
            logger.info("Exception: " + ioe);
        }

		return dayStockList;
	}

	// 建立近三年日交易資料
	@Async("taskExecutor")
	public void createStockInfoForDay(int monthInt,String stockNo){
		long startTime = System.currentTimeMillis();
		List<DayStockInfo> dayStockInfos = new ArrayList<>();
		for(int j = -(24+monthInt);j<=0;j++){
                String yearAndMonth = getResultMonth(j);
                List<DayStockInfo> stockInfoByDayList = getStockInfoByDay(yearAndMonth, stockNo);
			    logger.info(" createStockInfoForDay:88:"+j+", stockInfoByMonthList--"+stockInfoByDayList.isEmpty());
			    for(DayStockInfo dayInfo : stockInfoByDayList){
			    	dayStockInfos.add(dayInfo);
				}
                // 為免被證交所拒絕連線,延遲10秒再繼續循環
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
		mongoTemplate.insert(dayStockInfos,DayStockInfo.class);
		long endTime = System.currentTimeMillis();
		logger.info(" 結束執行非同步任務createStockInfoForDay,使用時間: "+((endTime-startTime)/1000)+" 秒");
	}

	// 輸入西洋年與股票代號,獲取該年各月資料(以月為單位)
	public List<MonthStockInfo> getStockInfoByMonth(String selectYear, String stockNo){
		String date = selectYear+"0105";
		String url = "https://www.twse.com.tw/exchangeReport/FMSRFK?response=json&date="+date+"&stockNo="+stockNo;
		ArrayList<MonthStockInfo> monthStockList = new ArrayList<>();
		try{
			// 自證交所獲取資料
			Connection.Response resp = Jsoup.connect(url)
					.userAgent(Constants.USER_AGENT)
					.timeout(30 * 1000)
					.ignoreContentType(true)
					.execute();
			String respStr = resp.body();
			Gson gson = new Gson();
			Map map = gson.fromJson(respStr, Map.class);
			// 自返回的資料中,提取須要的部份
			ArrayList dataList = (ArrayList)map.get("data");
			if(dataList == null){
				logger.info("129 respStr= "+respStr);
				logger.info(Thread.currentThread().getName()+"->"+"getStockInfoByMonth 130--date= "+date);
			}

			int length = dataList.size();
			for(int i=0;i<length;i++){
				ArrayList data = (ArrayList) dataList.get(i);
				// 獲取年度 110,並轉成西洋年2021
				Double yearDouble = (Double) data.get(0)+1911;
				String year = String.valueOf(yearDouble);
				int index = year.indexOf(".");
				String newYear = year.substring(0,index);
				// 獲取月份,並轉化成需要的字串格式,如:05
				int monthOri = (int) Math.floor((double)data.get(1));
				String pattern = "%02d"; // 格式化字串，整數，長度2，不足部分左邊補0
				String month = String.format(pattern, monthOri);
				// 結合年度與月份
				String yearAndMonth = newYear+month;
				// 獲取均價
				double price = Double.parseDouble((String)data.get(4));
				/*
				* 獲取日均量
				* 1.先獲取月總量
				* 2.再獲取各月交易日數
				* 3.將 1/2 獲取該月日均量
				* */
				// 1.先獲取月總量
				String amountOri = (String)data.get(7);
				amountOri = amountOri.replace(",","").trim();
				// int長度不足,報numberformatexception for input string xxxx的錯誤,調整為long,正常運行
				long amountNew = Long.parseLong(amountOri);
				// 2.再獲取各月交易日數
				int operateDays = getOperateDays(yearAndMonth, stockNo);
				// 3.將 1/2 獲取該月日均量
				long amount = (long)Math.ceil(amountNew/operateDays);
				// 將資料封裝入monthStockInfo
				MonthStockInfo monthStockInfo = new MonthStockInfo();
				monthStockInfo.setDate(yearAndMonth+"01");
				monthStockInfo.setPrice(price);
				monthStockInfo.setAmount(amount);
				monthStockInfo.setStockNo(stockNo);
				// 將monthStockInfo添加入monthStockList
				monthStockList.add(monthStockInfo);
				// 在獲取各月交易日數階段,必須每次向證交所該服務器請求一個月資料,為免遭封鎖,故延遲10秒再續行循環。
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}catch(IOException ioe){
			logger.info("Exception: " + ioe);
		}
		return monthStockList;
	}

	// 於mongoDB,建立近三年月交易資料
	@Async("taskExecutor")
	public void createStockInfoForMonth(int yearInt, String stockNo){
		long startTime = System.currentTimeMillis();
		List<MonthStockInfo> monthStockInfos = new ArrayList<>();
		for(int i=-2;i<=0;i++){
			List<MonthStockInfo> stockInfoByMonthList = getStockInfoByMonth(""+(yearInt+i), stockNo);
			logger.info("createStockInfoForMonth:176:"+i+", stockInfoByMonthList--"+stockInfoByMonthList.isEmpty());

			for(MonthStockInfo m : stockInfoByMonthList){
				logger.info(m.getStockNo()+" -- "+m.getPrice());
				monthStockInfos.add(m);
			}
		}
		mongoTemplate.insert(monthStockInfos,MonthStockInfo.class);
		long endTime = System.currentTimeMillis();
		logger.info(" 結束執行非同步任務createStockInfoForMonth: "+((endTime-startTime)/1000)+" 秒");
	}

	// 獲取當前月之前後的某個月份(如:202105),參數:獲取當前月前後某個月,正值:往後;負值:往前
	public String getResultMonth(int m) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// 獲取指定的月份(含年份)
		cal.add(Calendar.MONTH,m);
		// 獲取指定西洋年月份字串
		String monthStr = sdf.format(cal.getTime());

		logger.info(Thread.currentThread().getName()+"->211 getResultMonth-- "+monthStr);

		return monthStr;
	}

	// 獲取指定月份交易日數,參數格式:202005
	public int getOperateDays(String yearAndMonth,String stockNo){
		String date = yearAndMonth+"01";
		String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date="+date+"&stockNo="+stockNo;

		Connection.Response resp = null;
		try {
			resp = Jsoup.connect(url)
					.userAgent(Constants.USER_AGENT)
					.timeout(30 * 1000)
					.ignoreContentType(true)
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String respStr = resp.body();
		Gson gson = new Gson();
		Map map = gson.fromJson(respStr, Map.class);
		ArrayList dataList = (ArrayList)map.get("data");
		logger.info(Thread.currentThread().getName()+"-> 235 getOperateDays-- "+yearAndMonth+"--"+stockNo+"--"+date);
		if(dataList == null){
			logger.info(Thread.currentThread().getName()+"->237 getOperateDays dataList-- null"+ respStr);
			getOperateDays(yearAndMonth,stockNo);
		}
		logger.info(Thread.currentThread().getName()+"->236 getOperateDays dataList-- "+ dataList.isEmpty());
		int size = dataList.size();
		return size;
	}

	// 獲取個股當前股價
	public double getCurrentStockPrice(String stockNo){
		String date = getResultMonth(0)+"01";
		String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date="+date+"&stockNo="+stockNo;

		// 自證交所獲取資料
		Connection.Response resp = null;
		try {
			resp = Jsoup.connect(url)
					.userAgent(Constants.USER_AGENT)
					.timeout(30 * 1000)
					.ignoreContentType(true)
					.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String respStr = resp.body();
		Gson gson = new Gson();
		Map map = gson.fromJson(respStr, Map.class);
		// 自返回的資料中,提取須要的部份
		ArrayList dataList = (ArrayList)map.get("data");
		// 獲取最後一筆資料,索引值為6(收盤價)的數據
		int length = dataList.size();
		ArrayList data = (ArrayList) dataList.get(length-1);
		String priceStr = (String)data.get(6);
		// 轉成float格式
		Double price = Double.parseDouble(priceStr);
		logger.info("271 getCurrentStockPrice stockNo= "+stockNo+", price= "+price);
		return price;
	}

}
