package com.csw.update;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有升级信息bean
 * @author json_data
 *
 */
public class Getinfo_entity {

	
	
	String result;
	String time;
	ArrayList<InfoEntity> updateInfoList;

	public Getinfo_entity() {
		// TODO Auto-generated constructor stub
	}

	public Getinfo_entity(String result, String time, ArrayList<InfoEntity> infoEntityList) {
		super();
		this.result = result;
		this.time = time;
		this.updateInfoList = infoEntityList;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<InfoEntity> getUpdateInfoList() {
		return updateInfoList;
	}

	public void setUpdateInfoList(ArrayList<InfoEntity> updateInfoList) {
		this.updateInfoList = updateInfoList;
	}

	
}
