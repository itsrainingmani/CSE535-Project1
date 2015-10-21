/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Index;

/**
 *
 * @author Manikandan
 */
public class TopK {
    private String termName;
    private int lengthOfPosting;
    
    public TopK(){
        termName = "";
        lengthOfPosting = 0;
    }
    
    public void setTerm(String t){
        termName = t;
    }
    
    public void setLength(int l){
        lengthOfPosting = l;
    }
    
    public String getTerm(){
        return termName;
    }
    
    public int getLength(){
        return lengthOfPosting;
    }
}
