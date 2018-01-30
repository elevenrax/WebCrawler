package Core;

import Database.JavascripLibraryDatabase;
import Finder.SearchResultFinder;
import NetUtils.HttpRequest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class GoogleSearchActivity {

    private List<URI> mDomainsToCrawl;
    ConcurrentMap<String, Integer> mJSLibraryCount;

    private static final int RESULTS_PER_PAGE = 50;


    public GoogleSearchActivity() {
        mDomainsToCrawl = new ArrayList<>();
        mJSLibraryCount = new ConcurrentHashMap<>();
    }


    public void startSearch(String query) {
        // Connect to Google
        boolean searchSucceeded = false;
        HttpRequest googleRequest;
        try {
            String sanitisedQuery = query.trim().replaceAll("\\s+", "+");
            URI googleSearch = new URI(
                    "https",
                    null,
                    "google.com",
                    -1,
                    "/search",
                    "q=" + sanitisedQuery + "&num=" + RESULTS_PER_PAGE,
                    null
            );
            googleRequest = new HttpRequest(googleSearch);
            searchSucceeded = googleRequest.makeRequest(HttpRequest.HttpMethod.GET);

            // Obtain list of domains to crawl from result
            if (searchSucceeded) {
                processResult(googleRequest.getResponseBody());
            }
            else {
                useFallback();
            }

        }
        catch (URISyntaxException ex) {
            ex.printStackTrace();
        }

        // Run tasks to find the javascript libraries in each domain found above
        final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool( POOL_SIZE );

        for (URI uri : mDomainsToCrawl) {
            CompletableFuture
                    .supplyAsync( () -> new PageLibraryResolver(uri), executor )
                    .thenApply( result -> {
                        synchronized (this) {
                            for (String s : result.get()) {
                                if (mJSLibraryCount.containsKey(s)) {
                                    int val = mJSLibraryCount.get(s);
                                    mJSLibraryCount.put( s, ++val );
                                }
                                else {
                                    mJSLibraryCount.put(s, 1);
                                }
                            }
                            return result;
                        }
                    });
        }

        // Await executor approval to proceed
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // Sort the libraries and log top 5 to console
        getTopLibraries(5);

    }


    public synchronized void getTopLibraries(int topN) {

        JavascripLibraryDatabase jsDb = JavascripLibraryDatabase.getInstance();

        Comparator<Map.Entry<String, Integer>> valueComparator =
                (e1, e2) -> e2.getValue().compareTo(e1.getValue());

        Map<String, Integer> sortedMap =
                mJSLibraryCount.entrySet().stream()
                        .sorted(valueComparator)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        Iterator<String> libIter = sortedMap.keySet().iterator();
        int i = 0;
        System.out.printf("%-20s %-5s %n", "Library" ,"Count");
        System.out.println("--------------------------");
        while (libIter.hasNext() && i < topN) {
            String key = libIter.next();
            Integer val = sortedMap.get(key);
            System.out.printf("%-20s %5d %n", jsDb.getProperName(key) ,val);
            i++;
        }
    }


    private void processResult(String body) {
        SearchResultFinder finder = new SearchResultFinder(body);
        mDomainsToCrawl = finder.find();
    }


    // Fallback when request fails.
    // Use pre-loaded html file
    private void useFallback() {
        BufferedReader br = null;
        FileReader fr = null;
        String searchString = "";

        try {
            fr = new FileReader("Data/result.html");
            br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                searchString += line;
            }
        }
        catch(IOException ex) {
                ex.printStackTrace();
        }

        processResult(searchString);
    }

}
