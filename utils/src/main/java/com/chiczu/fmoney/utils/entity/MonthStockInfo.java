package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@Document("monthStockInfo")
public class MonthStockInfo {
	
	 @Id
	 private String mid;
	@Field("date")
	private String date; // 202105
	 @Field("stockNo")
	 private String stockNo;
	 @Field("price")
	 private double price; // 均價
	 @Field("amount")
	 private long amount; // 日均量
}
