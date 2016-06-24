package com.csw.update;

/**
 * 升级信息的entity
 * 
 * @author json_data
 * 
 */
public class InfoEntity {

	private String version;
	private String url;
	private String flag;

	public InfoEntity() {
		// TODO Auto-generated constructor stub
	}

	public InfoEntity(String version, String url, String flag) {
		super();
		this.version = version;
		this.url = url;
		this.flag = flag;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
