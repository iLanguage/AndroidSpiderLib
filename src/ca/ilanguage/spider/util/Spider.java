package ca.ilanguage.spider.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

// Code based on: http://www.javaworld.com/javaworld/jw-11-2004/jw-1101-spider.html
public class Spider {
	private static final String TAG = "Spider";
	private Document doc = null;
	
	/**
	 * Initializes a DOM object of the given URL's HTML.
	 */
	public Spider(String url) {
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			Log.e(TAG, "io exception", e);
		}
	}
	
	/** 
	 * Initializes a DOM object of the given HTML file.
	 */
	public Spider(File f) {
		try {
			doc = Jsoup.parse(f, "UTF-8");
		} catch (IOException e) {
			Log.e(TAG, "io exception", e);
		}
	}

	/**
	 * Get the HTML as a String.
	 */
	public String getHtml() {
		return (doc == null) ? "" : doc.toString();
	}
	
	/**
	 * Get the title of the HTML.
	 */
	public String getTitle() {
		return (doc == null) ? "" : doc.title();
	}
	
	/**
	 * Get the body of the HTML.
	 */
	public String getBody() {
		return (doc == null) ? "" : doc.body().html();
	}
	
	/**
	 * Get the content of any <style> tags.
	 */
	public ArrayList<String> getStyles() {
		ArrayList<String> styles = new ArrayList<String>();
		
		if (doc != null) {
			// Loop over all the <style> tags
			Elements elements = doc.select("style");
			for (Element element : elements) {
				// Add to the array the contents of the <style> tag
				styles.add(element.html());
			}
		}
		
		return styles;
	}
	
	/**
	 * Gets an array of CSS links in the HTML.
	 */
	public ArrayList<String> getCss() {
		ArrayList<String> cssFiles = new ArrayList<String>();
		
		if (doc != null) {
			// Loop over all <link> tags that have the attribute type="text/css"
			Elements elements = doc.select("link[type=text/css]");
			for (Element element : elements) {
				// Add to the array the absolute path referenced in the href attribute
				String cssUrl = element.attr("abs:href");
				cssFiles.add(cssUrl);
			}
		}
		
		return cssFiles;
	}
	
	/**
	 * Gets all linked CSS URLs in the HTML, replaces the URLs in the HTML with a new relative URL,
	 * (css0.css, css1.css, etc), and returns a HashMap of the replacements where the key is the
	 * new relative URL and the value is the original absolute URL.
	 */
	public HashMap<String, String> getAndReplaceCss(String filePrefix, String fileSuffix) {
		HashMap<String, String> cssLinks = new HashMap<String, String>();
		int cssIndex = 0;
		
		if (doc != null) {
			// Loop over all <link> tags that have the attribute type="text/css"
			Elements elements = doc.select("link[type=text/css]");
			for (Element element : elements) {
				// Get the original absolute URL of the CSS file
				String originalLink = element.attr("abs:href");
				
				// Remove the original URL from the CSS file
				element.removeAttr("href");
				
				// Add the new relative URL to the CSS file
				String newLink = filePrefix + cssIndex + fileSuffix;
				element.attr("href", newLink);
				cssIndex++;
				
				// Map the original and new URLs together
				cssLinks.put(newLink, originalLink);
			}
		}
		
		return cssLinks;
	}

	/**
	 * Gets all CSS URLs imported in the HTML's <style> tag, replaces the URLs in the HTML 
	 * with a new relative URL, (import0.css, import1.css, etc), and returns a HashMap of 
	 * the replacements where the key is the new relative URL and the value is the original 
	 * absolute URL.
	 * 
	 * Assumes that the CSS URLs imported in the HTML's <style> tag are absolute URLs.
	 * 
	 * Assumes that if the protocol was not specified, then it will work with the HTTP protocol.
	 */
	// Code based on: http://www.javamex.com/tutorials/regular_expressions/search_replace_loop.shtml
	public HashMap<String, String> getAndReplaceImports(String filePrefix, String fileSuffix) {
		HashMap<String, String> importLinks = new HashMap<String, String>();
		int importIndex = 0;
		
		if (doc != null) {
			// Loop over all the <style> tags
			Elements elements = doc.select("style");
			for (Element element : elements) {
				// Get the contents of the <style> tag
				String style = element.html();
				
				// Regular expression to get the content of any @import statements
				Pattern patt = Pattern.compile("@import ([^;]*);");
				Matcher m = patt.matcher(style);
				
				// Loop over all the @import statements
				StringBuffer sb = new StringBuffer(style.length());
			  	while (m.find()) {
			  		// Get the old URL specified in the @import
			  		String importContent = m.group(1).trim();
			  		String originalLink = "";
			  		if (importContent.contains("\"")) {
			  			// The URL is within double quotes
			  			originalLink = importContent.substring(importContent.indexOf("\"") + 1, importContent.lastIndexOf("\""));
			  		} else if (importContent.contains("'")) {
			  			// The URL is within single quotes
			  			originalLink = importContent.substring(importContent.indexOf("'") + 1, importContent.lastIndexOf("'"));
			  		} else if (importContent.contains("(")) {
			  			// The URL is within brackets without any quotes
			  			originalLink = importContent.substring(importContent.indexOf("(") + 1, importContent.lastIndexOf(")"));
			  		} else if (importContent.contains(" ")) {
			  			// The URL is not quoted and is not within brackets but does have media specified
			  			originalLink = importContent.substring(0, importContent.indexOf(" "));
			  		} // else The URL is the only thing after the @import
			  		originalLink.trim();
			  		
			  		// Trim any leading "//" from
			  		if (originalLink.indexOf("//") == 0) {
			  			originalLink = originalLink.substring("//".length());
			  		}
			  		
			  		// Add "http://" if necessary
			  		if (originalLink.indexOf("http") != 0) {
			  			originalLink = "http://" + originalLink;
			  		}
			  		
			  		// Add the new relative URL to the @import
			  		String newLink = filePrefix + importIndex + fileSuffix;
			  		m.appendReplacement(sb, "@import '" + newLink + "';");
			  		importIndex++;
			  		
					// Map the original and new URLs together
					importLinks.put(newLink, originalLink);
			  	}
			  	m.appendTail(sb);
			  	
			  	// Replace the contents of the <style> tag with the new URL
			  	element.html(sb.toString());
			}
		}
		
		return importLinks;
	}

	/**
	 * Gets all file URLs referenced in the HTML's <style> tag, replaces the URLs in the HTML 
	 * with a new relative URL, (url0, url1, etc), and returns a HashMap of 
	 * the replacements where the key is the new relative URL and the value is the original 
	 * absolute URL.
	 * 
	 * Assumes that the URLs imported in the HTML's <style> tag are absolute URLs.
	 * 
	 * Assumes that if the protocol was not specified, then it will work with the HTTP protocol.
	 */
	// Code based on: http://www.javamex.com/tutorials/regular_expressions/search_replace_loop.shtml
	public HashMap<String, String> getAndReplaceUrls(String filePrefix, String fileSuffix) {
		HashMap<String, String> urlLinks = new HashMap<String, String>();
		int urlIndex = 0;
		
		if (doc != null) {
			// Loop over all the <style> tags
			Elements elements = doc.select("style");
			for (Element element : elements) {
				// Get the contents of the <style> tag
				String style = element.html();
				
				// Regular expression to get the content of any url() statements
				Pattern patt = Pattern.compile("url\\(([^\\)]*)\\)");
				Matcher m = patt.matcher(style);
				
				// Loop over all the url() statements
				StringBuffer sb = new StringBuffer(style.length());
			  	while (m.find()) {
			  		// Get the old URL specified in the url()
			  		String originalLink = m.group(1).trim();
			  		if (originalLink.contains("\"")) {
			  			// The URL is within double quotes
			  			originalLink = originalLink.substring(originalLink.indexOf("\"") + 1, originalLink.lastIndexOf("\""));
			  		} else if (originalLink.contains("'")) {
			  			// The URL is within single quotes
			  			originalLink = originalLink.substring(originalLink.indexOf("'") + 1, originalLink.lastIndexOf("'"));
			  		} // else The URL is the only thing in the url()
			  		originalLink.trim();
			  		
			  		// Trim any leading "//" from
			  		if (originalLink.indexOf("//") == 0) {
			  			originalLink = originalLink.substring("//".length());
			  		}
			  		
			  		// Add "http://" if necessary
			  		if (originalLink.indexOf("http") != 0) {
			  			originalLink = "http://" + originalLink;
			  		}
			  		
			  		// Change any "&amp;" to "&"
			  		originalLink = originalLink.replaceAll("&amp;", "&");
			  		
			  		// Add the new relative URL to the url()
			  		String newLink = filePrefix + urlIndex + fileSuffix;
			  		m.appendReplacement(sb, "url('" + newLink + "')");
			  		urlIndex++;
			  		
					// Map the original and new URLs together
					urlLinks.put(newLink, originalLink);
			  	}
			  	m.appendTail(sb);
			  	
			  	// Replace the contents of the <style> tag with the new URL
			  	element.html(sb.toString());
			}
		}
		
		return urlLinks;
	}
}