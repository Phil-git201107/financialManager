package com.chiczu.fmoney.stockprovider.controller;

import com.chiczu.fmoney.utils.entity.CompanyInfo;
import com.chiczu.fmoney.utils.entity.DayStockInfo;
import com.chiczu.fmoney.utils.entity.MonthStockInfo;
import com.chiczu.fmoney.utils.entity.StockFile;
import com.chiczu.fmoney.utils.utils.CommonUtils;
import com.chiczu.fmoney.utils.utils.GetStockInfo;
import com.chiczu.fmoney.utils.utils.Result;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockProviderController {

    Logger logger = LoggerFactory.getLogger(StockProviderController.class);

    @Autowired
    private GetStockInfo getStockInfo;

    @Autowired
    private MongoTemplate mongoTemplate;

    // 依股票代碼,自資料庫取得近2年monthStockInfo
    @GetMapping("/get/monthStockInfo")
    public Result<List<MonthStockInfo>> getMonthStockInfo(@RequestParam("stockNo") String stockNo){
        Query query = new Query((Criteria.where("stockNo").is(stockNo)));
        List<MonthStockInfo> monthStockInfoList = mongoTemplate.find(query, MonthStockInfo.class);
        if(monthStockInfoList != null){
            return Result.successWithData(monthStockInfoList);
        }else{
            return Result.failed("未獲得【 "+stockNo+" 】的月資料。");
        }
    }

    // 依股票代碼,自資料庫取得近2年dayStockInfo
    @GetMapping("/get/dayStockInfo")
    public Result<List<DayStockInfo>> getDayStockInfo(@RequestParam("stockNo") String stockNo){
        Query query = new Query((Criteria.where("stockNo").is(stockNo)));
        List<DayStockInfo> DayStockInfoList = mongoTemplate.find(query, DayStockInfo.class);
        if(DayStockInfoList != null){
            return Result.successWithData(DayStockInfoList);
        }else{
            return Result.failed("未獲得【 "+stockNo+" 】的日資料。");
        }
    }

    // 更新指定股票資料
    @PostMapping("/update/oneStockFile")
    public Result<String> updateOneStockFile(@RequestBody StockFile stock){
        Query query = new Query(Criteria.where("stockNo").is(stock.getStockNo()));
        Update update = new Update();
        update.set("price",stock.getPrice());
        update.set("amount",stock.getAmount());
        update.set("cost",stock.getPrice()*stock.getAmount());
        mongoTemplate.upsert(query, update, StockFile.class);

        return Result.successWithoutData();
    }

    // 刪除指定股票
    @DeleteMapping("/delete/oneStock")
    public Result<List<StockFile>> deleteOneStock(@RequestParam("id") String stockNo){
        Query query = new Query(Criteria.where("stockNo").is(stockNo));
        DeleteResult removeStockFile = mongoTemplate.remove(query, StockFile.class);
        logger.info("removeStockFile= "+removeStockFile.toString());
        DeleteResult removeMonth = mongoTemplate.remove(query, MonthStockInfo.class);
        logger.info("removeMonth= "+removeMonth.toString());
        DeleteResult removeDay = mongoTemplate.remove(query, DayStockInfo.class);
        logger.info("removeDay= "+removeDay.toString());

        Result<List<StockFile>> allStockFile = getAllStockFile();
        return allStockFile;
    }

    // 獲取全部庫存股票資料
    @GetMapping("/get/allStockFile")
    public Result<List<StockFile>> getAllStockFile(){
        List<StockFile> stockFileList = mongoTemplate.findAll(StockFile.class);
        // 確認資料庫裡是否已有庫存股票
        if(stockFileList != null){
            // 已存在庫存股票
            // 先確認資料庫裡的最近收盤價更新日期是否是當前日,若非,更新要返回資料的相關內容,並再存回資料庫
            String currentDate = CommonUtils.getCurrentDate();
            for(StockFile stock : stockFileList){
                // 如果非當前日,則更新最近收盤價與最近收盤價日期
                if(!(currentDate.equals(stock.getCurrentPriceUpdateDate()))){
                    double currentStockPrice = getStockInfo.getCurrentStockPrice(stock.getStockNo());
                    stock.setCurrentPrice(currentStockPrice);
                    stock.setCurrentPriceUpdateDate(currentDate);
                    // 更新損益情形與類型的方法
                    updateprofitOrLoss(stock);
                    mongoTemplate.save(stock,"stockFile");
                    // 避免一次有多檔股票要向證交所查詢最近收盤價,導致被封鎖,故設計睡6秒再續行程式
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    // 更新損益情形與類型的方法
                    updateprofitOrLoss(stock);
                }
            }
            return Result.successWithData(stockFileList);
        }
        return Result.failed("目前無庫存股票!");
    }

    // 將用戶傳來股票資料,存入資料庫
    @PutMapping("/save/stockFile")
    public Result<String> saveStockFile(@RequestBody StockFile stockFile){
        // 依股票代碼,判斷是否有這支股票
        String stockNo = stockFile.getStockNo();
        Query query = new Query(Criteria.where("stockNo").is(stockNo));
        List<CompanyInfo> companyInfos = mongoTemplate.find(query, CompanyInfo.class);
        if(companyInfos.size() < 1){
            return Result.failed("查無 代碼["+stockNo+"] 的股票資料");
        }
        String stockName = companyInfos.get(0).getCompanyName();
        // 判斷資料庫是否已存在此股票資料
        List<StockFile> stockFileDB = mongoTemplate.find(query, StockFile.class);
        if( stockFileDB.size() < 1 ){
            // 若無存在,則建立近三年月交易資料與日交易資料
            // 進入獲取此股月與日交易資料的for循環需要年月參與計算,故將年月轉成數字
            String dateOri = getStockInfo.getResultMonth(0); // 獲取當前西洋年月
            // year轉成數字
            String year = dateOri.substring(0,4); // 獲取年
            int yearInt = Integer.parseInt(year);
            // month轉成數字
            String month = dateOri.substring(4); // 獲取月
            int monthInt = Integer.parseInt(month);
            // 建立近2年月交易資料,為免前端因遲未收到後端回應而報錯(ERROR CODE:500),故將方法寫為異步方法@Async
            getStockInfo.createStockInfoForMonth(yearInt,stockNo);
            // 建立近2年日交易資料,為免前端因遲未收到後端回應而報錯(ERROR CODE:500),故將方法寫為異步方法@Async
            getStockInfo.createStockInfoForDay(monthInt,stockNo);
            // 設置庫存股名稱
            stockFile.setStockName(stockName);
            // 設置庫存股買入成本金額
            long newCost = (long) (stockFile.getPrice()*stockFile.getAmount());
            stockFile.setCost(newCost);
            // 設置庫存股最近收盤價與設置庫存股最近收盤價更新日期
            updateCurrentPrice(stockFile);
            // 計算並設置庫存股損益情形(顯示為%),如果最近收盤價大於成本價,將type設值為true;反之,設值為false
            updateprofitOrLoss(stockFile);
            // 將資料存入mongodb
            mongoTemplate.save(stockFile);
        }else{
            // 若已存在,則更新資料(數量、成本、買入均價)
            long newAmount = stockFile.getAmount()+stockFileDB.get(0).getAmount();
            long newCost = ((long) ((stockFile.getPrice()*stockFile.getAmount())+stockFileDB.get(0).getCost()));
            double newPrice =  (newCost/newAmount)/1.00;
            stockFileDB.get(0).setAmount(newAmount);
            stockFileDB.get(0).setPrice(newPrice);
            stockFileDB.get(0).setCost(newCost);
            // 將建檔日期,調整為最新一次日期
            stockFileDB.get(0).setDate(stockFile.getDate());
            // 順便更新最近收盤價、更新收盤價日期、損益情形與損益type
            // 先判斷最近收盤價更新日期是否為當前日期
            // 獲取當前日期
            String currentDate = CommonUtils.getCurrentDate();
            double currentStockPrice = stockFileDB.get(0).getCurrentPrice();
            if(!(currentDate.equals(stockFileDB.get(0).getCurrentPriceUpdateDate()))){
                // 不相等,則更新最近收盤價資料
                updateCurrentPrice(stockFileDB.get(0));
            }
            // 計算並設置庫存股損益情形(顯示為%),如果最新收盤價大於成本價,將type設值為true;反之,設值為false
            updateprofitOrLoss(stockFileDB.get(0));

            mongoTemplate.save(stockFileDB.get(0));
        }

        return Result.successWithoutData();
    }

    // 更新指定股票最近收盤價、更新最近收盤價日期的方法
    public StockFile updateCurrentPrice(StockFile stock){
        // 獲取最近收盤價
        double currentStockPrice = getStockInfo.getCurrentStockPrice(stock.getStockNo());
        // 獲取當前日期字串
        String currentDate = CommonUtils.getCurrentDate();

        // 將上述更新資料封裝入stock,並返回
        stock.setCurrentPrice(currentStockPrice);
        stock.setCurrentPriceUpdateDate(currentDate);
        return stock;
    }

    // 更新損益情形與類型的方法,若有updateCurrentPrice(),這個方法必須在其後之後執行
    public StockFile updateprofitOrLoss(StockFile stock){
        // 計算更新收盤價後的損益情形,並轉成 %格式字串
        // 為顯示損益情形格式(xx.xx%)做準備
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        String profitOrLoss = numberFormat.format((stock.getCurrentPrice() - stock.getPrice()) / stock.getPrice());
        stock.setProfitOrLoss(profitOrLoss);
        // 獲取損益類型
        // 如果最新收盤價大於成本價,將type設值為true;反之,設值為false
        if((stock.getCurrentPrice() - stock.getPrice()) > 0){
            stock.setType(true);
        }else{
            stock.setType(false);
        }

        return stock;
    }
}
