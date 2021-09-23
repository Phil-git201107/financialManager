package com.chiczu.fmoney.feign;

import com.chiczu.fmoney.utils.entity.MonthStockInfo;
import com.chiczu.fmoney.utils.entity.StockFile;
import com.chiczu.fmoney.utils.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("stock")
public interface StockFeign {

    @GetMapping("/stock/getport")
    public String getPort();

    // 獲取全部庫存股票資料
    @GetMapping("/stock/get/allStockFile")
    public Result<List<StockFile>> getAllStockFile();

    // 建檔股票
    @PutMapping("/stock/save/stockFile")
    public Result<String> saveStockFile(@RequestBody StockFile stockFile);

    // 刪除指定股票
    @DeleteMapping("/stock/delete/oneStock")
    public Result<List<StockFile>> deleteOneStock(@RequestParam("id") String stockNo);

    // 更新指定股票資料
    @PostMapping("/stock/update/oneStockFile")
    public Result<String> updateOneStockFile(@RequestBody StockFile stock);

    // 依股票代碼,自資料庫取得近2年monthStockInfo
    @GetMapping("/stock/get/monthStockInfo")
    public Result<List<MonthStockInfo>> getMonthStockInfo(@RequestParam("stockNo") String stockNo);

    // 依股票代碼,自資料庫取得近2年dayStockInfo
    @GetMapping("/stock/get/dayStockInfo")
    public Result<List<MonthStockInfo>> getDayStockInfo(@RequestParam("stockNo") String stockNo);
}
