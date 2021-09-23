package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@Document("companyInfo")
public class CompanyInfo {

    @Id
    private String id;
    @Field("stockNo")
    private String stockNo;
    @Field("companyName")
    private String companyName;

}
