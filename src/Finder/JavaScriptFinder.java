package Finder;


import Database.JavascripLibraryDatabase;

import java.util.*;

public class JavaScriptFinder {

    private String mBody;
    private JavascripLibraryDatabase mLibDatabase;


    public JavaScriptFinder(String body) {
        mBody = body;
        mLibDatabase = JavascripLibraryDatabase.getInstance();
    }


    // TODO using mBody as string for search is hacky. Fix if time.
    public List<String> findLibaries() {

        List<String> libsFoundInPage = new LinkedList<>();  // For returning
        Set<String> libsInPageHashSet = new HashSet();      // For O(1) checking

        // Remove raw js code to remove false positives
        int bodyBeginIndex = mBody.indexOf("<script>");
        int bondyEndIndex = mBody.indexOf("</script>");
        String sanitisedBody = mBody.substring(0, bodyBeginIndex);
        sanitisedBody += mBody.substring(bondyEndIndex);

        String[] candidates = sanitisedBody.split(">");
        final String searchCriteria = ".js";

        for (String line : candidates) {

            int jsIndex = line.indexOf(searchCriteria);
            String analyseString = "";
            int start = line.lastIndexOf("=");
            if (jsIndex > start && start >= 0) {
                analyseString = line.substring(start, jsIndex);

                /*
                    Assumed that the spec per #3 is only interested in javascript libraries.
                    Therefore, created a store of major libraries to ensure junk data
                    not added.
                 */
                Iterator<String> dbIter = mLibDatabase.iterator();

                while (dbIter.hasNext()) {
                    String lib = dbIter.next();
                    if (analyseString.contains(lib)) {
                        if (!libsInPageHashSet.contains(lib) && line.contains("src=")) {
                            libsFoundInPage.add(lib);
                            libsInPageHashSet.add(lib);
                        }
                    }
                }
            }
        }
        return libsFoundInPage;
    }

}
