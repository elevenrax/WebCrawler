package Finder;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultFinder {


    private String mSearchContents;
    private List<URI> mPagesToSearch;


    public SearchResultFinder(String searchContents) {
        mSearchContents = searchContents;
        mPagesToSearch = new ArrayList<>();
    }


    public List<URI> find() {
        // Class labels that identify results (including ads in results too).
        final String resultMatch = "class=\"r\"";
        final String advertMatch = "class=\"add_cclk\"";

        extractUrlCandidates(resultMatch);
        extractUrlCandidates(advertMatch);

        return mPagesToSearch;
    }


    // TODO Burst approach is not elegant. Fix if time.
    private void extractUrlCandidates(String matching) {
        int index = mSearchContents.indexOf(matching);
        // The range of indexes to include beyond the found instance of `resultMatch` or `advertMatch`
        final int INDEX_BURST = 175;
        final int EOF_INDEX = mSearchContents.length();

        while (index >= 0) {
            int end = index + INDEX_BURST;
            if (end < EOF_INDEX) {
                extractAndSaveSearchUrl(mSearchContents.substring(index, end));
            }
            index = mSearchContents.indexOf(matching, index+1);
        }
    }


    private void extractAndSaveSearchUrl(String line) {
        String urlString = "";

        int begin = line.indexOf("href=\"") + 6;
        int end = line.indexOf("\"", begin+1);
        if (end > begin && begin > 0) {
            urlString = line.substring(begin, end);
            try {
                mPagesToSearch.add( new URI(urlString) );
            }
            catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public String toString() {
        return mPagesToSearch.toString();
    }
}
