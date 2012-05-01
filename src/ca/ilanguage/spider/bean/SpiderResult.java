package ca.ilanguage.spider.bean;

public class SpiderResult {
	private Boolean isSuccessful;
	private String originalUrl;
	private String fileLocation;
	private String surveyId;
	
	public SpiderResult(Boolean isSuccessful, String originalUrl, String fileLocation, String surveyId) {
		this.isSuccessful = isSuccessful;
		this.originalUrl = originalUrl;
		this.fileLocation = fileLocation;
		this.surveyId = surveyId;
	}
	
	public Boolean getIsSuccessful() {
		return isSuccessful;
	}

	public void setIsSuccessful(Boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
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