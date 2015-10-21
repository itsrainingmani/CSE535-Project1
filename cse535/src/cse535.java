/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Index.Posting;
import Index.TopK;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import Index.DocIdIndex;
import Index.TfIndex;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
/**
 *
 * @author Manikandan
 */
public class cse535 {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //String indexName = args[0];
        if (args.length < 4){
            System.out.println("Not enough arguments");
            return;
        }
        cse535 program = new cse535();
        program.start(args);
    }

    public void start(String[] args) throws IOException{
        
        String indexName = args[0];
        String outputName = args[1];
        int k = Integer.parseInt(args[2]);
        String queryFileName = args[3];

        DocIdIndex di = new DocIdIndex();
        TfIndex ti = new TfIndex();

        ArrayList<TopK> tk = new ArrayList<>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(indexName));
        }
        catch (FileNotFoundException e) {
            System.out.println("Input file " + indexName + " not found");
            System.exit(1);
        }
        String strline;
        //int lol = 0;
        while((strline = br.readLine()) != null){
            //if (lol > 10)
            //    break;
            TopK t = new TopK();
            String term = strline.substring(0, strline.indexOf('\\')); //System.out.println(term);
            int lengthOfPostings = Integer.parseInt(strline.substring(strline.indexOf('\\') + 2, strline.lastIndexOf('\\'))); //System.out.println(lengthOfPostings);

            t.setTerm(term);
            t.setLength(lengthOfPostings);
            tk.add(t);

            String postings = strline.substring(strline.indexOf('[') + 1, strline.indexOf(']')); //System.out.println(postings);
            String[] pList = postings.split(", ");
            LinkedList<Posting> docList = new LinkedList<>();
            LinkedList<Posting> tfList = new LinkedList<>();
            for (String pList1 : pList) {
                Posting pl = new Posting();
                String[] docAndTf = pList1.split("/");
                pl.setDocid(Integer.parseUnsignedInt(docAndTf[0]));
                pl.setTf(Integer.parseUnsignedInt(docAndTf[1]));
                docList.add(pl);
                tfList.add(pl);
            }
            Collections.sort(docList, new DocComp());
            di.addTerms(term, docList);
            Collections.sort(tfList, Collections.reverseOrder(new TfComp()));
            ti.addTerms(term, tfList);
            //lol++;
        }
        String[][] queries;
        queries = this.readQueryFile(queryFileName);
        int len = queries.length;
        //int[][] tInvIndex = new int[len][15000];
        /*
        for (int i = 0; i < len; i++){
            LinkedList<Posting> pl = new LinkedList<>();
            pl = ti.getIndex().get(queries[i]);
            for (int j = 0; j < pl.size(); j++){
                invIndex[i][pl.get(j).getDocId()] = 1;
            }
        }
        */
        //System.out.println(queries[0][1]);
        FileWriter fw = new FileWriter(outputName);
        BufferedWriter bw = new BufferedWriter(fw);
        this.getTopK(tk, k, bw);
        for (int i = 0; i < queries.length; i++){
            this.getPostings(queries[i], di.getIndex(), ti.getIndex(), bw);
        }
        for (int i = 0; i < queries.length; i++){
            /**
            for (int j = 0; j < queries[i].length; j++){
                LinkedList<Posting> dPl = new LinkedList<>();
                dPl = ti.getIndex().get(queries[i][j]);
                for (int l = 0; l < dPl.size(); l++){
                    tInvIndex[i][dPl.get(l).getDocId()] = 1;
                }
            }
            * */
            this.taatAnd(ti.getIndex(), queries[i], bw);
            this.daatAnd(di.getIndex(), queries[i], bw);
            this.taatOr(ti.getIndex(), queries[i], bw);
            this.daatOr(di.getIndex(), queries[i], bw);
        }
        br.close();
        bw.close();
    }

    public String[][] readQueryFile(String queryFile) throws FileNotFoundException, IOException{
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(queryFile));
        String strLine;
        int lineLength = 0;
        while((strLine = br.readLine()) != null){
            lineLength++;
        }
        br.close();
        BufferedReader vr = null;
        vr = new BufferedReader(new FileReader(queryFile));
        String[][] queries = new String[lineLength][];
        int len = 0;
        while((strLine = vr.readLine()) != null){
            queries[len] = strLine.split(" ");
            len++;
        }
        vr.close();
        return queries;
    }

    public void getTopK(ArrayList<TopK> tk, int N, BufferedWriter bw) throws IOException{
        bw.write("FUNCTION: getTopK " + N); bw.newLine();
        Collections.sort(tk, Collections.reverseOrder(new TopKComp()));
        for (int i =0; i < N; i++){
            //System.out.println(tk.get(i).getTerm());
            bw.write(tk.get(i).getTerm() + " ");
        }
        bw.newLine();
    }

    public void getPostings(String[] queryList, HashMap<String, LinkedList<Posting>> doc, HashMap<String, LinkedList<Posting>> tf, BufferedWriter bw) throws IOException{
        for (String queryList1 : queryList) {
            if (doc.containsKey(queryList1)){
                //System.out.println(queryList1);
                bw.write("FUNCTION: getPostings " + queryList1); bw.newLine();
                String d = ""; String t = "";
                LinkedList<Posting> al = doc.get(queryList1);
                for (Iterator<Posting> it = al.iterator(); it.hasNext();) {
                    int dId = it.next().getDocId();
                    d += Integer.toString(dId) + " ";
                }
                //System.out.println(d);
                bw.write("Ordered by doc ID's: " + d);
                bw.newLine();
                al = tf.get(queryList1);
                for (Iterator<Posting> it = al.iterator(); it.hasNext();) {
                    int tId = it.next().getTf();
                    t += Integer.toString(tId) + " ";
                }
                //System.out.println(t);
                bw.write("Ordered by TF's: " + t);
                bw.newLine();
            }
            else{
                //System.out.println("term not found");
                bw.write("Term not found");
                bw.newLine();
            }
        }
    }

    public void taatAnd(HashMap<String, LinkedList<Posting>> tf, String[] q, BufferedWriter bw) throws IOException{
        bw.write("FUNCTION: termAtATimeQueryAnd ");
        for (int i = 0; i < q.length; i++){
            if (!tf.containsKey(q[i])){
                System.out.println("Terms not found");
                return;
            }
            else{
                bw.write(q[i] + " ");
            }
        }
        int numComparisons = 0;
        ArrayList<TopK> tk = new ArrayList<>();
        for (int i = 0; i < q.length; i++){
            TopK t = new TopK();
            t.setTerm(q[i]);
            t.setLength(tf.get(q[i]).size());
            tk.add(t);
        }
        String query = "";
        Collections.sort(tk, Collections.reverseOrder(new TopKComp()));
        if (tk.size() == 1){
            String rez = "";
            LinkedList<Posting> result = new LinkedList<>();
            result = tf.get(tk.get(0).getTerm());
            for (int i = 0; i < result.size(); i++){
                rez += Integer.toString(result.get(i).getDocId()) + " ";
            }
            bw.newLine(); bw.write(result.size() + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write("0 seconds are used");
            bw.newLine();
            bw.write("Result: " + rez);
            bw.newLine();
            return;
        }
        String term0 = tk.get(0).getTerm();
        LinkedList<Posting> result = new LinkedList<>();
        result = tf.get(term0);
        long begin = System.nanoTime();
        for (int i = 1; i < q.length; i++){
            String term = tk.get(i).getTerm();
            query += term + " ";
            LinkedList<Posting> temp = new LinkedList<>();
            temp = tf.get(term);
            Boolean bFlag = false;
            for (int j = 0; j < result.size(); j++){
                for (int k = 0; k < temp.size(); k++){
                    if (Objects.equals(result.get(j).getDocId(), temp.get(k).getDocId())){
                        numComparisons++;
                        bFlag = true;
                        break;
                    }
                }
                if (bFlag == false){
                    result.remove(j);
                }
            }
        }
        long end = System.nanoTime() - begin;
        double seconds = (double)end / 1E9;
        Collections.sort(result, new DocComp());
        String rez = "";
        for (int i = 0; i < result.size(); i++){
            rez += Integer.toString(result.get(i).getDocId()) + ", ";
        }
        bw.newLine(); bw.write(result.size() + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write(seconds + " seconds are used");
        bw.newLine();
        bw.write("Result: " + rez); bw.newLine();
    }

    public void taatOr(HashMap<String, LinkedList<Posting>> tf, String[] q, BufferedWriter bw) throws IOException{
        int numComparisons = 0;
        bw.write("FUNCTION: termAtATimeQueryOr ");

        ArrayList<TopK> tk = new ArrayList<>();
        for (int i = 0; i < q.length; i++){
            if(!tf.containsKey(q[i]))
                continue;
            TopK t = new TopK();
            t.setTerm(q[i]); //System.out.println(t.getTerm());
            t.setLength(tf.get(q[i]).size()); //System.out.println(t.getLength());
            tk.add(t);
            bw.write(q[i] + " ");
        }
        Collections.sort(tk, Collections.reverseOrder(new TopKComp()));
        if (tk.size() == 0){
            bw.write("terms not found"); bw.newLine();
            return;
        }
        else if (tk.size() == 1){
            String rez = "";
            LinkedList<Posting> result = new LinkedList<>();
            result = tf.get(tk.get(0).getTerm());
            for (int i = 0; i < result.size(); i++){
                rez += Integer.toString(result.get(i).getDocId()) + " ";
            }
            bw.newLine(); bw.write(result.size() + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write("0 seconds are used");
            bw.newLine();
            bw.write("Result: " + rez);
            bw.newLine();
            return;
        }
        String term0 = tk.get(0).getTerm(); //System.out.println(term0);
        LinkedList<Posting> result = new LinkedList<>();
        result = tf.get(term0);
        long begin = System.nanoTime();
        for (int i = 1; i < q.length; i++){
            String term = tk.get(i).getTerm();
            LinkedList<Posting> temp = new LinkedList<>();
            temp = tf.get(term);
            Boolean bFlag = false;
            int index = 0;
            for (int j = 0; j < temp.size(); j++){
                for (int k = 0; k < result.size(); k++){
                    numComparisons++;
                    if (Objects.equals(result.get(k).getDocId(), temp.get(j).getDocId())){
                        bFlag = true;
                        break;
                    }
                }
                if (bFlag == false){
                    result.add(temp.get(j));
                }
            }
        }
        long end = System.nanoTime() - begin;
        double seconds = (double)end / 1E9;
        Collections.sort(result, new DocComp());
        String rez = "";
        for (int i = 0; i < result.size(); i++){
            rez += Integer.toString(result.get(i).getDocId()) + " ";
        }
        bw.newLine(); bw.write(result.size() + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write(seconds + " seconds are used");
        bw.newLine();
        bw.write("Result: " + rez);
        bw.newLine();
    }

    public void taatAnd1(int[][] invIndex, int len, BufferedWriter bw){
        if (len == 1){
            String rez = "";
            for (int i = 9500; i < 15000; i++){
                if (invIndex[0][i] == 1)
                    rez += Integer.toString(i) + " ";
            }
            System.out.println("TAAT AND1 - " + rez);
            return;
        }
        int[] bIndex = new int[15000];
        bIndex = invIndex[0];
        for (int i = 1; i < len; i++){
            for (int j = 9500; j < 15000; j++){
                if ((bIndex[j] == 1) && (invIndex[i][j] == 1))
                    bIndex[j] = 1;
                else
                    bIndex[j] = 0;
            }
        }
        int num = 0;
        String rez = "";
        for (int i = 9500; i < 15000; i++){
            if (bIndex[i] == 1){
                rez += Integer.toString(i) + " ";
                num++;
            }
        }
        System.out.println(num);
        System.out.println("TAAT AND1 - " + rez);
    }

    public void taatOr1(int[][] invIndex, int len, BufferedWriter bw){
        if (len == 1){
            String rez = "";
            for (int i = 9500; i < 15000; i++){
                if (invIndex[0][i] == 1)
                    rez += Integer.toString(i) + " ";
            }
            System.out.println("TAAT OR1 - " + rez);
            return;
        }
        int[] bIndex = new int[15000];
        bIndex = invIndex[0];
        for (int i = 0; i < len; i++){
            for (int j = 9500; j < 15000; j++){
                if ((bIndex[j] == 1) || (invIndex[i][j] == 1))
                    bIndex[j] = 1;
                else
                    bIndex[j] = 0;
            }
        }
        int num = 0;
        String rez = "";
        for (int i = 9500; i < 15000; i++){
            if (bIndex[i] == 1){
                rez += Integer.toString(i) + " ";
                num++;
            }
        }
        System.out.println(num);
        System.out.println("TAAT OR1 - " + rez);
    }

    public void daatAnd(HashMap<String, LinkedList<Posting>> doc, String[] q, BufferedWriter bw) throws IOException{
        bw.write("FUNCTION: documentAtATimeQueryAnd ");
        int numComparisons = 0;
        int len = q.length;
        for (int i = 0; i < q.length; i++){
            if (!doc.containsKey(q[i])){
                bw.write("Terms not found");
                return;
            }
            bw.write(q[i] + " ");
        }
        long begin = System.nanoTime();
        int[][] invIndex = new int[len][15000];
        for (int i = 0; i < len; i++){
            LinkedList<Posting> pl = new LinkedList<>();
            pl = doc.get(q[i]);
            for (int j = 0; j < pl.size(); j++){
                invIndex[i][pl.get(j).getDocId()] = 1;
            }
        }

        int[] bIndex = new int[15000];
        for (int i = 9500; i < 15000; i++){
            for (int j = 0; j < len; j++){
                if (invIndex[j][i] == 1){
                    bIndex[i]++;
                    numComparisons++;
                }
            }
        }
        
        long end = System.nanoTime() - begin;
        int num = 0;
        String rez = "";
        for (int i = 9500; i < 15000; i++){
            if (bIndex[i] == len){
                rez += Integer.toString(i) + " ";
                num++;
            }
        }
        double seconds = (double)end / 1E9;
        bw.newLine(); bw.write(num + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write(seconds + " seconds are used");
        bw.newLine();
        bw.write("Result: " + rez);
        bw.newLine();
    }

    public void daatOr(HashMap<String, LinkedList<Posting>> doc, String[] q, BufferedWriter bw) throws IOException{
        bw.write("FUNCTION: documentAtATimeQueryOr ");
        int numComparisons = 0;
        int len = q.length;
        int[][] invIndex = new int[len][15000];
        long begin = System.nanoTime();
        for (int i = 0; i < len; i++){
            if (!doc.containsKey(q[i]))
                continue;
            LinkedList<Posting> pl = new LinkedList<>();
            pl = doc.get(q[i]);
            bw.write(q[i] + " ");
            for (int j = 0; j < pl.size(); j++){
                invIndex[i][pl.get(j).getDocId()] = 1;
            }
        }

        int[] bIndex = new int[15000];
        for (int i = 9500; i < 15000; i++){
            for (int j = 0; j < len; j++){
                if (invIndex[j][i] == 1){
                    numComparisons++;
                    bIndex[i]++;
                }
            }
        }
        long end = System.nanoTime() - begin;
        double seconds = (double)end / 1E9;
        String rez = "";
        int num = 0;
        for (int i = 9500; i < 15000; i++){
            if (bIndex[i] > 0){
                rez += Integer.toString(i) + " ";
                num++;
            }
        }
        bw.newLine(); bw.write(num + " documents are found"); bw.newLine(); bw.write(numComparisons + " comparisons are made"); bw.newLine(); bw.write(seconds + " seconds are used");
        bw.newLine();
        bw.write("Result: " + rez);
        bw.newLine();
    }

    public int[] unionArrays(int[]... arrays){
        int maxSize = 0;
        int counter = 0;

        for(int[] array : arrays) maxSize += array.length;
        int[] accumulator = new int[maxSize];

        for(int[] array : arrays)
            for(int i : array)
                if(!isDuplicated(accumulator, counter, i))
                    accumulator[counter++] = i;

        int[] result = new int[counter];
        for(int i = 0; i < counter; i++) result[i] = accumulator[i];

        return result;
    }

    public boolean isDuplicated(int[] array, int counter, int value){
        for(int i = 0; i < counter; i++) if(array[i] == value) return true;
        return false;
    }

}

class TfComp implements Comparator<Posting>{
    @Override
    public int compare(Posting doc1, Posting doc2) {
        int tf1 = doc1.getTf();
        int tf2 = doc2.getTf();
        if (tf1 == tf2)
            return 0;
        else if (tf1 > tf2)
            return 1;
        else
            return -1;
    }
}

class DocComp implements Comparator<Posting>{
    @Override
    public int compare(Posting doc1, Posting doc2) {
        int tf1 = doc1.getDocId();
        int tf2 = doc2.getDocId();
        if (tf1 == tf2)
            return 0;
        else if (tf1 > tf2)
            return 1;
        else
            return -1;
    }
}

class TopKComp implements Comparator<TopK>{
    @Override
    public int compare(TopK t1, TopK t2) {
        int tf1 = t1.getLength();
        int tf2 = t2.getLength();
        if (tf1 == tf2)
            return 0;
        else if (tf1 > tf2)
            return 1;
        else
            return -1;
    }
}

class LlComp implements Comparator<ArrayList<Integer>>{
    @Override
    public int compare(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        int tf1 = l1.size();
        int tf2 = l2.size();
        if (tf1 == tf2)
            return 0;
        else if (tf1 > tf2)
            return 1;
        else
            return -1;
    }
}
