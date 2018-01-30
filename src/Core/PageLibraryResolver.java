package Core;

import Finder.JavaScriptFinder;
import NetUtils.HttpRequest;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

public class PageLibraryResolver implements Supplier<List<String>> {

    HttpRequest request;

    public PageLibraryResolver(URI uri) {
        request = new HttpRequest(uri);
    }

    @Override
    public List<String> get() {
        // Start request
        request.makeRequest(HttpRequest.HttpMethod.GET);

        // Get Result
        String result = request.getResponseBody();

        // Use JSF to get details
        JavaScriptFinder jsf = new JavaScriptFinder(result);
        List<String> libraries = jsf.findLibaries();

        // Return name of Library used
        return libraries;
    }

}
