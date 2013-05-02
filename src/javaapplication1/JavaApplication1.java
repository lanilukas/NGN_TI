/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;
import java.io.IOException;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.util.*;

/**
 *
 * @author lukas
 */

enum RType { //restriction type
        StaticST,MobileST,RoadCheck,LongTerm //ST= speed trap
    }


public class JavaApplication1 {

      /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        List<TItype> TI = new ArrayList<TItype>(); //traffic information

        Date d = new Date();
        
        //stiahnutie stranky do doc
        Document doc = Jsoup.connect("http://m.stellacentrum.sk/aktual.php")
                .timeout(3000)
                .ignoreHttpErrors(true)
                .get();
        String msg = doc.html().replace("<br />","$$$");
        doc = Jsoup.parse(msg);
        
        msg = doc.body().text().replace("$$$", "\n").toString();
        //parsovanie a ukladanie po jednom
//        TItype newTI = new TItype(d,"zs","d1","aa",RType.StaticST);
        TItype newTI = new TItype();
        newTI.name="";
        newTI.description="";
        newTI.region="";
        newTI.type=RType.StaticST;
        TI.add(newTI);
    }
}
