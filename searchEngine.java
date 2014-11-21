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
    }
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    void traverseInternet(String url) throws Exception {
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
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.  
    */
    void computePageRanks() {
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
    */
    String getBestURL(String query) {
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
	
	// mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	// this is just for debugging purposes
	// System.out.println(mySearchEngine);
	
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