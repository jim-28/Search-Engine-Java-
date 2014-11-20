import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
	// Below is the directory that contains all the internet files
	htmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new directedGraph();				
    } // end of constructor2014
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
	/* WRITE SOME CODE HERE */
    	//Add vertices
    	internet.setVisited(url, true);
    	internet.addVertex(url);
    	internet.vertices.put(url,htmlParsing.getLinks(url));
    	//Add edges
    	Iterator<String> j = internet.getNeighbors(url).iterator();
    	while(j.hasNext()){
    		String edge = j.next(); 
    		internet.addEdge(url, edge);
    	}
    	//Put each word into wordIndex, make sure no same words are put in twice
    	Iterator<String> i = htmlParsing.getContent(url).iterator();
    	while(i.hasNext()){
    		String word = i.next();
    		    if(wordIndex.get(word)==null){
    		    	LinkedList<String> putUrl1 = new LinkedList<String>();
    		    	putUrl1.add(url);
    		    	wordIndex.put(word, putUrl1);
    		    	}
    		    //If the word already exists, add in the new URL to previous ones
    		    else{	
    		    	 LinkedList<String> previousVal = wordIndex.get(word);
    		    	 previousVal.add(url);
    		     	 wordIndex.put(word, previousVal);  		    		
    		    	}
    		}
    	//Depth-First Graph Traversal
    	for(String run:internet.getNeighbors(url)){
    		if(!internet.getVisited(run)){
    			traverseInternet(run);
    		}
    	}
	/* Hints
	   0) This should take about 50-70 lines of code (or less)
	   1) To parse the content of the url, call
	   htmlParsing.getContent(url), which returns a LinkedList of Strings 
	   containing all the words at the given url. Also call htmlParsing.getLinks(url).
	   and assign their results to a LinkedList of Strings.
	   2) To iterate over all elements of a LinkedList, use an Iterator,
	   as described in the text of the assignment
	   3) Refer to the description of the LinkedList methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods contains(String s), 
	   addLast(String s), iterator()
	   4) Refer to the description of the HashMap methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods containsKey(String s), 
	   get(String s), put(String s, LinkedList l).  
	*/
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() {
	/* WRITE YOUR CODE HERE */
    	//Initialize the values of all vertices
    	Iterator<String> i = internet.getVertices().iterator();
    	while(i.hasNext()){
    		String vertex = i.next();
    		internet.setPageRank(vertex, 1);
    	}
    			//Iterate page-rank calculations 100 times for more accurate page-rankings (convergence)
    	for(int y = 0; y<100; y++){
    		//Get all vertices again
    		Iterator<String> j = internet.getVertices().iterator();
    		while(j.hasNext()){
    			String vertex = j.next();
    	
    			double recursive = 0;
    			//Get all edges to each vertex
    			Iterator<String> k = internet.getEdgesInto(vertex).iterator();
    			while(k.hasNext()){
    				String edgeInto = k.next();
    				//implement PR(v)= calculations
    				recursive = recursive+(internet.getPageRank(edgeInto)/internet.getOutDegree(edgeInto));
    				internet.setPageRank(vertex, recursive);
    			}
    			double pr = 0.5+0.5*internet.getPageRank(vertex);
    			internet.setPageRank(vertex, pr);
    		}
    	}
    
    	//The following commented-out code is for DEBUGGING
    /*	Iterator<String> l = internet.getVertices().iterator();
    	while(l.hasNext()){
    		String vertex = l.next();
    	System.out.println(vertex+": "+internet.getPageRank(vertex));
    	}         */
 
    } // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
	/* WRITE YOUR CODE HERE */
    	
    	//	HOW TO MAKE BESTURL & COMPARE GLOBAL WITHIN THIS METHOD??????
    	String bestUrl = "";
    	double compare = -10;
    	//If the word is not in the wordIndex, it is not on any site
    	if(wordIndex.get(query)==null){
			return " ";
		}
    	
    	//Go through each vertex's page-rank. The highest page-rank with the query is returned. 
    	Iterator<String> i = internet.getVertices().iterator();
    	while(i.hasNext()){
    		String vertex = i.next();
    		//Make sure the URL has the query word: if vertex is in wordIndex.get(query)
    		if(wordIndex.get(query).contains(vertex)){
    			//compare page-rankings
    			if(internet.getPageRank(vertex)>compare){
    				compare = internet.getPageRank(vertex);
    				bestUrl = vertex;
    			}
    		}
    	}
	return bestUrl; // remove this
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{		
	searchEngine mySearchEngine = new searchEngine();
	// to debug your program, start with.
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
	
	// When your program is working on the small example, move on to
	// mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
	 System.out.println(mySearchEngine);
	
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main
}