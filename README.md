# WARNING

Still under development. Very experimental software. Many fixed needed!

# Web Crawler

A library to search google and return the top five Javascript libraries in use. 
NB. No third-party libraries used (to challenge self). 

```$xslt
cd WebCrawler/
./run.sh terms that you want to search for
```

## ISSUES

Not all tasks have been completed. 
The following are issues that need to be resovled:

### GoogleSearchActivity

##### (1) Provision for Google Search Failure

Used `CompletableFutures` to callback with response and update a `ConcurrentHashMap` with its findings. 
If a search fails (i.e. not 200) will use a fallback file for subsequent steps. 

##### (2) Domain in `mDomainsToCrawl` more than once

The list `mDomainsToCrawl` should not have the same domain twice. Some checking needed to remove same domains. 

### PageLibraryResolver

Simple class that implements `Supplier<T>` for ingestion into a `CompletableFuture`. 

### JavascriptLibraryDatabase

##### (1) No differentiation between Angular and Angular JS

Issue. 

##### (2) Incomplete

Need to obtain exhaustive list of libraries and do extensive testing.

##### (3) Partial matches to Libraries in the Database
Be nice to have Levenshtein distance to return shortest distance result if above threshhold.

##### (4) Errors in identifying Libraries
Could produce type 2 errors: failing to identify a library that is actually there. Case: Where the library
name is slightly different. 

Could produce type 1 errors: identifying a library incorrectly. Case: for example
`/foundation/jQuery.min.js` would identify two libraries where only one was present. 


### SearchResultFinder

##### (1) Finding Pages

The parsing approach used is not elegant due to working with a giant string as the input. 
It also parses the input twice, looking for two tags: `class="r"` and `class="add_cclk"`.

### JavaScriptFinder

##### (1) Quick and Dirty way to find Libraries

As a result of the incomplete `JavascriptLibraryDatabase` some libraries could be missed. 

Also, `JavaScriptFinder` searches backwards from the first instance of `.js` to the first quote character. Only
that substring is evaluated against the records in `JavascriptLibraryDatabase`. This allows for libraries to be missed.
For example, angular pages will feature `ng-` quite a lot. Knockout uses the global variable `ko`. There is much
deeper analysis that can be performed on a page. 


### HttpRequest

##### (1) Representation of Data in Successful Response

When `HttpUrlConnection` receives a `200` response. `mResult` should be processed using the third party library `Jsoup`. 
Sticking with the spirit of the exercise (i.e. JDK-only being the ideal solution), the internal `DocumentBuilder` could 
be used instead. Approach not used as DocumentBuilder is pedantic about values in xml source.

List of Characters DocumentBuilder needs to escape:
https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents

##### (2) HttpURLRequest

Preferred implementation is to use `unirest` usually. However given purpose is to parse HTML, `jsoup` 
is the better approach. 

jsoup allows quick retrieval of a page and then represents it as a Document object. For example:

```$xslt
Document doc = Jsoup.connect("some/url")
.data(
.userAgent()
.cookie()
.timeout()
.post();
```

### General

##### (1) URL/URI Usage

Unnecessary flicking between both URL and URI, vestige of tinkering to get Google search to work.

##### (2) Search Result Representation of HTML Page

Had some issues with GoogleSearch initially. Possibly tried too many times (whilst on my VPN)
and Google shut me down. 

I would have preferred (time and third party products permitting) to use:
`Jsoup` (third party) or `DocumentBuilder` (Java API).

Representing the Webpage this way would have made search and other tasks far easier. 
`Jsoup` would allow to select all elements with `class="r"` then within those select 
all the a href elements. 
 
##### (3) Unit Testing

Needs Unit Testing.
