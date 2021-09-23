package com.chiczu.fmoney.utils.utils;

import lombok.Data;
import lombok.ToString;

/**
 * 整個專案使用這類,作為Ajax請求或遠程方法調用返回響應響應資料的格式
 * 
 * @author Phil
 */

@Data
@ToString
public class Result<T> {
	
	private String result;
	private String message;
	private T data;

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	public static final String NO_MESSAGE = "NO_MESSAGE";
	public static final String NO_DATA = "NO_DATA";
	
	public Result(String result, String message, T data) {
		super();
		this.result = result;
		this.message = message;
		this.data = data;
	}

	public Result() {
	}

	/**
	* 操作成功，不需要返回資料
	* @return
	*/
	public static Result<String> successWithoutData() {
		return new Result<String>(SUCCESS, NO_MESSAGE, NO_DATA);
	}
	
	/**
	* 操作成功，需要返回資料
	* @param data
	* @return
	*/
	public static <E> Result<E> successWithData(E data) {
		return new Result<E>(SUCCESS, NO_MESSAGE, data);
	}
	/**
	* 操作失败，返回錯誤訊息
	* @param message
	* @return
	*/
	public static <E> Result<E> failed(String message) {
		return new Result<E>(FAILED, message, null);
	}
}
