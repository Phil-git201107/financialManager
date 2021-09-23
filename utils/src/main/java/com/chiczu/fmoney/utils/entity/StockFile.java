package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@Document("stockFile")
public class StockFile {

    @Id
    private String id;
    @Field("date")
    private String date;
    @Field("stockNo")
    private String stockNo;
    @Field("stockName")
    private String stockName;
    @Field("price")
    private Double price; // 均價
    @Field("amount")
    private long amount; // 庫存數-股
    @Field("cost")
    private long cost;
    @Field("currentPrice")
    private double currentPrice;
    @Field("currentPriceUpdateDate")
    private String currentPriceUpdateDate;
    @Field("profitOrLoss")
    private String profitOrLoss; // 個股損益情形 %
    @Field("type")
    private boolean type; // profitOrLoss 正值:true;負值:false,配合前端圖表需求設置的屬性
}
