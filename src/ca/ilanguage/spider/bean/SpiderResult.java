package ca.ilanguage.spider.bean;

public class SpiderResult {
	private String originalUrl;
	private String fileLocation;
	
	public SpiderResult(String originalUrl, String fileLocation) {
		this.originalUrl = originalUrl;
		this.fileLocation = fileLocation;
	}
	
	public String getOriginalUrl() {
		return originalUrl;
	}
	
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	
	public String getFileLocation() {
		return fileLocation;
	}
	
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
}
