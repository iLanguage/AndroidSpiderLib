package ca.ilanguage.spider.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
			Log.e(TAG, e.getMessage());
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
	 * (0.css, 1.css, 2.css, etc), and returns a HashMap of the replacements where the key is the
	 * new relative URL and the value is the original absolute URL.
	 */
	public HashMap<String, String> getAndReplaceCss() {
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
				String newLink = cssIndex + ".css";
				element.attr("href", newLink);
				cssIndex++;
				
				// Map the original and new URLs together
				cssLinks.put(newLink, originalLink);
			}
		}
		
		return cssLinks;
	}
}