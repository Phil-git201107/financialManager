package com.chiczu.fmoney.stockprovider;

import com.chiczu.fmoney.utils.entity.CompanyInfo;
import com.chiczu.fmoney.utils.entity.DayStockInfo;
import com.chiczu.fmoney.utils.entity.MonthStockInfo;
import com.chiczu.fmoney.utils.entity.StockFile;
import com.chiczu.fmoney.utils.utils.CommonUtils;
import com.chiczu.fmoney.utils.utils.GetStockInfo;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@SpringBootTest
public class StockInfoTest {

    Logger logger = LoggerFactory.getLogger(StockInfoTest.class);

    @Autowired
    private GetStockInfo getStockInfo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testGetDayStockInfo(){
        List<DayStockInfo> dayStockInfoList = getStockInfo.getStockInfoByDay("202002", "2002");
        for(DayStockInfo dayStockInfo : dayStockInfoList){
            logger.info(dayStockInfo.toString());
        }
    }

    @Test
    public void testGetMonthStockInfo(){
        List<MonthStockInfo> stockInfoByMonth = getStockInfo.getStockInfoByMonth("2021","2002");
        for(MonthStockInfo stockInfo : stockInfoByMonth){
            logger.info(stockInfo.toString());
        }
    }

    @Test
    public void mongoFind(){
        Query query = new Query(Criteria.where("stockNo").is("2455"));
        List<CompanyInfo> dayStockInfos = mongoTemplate.find(query, CompanyInfo.class);
        logger.info(""+dayStockInfos);
    }

    @Test
    public void mongoRemove(){
        Query query = new Query(Criteria.where("stockNo").is("2455"));
        DeleteResult removeStockFile = mongoTemplate.remove(query, StockFile.class);
        logger.info("removeStockFile= "+removeStockFile.toString());
        DeleteResult removeMonth = mongoTemplate.remove(query, MonthStockInfo.class);
        logger.info("removeMonth= "+removeMonth.toString());
        DeleteResult removeDay = mongoTemplate.remove(query, DayStockInfo.class);
        logger.info("removeDay= "+removeDay.toString());
    }

    @Test
    public void mongoUpdate(){
        Query query = new Query(Criteria.where("stockNo").is("2002"));
        Update update = new Update();
        update.set("price",36.25);
        update.set("amount",2100);
        update.set("cost",76125);
        UpdateResult upsert = mongoTemplate.upsert(query, update, StockFile.class);
        logger.info(upsert.toString());
    }

    @Test
    public void testAsyncGetMonthStockInfo(){
        getStockInfo.createStockInfoForMonth(2021,"2455");
    }

    @Test
    public void testAsyncGetDayStockInfo(){
        getStockInfo.createStockInfoForDay(9,"6213");
    }

    @Test
    public void testGetStockPrice(){
        getStockInfo.getCurrentStockPrice("1717");
    }

    @Test
    public void testGetCurrentDate(){
        String currentDate = CommonUtils.getCurrentDate();
        logger.info(currentDate);
    }
}
