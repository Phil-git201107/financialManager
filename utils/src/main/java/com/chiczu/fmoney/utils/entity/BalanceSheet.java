package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@Document("balanceSheet")
public class BalanceSheet {

    @Id
    private String id;
    @Field("date")
    private String date;
    @Field("amount")
    private int amount;
    @Field("category")
    private String category;
    @Field("type")
    private String type;

}
