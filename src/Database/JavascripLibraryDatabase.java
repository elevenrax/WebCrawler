package Database;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

//TODO Implement better library repository. Can get vals from here: https://www.javascripting.com
//TODO Differentiate between Angular and Angular JS
//TODO Use Levenshtein distance to return shortest distance result if above threshhold? -- No way I have time to do this now.
public class JavascripLibraryDatabase implements Iterable<String> {


    private static JavascripLibraryDatabase mInstance;

    HashSet<String> mDatabase;

    private JavascripLibraryDatabase() {
        mDatabase = new HashSet<>();
        mDatabase.add("angular");
        mDatabase.add("jquery");
        mDatabase.add("react");
        mDatabase.add("bootstrap");
        mDatabase.add("analytics");
        mDatabase.add("nightwatch");
        mDatabase.add("backbone");
        mDatabase.add("vue");
        mDatabase.add("ember");
        mDatabase.add("knockout");
        mDatabase.add("npm");
        mDatabase.add("redux");
        mDatabase.add("loadash");
        mDatabase.add("underscore");
        mDatabase.add("yarn");
        mDatabase.add("socket");
        mDatabase.add("leaflet");
        mDatabase.add("foundation");
    }

    public static JavascripLibraryDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new JavascripLibraryDatabase();
        }
        return mInstance;
    }


    public String getProperName(String key) {
        if (mDatabase.contains(key)) {
            switch(key) {
                case "angular":
                    return "Angular JS";
                case "jquery":
                    return "jQuery";
                case "react":
                    return "React JS";
                case "bootstrap":
                    return "Bootstrap";
                case "analytics":
                    return "Google Analytics";
                case "nightwatch":
                    return "Nightwatch.JS";
                default:
                    if (mDatabase.contains(key)) return key;
            }
        }
        return null;
    }


    @Override
    public Iterator<String> iterator() {
        return mDatabase.iterator();
    }


    @Override
    public void forEach(Consumer<? super String> action) {
        for (String s : this) {
            action.accept(s);
        }
    }


    @Override
    public Spliterator<String> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}
