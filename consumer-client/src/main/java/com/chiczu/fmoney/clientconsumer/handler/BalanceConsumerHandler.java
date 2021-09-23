package com.chiczu.fmoney.clientconsumer.handler;

import com.chiczu.fmoney.feign.BalanceFeign;
import com.chiczu.fmoney.utils.entity.BalanceSheet;
import com.chiczu.fmoney.utils.entity.ExpenditureTypeSum;
import com.chiczu.fmoney.utils.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/balance")
public class BalanceConsumerHandler {

    Logger logger = LoggerFactory.getLogger(StockConsumerHandler.class);

    @Autowired
    private BalanceFeign balanceFeign;

    @GetMapping("getport")
    public String getPort(){
        return balanceFeign.getPort();
    }

    // 獲取當月收支表
    @GetMapping("/getBalanceSheet")
    public Result<List<BalanceSheet>> getBalanceSheet(){
        return balanceFeign.getBalanceSheet();
    }

    // 獲取當月支出類型圖表資料
    @GetMapping("/getBalanceSheet/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForChart(){
        return balanceFeign.getBalanceSheetForChart();
    }

    // 獲取上個月收支表
    @GetMapping("/getBalanceSheet/lastMonth")
    public Result<List<BalanceSheet>> getBalanceSheetForLastMonth(){
        return balanceFeign.getBalanceSheetForLastMonth();
    }

    // 獲取上月支出類型圖表資料
    @GetMapping("/getBalanceSheet/lastMonth/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForLastMonthForChart(){
        return balanceFeign.getBalanceSheetForLastMonthForChart();
    }

    // 存入收支紀錄
    @PutMapping("/balanceSheet/save")
    public Result<String> saveForBalanceSheet(@RequestBody BalanceSheet balanceSheet){
        logger.info("UserProviderController-saveForBalanceSheet"+ balanceSheet);
        return balanceFeign.saveForBalanceSheet(balanceSheet);
    }


}
