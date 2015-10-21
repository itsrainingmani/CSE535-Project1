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
public class Posting {
    private int docId;
    private int tf;
    
    public Posting(){
        docId = 0;
        tf = 0;
    }
    
    public void setDocid(int doc){
        this.docId = doc;
    }
    
    public void setTf(int t){
        this.tf = t;
    }
    
    public int getDocId(){
        return this.docId;
    }
    
    public int getTf(){
        return this.tf;
    }
}
