package com.chiczu.fmoney.balanceprovider.controller;

import com.chiczu.fmoney.utils.entity.BalanceSheet;
import com.chiczu.fmoney.utils.entity.ExpenditureTypeSum;
import com.chiczu.fmoney.utils.utils.CommonUtils;
import com.chiczu.fmoney.utils.utils.GetStockInfo;
import com.chiczu.fmoney.utils.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RestController
@RequestMapping("/balance")
public class BalanceProciderController {

    @Autowired
    private GetStockInfo getStockInfo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${server.port}")
    private String port;

    @GetMapping("/getport")
    public String getPort(){
        return this.port;
    }

    // 獲取當月收支表
    @GetMapping("/getBalanceSheet")
    public Result<List<BalanceSheet>> getBalanceSheet(){
        // 獲取當前月月份字串,如202109
        String dateSubstring = CommonUtils.getResultMonth(0);
        Pattern pattern = Pattern.compile("^" + dateSubstring , Pattern.CASE_INSENSITIVE);
        Query query = Query.query(Criteria.where("date").regex(pattern));
        List<BalanceSheet> balanceSheetList = mongoTemplate.find(query, BalanceSheet.class, "balanceSheet");
        if(balanceSheetList != null){
            return Result.successWithData(balanceSheetList);
        }else{
            return Result.failed("查無["+dateSubstring+"]月份的收支資料");
        }
    }

    // 獲取當月支出類型圖表資料
    @GetMapping("/getBalanceSheet/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForChart(){
        List<ExpenditureTypeSum> result = getExpenditureTypeSum(0);
        return Result.successWithData(result);
    }

    // 獲取上個月收支表
    @GetMapping("/getBalanceSheet/lastMonth")
    public Result<List<BalanceSheet>> getBalanceSheetForLastMonth(){
        // 獲取上個月月份字串,如202108
        String dateSubstring = CommonUtils.getResultMonth(-1);
        Pattern pattern = Pattern.compile("^" + dateSubstring , Pattern.CASE_INSENSITIVE);
        Query query = Query.query(Criteria.where("date").regex(pattern));
        List<BalanceSheet> balanceSheetList = mongoTemplate.find(query, BalanceSheet.class, "balanceSheet");
        if(balanceSheetList != null){
            return Result.successWithData(balanceSheetList);
        }else{
            return Result.failed("查無["+dateSubstring+"]月份的收支資料");
        }
    }

    // 存入收支紀錄
    @PutMapping("/balanceSheet/save")
    public Result<String> saveForBalanceSheet(@RequestBody BalanceSheet balanceSheet){

        if("支出".equals(balanceSheet.getCategory())){
            balanceSheet.setAmount(balanceSheet.getAmount()*-1);
        }
        if(balanceSheet != null){
            BalanceSheet save = mongoTemplate.insert(balanceSheet);
            if(save != null){
                return Result.successWithoutData();
            }else{
                return Result.failed("收支資料存檔失敗");
            }
        }else{
            return Result.failed("服務器為獲取資料");
        }
    }

    // 獲取上月支出類型圖表資料
    @GetMapping("/getBalanceSheet/lastMonth/chart")
    public Result<List<ExpenditureTypeSum>> getBalanceSheetForLastMonthForChart(){
        List<ExpenditureTypeSum> result = getExpenditureTypeSum(-1);
        return Result.successWithData(result);
    }


    // 為支出圖表,獲取指定月份各type支出總數,參數:0表當前月;-1表上個月
    public List<ExpenditureTypeSum> getExpenditureTypeSum(int m){
        // 獲取指定月月份字串,如202109
        String dateSubstring = CommonUtils.getResultMonth(m);
        Pattern pattern = Pattern.compile("^" + dateSubstring , Pattern.CASE_INSENSITIVE);

        Aggregation aggregation = newAggregation(
                match(Criteria.where("date").regex(pattern)),
                match(Criteria.where("category").is("支出")),
                group("type").sum("amount").as("totalAmount")
                        .first("type").as("type")
        );
        // 由mongoTemplate調用aggregate方法,以組合條件進行查詢
        AggregationResults<ExpenditureTypeSum> result = mongoTemplate.aggregate(
                aggregation, "balanceSheet", ExpenditureTypeSum.class);
        List<ExpenditureTypeSum> mappedResults = result.getMappedResults();

        return mappedResults;
    }
}
