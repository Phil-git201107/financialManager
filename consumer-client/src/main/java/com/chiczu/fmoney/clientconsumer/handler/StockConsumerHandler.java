package com.chiczu.fmoney.clientconsumer.handler;

import com.chiczu.fmoney.feign.StockFeign;
import com.chiczu.fmoney.utils.entity.MonthStockInfo;
import com.chiczu.fmoney.utils.entity.StockFile;
import com.chiczu.fmoney.utils.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockConsumerHandler {

    Logger logger = LoggerFactory.getLogger(StockConsumerHandler.class);

    @Autowired
    private StockFeign stockFeign;

    @GetMapping("/getport")
    public String getPort(){
        return stockFeign.getPort();
    }

    // 依股票代碼,自資料庫取得近2年monthStockInfo
    @GetMapping("/get/monthStockInfo")
    public Result<List<MonthStockInfo>> getMonthStockInfo(@RequestParam("stockNo") String stockNo){
        return stockFeign.getMonthStockInfo(stockNo);
    }

    // 依股票代碼,自資料庫取得近2年dayStockInfo
    @GetMapping("/get/dayStockInfo")
    public Result<List<MonthStockInfo>> getDayStockInfo(@RequestParam("stockNo") String stockNo){
        return stockFeign.getDayStockInfo(stockNo);
    }

    // 更新指定股票資料
    @PostMapping("/update/oneStockFile")
    public Result<String> updateOneStockFile(@RequestBody StockFile stock){
        logger.info("StockConsumerHandler "+stock.toString());
        return stockFeign.updateOneStockFile(stock);
    }

    // 刪除指定股票
    @DeleteMapping("/delete/oneStock")
    public Result<List<StockFile>> deleteOneStock(@RequestParam("id") String stockNo){
        logger.info("StockConsumerHandler== "+stockNo);
        return stockFeign.deleteOneStock(stockNo);
    }

    // 獲取全部庫存股票資料
    @GetMapping("/get/allStockFile")
    public Result<List<StockFile>> getAllStockFile(){
        return stockFeign.getAllStockFile();
    }

    // 建檔股票
    @PutMapping("/save/stockFile")
    public Result<String> saveStockFile(@RequestBody StockFile stockFile){
        logger.info(stockFile.toString());
        return stockFeign.saveStockFile(stockFile);
    }
}
