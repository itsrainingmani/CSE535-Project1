/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Index;

import Index.Posting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
/**
 *
 * @author Manikandan
 */
public class DocIdIndex {
    public HashMap<String, LinkedList<Posting>> docIndex;

    public DocIdIndex(){
        docIndex = new HashMap<>();
    }

    public void addTerms(String term, LinkedList<Posting> postings){
        docIndex.put(term, postings);
    }
    
    public HashMap<String, LinkedList<Posting>> getIndex(){
        return docIndex;
    }
    
    public LinkedList<Posting> getPostings(String term){
        return docIndex.get(term);
    }
}