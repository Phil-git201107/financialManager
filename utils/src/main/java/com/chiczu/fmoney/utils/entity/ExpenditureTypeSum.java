package com.chiczu.fmoney.utils.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExpenditureTypeSum {

    private String type;
    private int totalAmount;

}
