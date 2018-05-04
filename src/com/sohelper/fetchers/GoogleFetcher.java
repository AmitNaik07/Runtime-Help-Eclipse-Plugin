package com.sohelper.fetchers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.sohelper.datatypes.GoogleResult;


public class GoogleFetcher {
    
    private final static String SEARCH_SERVICE = "https://www.google.com/search?as_q=";
    private final static String DOMAIN = ":stackoverflow.com";

    // Searches in Google for a query and returns a list containing <code>GoogleResult</code> objects.
     
    public static List<GoogleResult> getGoogleResults(String query, IProgressMonitor monitor) {
        Elements elements = getElements(query);
        if (elements == null) {
            return null;
        }

        monitor.worked(10);
        int size = elements.size();
        ArrayList<GoogleResult> googleResults = new ArrayList<>();
        for (Element element : elements) {
            monitor.worked(20 / size);
            GoogleResult googleResult = getGoogleResult(element);
            if (googleResult != null && googleResult.getUrl().toString().contains("stackoverflow")) {
                googleResults.add(googleResult);
            }
        }

        return googleResults;
    }

    // Returns elements from Google to the given query.
  
    private static Elements getElements(String query) {
        String service = SEARCH_SERVICE + query + DOMAIN;
        Document doc;
        try {
            doc = Jsoup.connect(service).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
        } catch (IOException e) {
            return null;
        }

        return doc.select("div.g");
    }

    // Builds and returns a <code>GoogleResult</code> from a given element.
    
    private static GoogleResult getGoogleResult(Element element) {
        GoogleResult googleResult = new GoogleResult();

        // get title
        Elements titles = element.select("h3[class=r]");
        String title = titles.text();
        googleResult.setTitle(title);

        // extract link to the post in Stack Overflow
        Pattern p = Pattern.compile("url\\?q=(.*?)&");
        Matcher m = p.matcher(element.select("h3.r > a").attr("href"));
        String linkToPost = m.find() ? m.group(1) : "";
        try {
            googleResult.setUrl(new URL(linkToPost));
        } catch (MalformedURLException e) {
            return null;
        }

        return googleResult;
    }
    
}
