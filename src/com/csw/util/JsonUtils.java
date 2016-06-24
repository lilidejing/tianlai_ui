package com.csw.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class JsonUtils {

	private static Gson gson = new Gson();
	
	/**
	 * 转换对象成json字符串格�?
	 * @param src 被转换的对象
	 * @return json 字符�?
	 */
	public static String objectToJson(Object srcObj){
		String jsonStr = "";
		try {
			jsonStr = gson.toJson(srcObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
	/**
	 * 把json串转化成对象
	 * @param jsonStr json�?
	 * @param typeToken 对象类型
	 * @return 目标对象
	 */
	public static <T> T jsonToObject(String jsonStr,TypeToken<T> typeToken) throws JsonSyntaxException {
		Type type = typeToken.getType();
		T result = null;
		try {
			result = gson.fromJson(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 利用gson包方法将网络返回的inputstream流转成对象（objClass类型�?
	 * 
	 * @param inStream
	 * @param classType
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static <T> T parseObjectFromInputStreamByGson(InputStream inStream,
			Type classType) throws IllegalStateException, IOException {
		Reader reader = new InputStreamReader(inStream);
		JsonReader jReader = new JsonReader(reader);
		return (T)gson.fromJson(jReader, classType);
	}
}
