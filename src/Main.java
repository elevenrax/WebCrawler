import Core.GoogleSearchActivity;

public class Main {

    public static void main(String[] args) {

        String searchparams = "";

        if (args.length == 0) {
            System.out.println("Please supply search terms");
            System.exit(1);
        }
        else {
            searchparams = String.join(" ", args);
        }

        try {
            GoogleSearchActivity gsa = new GoogleSearchActivity();
            gsa.startSearch(searchparams);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
