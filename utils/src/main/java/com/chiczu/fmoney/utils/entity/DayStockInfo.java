package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@Document("dayStockInfo")
public class DayStockInfo {

    @Id
    private String did;
    @Field("date")
    private String date; // 20210501
    @Field("stockNo")
    private String stockNo;
    @Field("price")
    private double price;
    @Field("amount")
    private long amount;


}
