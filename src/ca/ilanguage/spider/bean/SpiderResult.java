package ca.ilanguage.spider.bean;

public class SpiderResult {
	private String originalUrl;
	private String fileLocation;
	private String surveyId;
	
	public SpiderResult(String originalUrl, String fileLocation, String surveyId) {
		this.originalUrl = originalUrl;
		this.fileLocation = fileLocation;
		this.surveyId = surveyId;
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

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
}
