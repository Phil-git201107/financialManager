package com.chiczu.fmoney.feign;

import com.chiczu.fmoney.utils.entity.BalanceSheet;
import com.chiczu.fmoney.utils.entity.ExpenditureTypeSum;
import com.chiczu.fmoney.utils.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("balance")
public interface BalanceFeign {

    @GetMapping("/balance/getport")
    public String getPort();

    // 獲取當月收支表
    @GetMapping("/balance/getBalanceSheet")
    public Result<List<BalanceSheet>> getBalanceSheet();

    // 獲取當月支出類型圖表資料
    @GetMapping("/balance/getBalanceSheet/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForChart();

    // 獲取上個月收支表
    @GetMapping("/balance/getBalanceSheet/lastMonth")
    public Result<List<BalanceSheet>> getBalanceSheetForLastMonth();

    // 獲取上月支出類型圖表資料
    @GetMapping("/balance/getBalanceSheet/lastMonth/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForLastMonthForChart();

    // 存入收支紀錄
    @PutMapping("/balance/balanceSheet/save")
    public Result<String> saveForBalanceSheet(@RequestBody BalanceSheet balanceSheet);

}
