/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikacnyserver;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.util.*;
import java.io.IOException;
/**
 *
 * @author lukas
 */
public class Parse {
    
    public void ParseStella() throws IOException{
    // ZACIATOK ulozenie a parsovanie m.stellacentrum.sk/aktual.php do TI - Arraylistu 
        int i,a,ado,hp,dn,ck,mr,st,ddo,eol,zs,ss,vs;
        String s;
        List<TItype> TI = new ArrayList<TItype>(); //traffic information

        Date d = new Date();
        
        //stiahnutie stranky m.stellacentrum.sk/aktual.php do doc
        Document doc = Jsoup.connect("http://m.stellacentrum.sk/aktual.php")
                .timeout(3000)
                .ignoreHttpErrors(true)
                .get();
        //zmena <br/> a <b/> na koce riadkov
        String msg = doc.html().replace("<br />","$$$");
        //msg = doc.html().replace("<p>","$$$");
        //msg = doc.html().replace("</b>","$$$");
        doc = Jsoup.parse(msg);
        msg = doc.body().text().replace("$$$", "\n").toString();
        
        //default hodnoty
        TItype newTI = new TItype();
        a=0;    //zaciatocnsa pozicia v subore 
        s="\n"; //hladany string
        //najdenie zmysluplneho zaciatku
        for(i=0;i<3;i++){
            a=msg.indexOf(s, a)+1;
        }
        //parsovanie a ukladanie po jednotlivych zaznamoch
        
        //zistit ktore sa tam nachadzaju
        //s="AKTUÁLNE DOPRAVNÉ OBMEDZENIA"
        s="AKTUÁLNE DOPRAVNÉ OBMEDZENIA";
        ado=msg.indexOf(s, a);
        //s="DOPRAVNÉ NEHODY"
        s="DOPRAVNÉ NEHODY";
        dn=msg.indexOf(s, a);
        //s="CESTNÉ KONTROLY"
        s="CESTNÉ KONTROLY";
        ck=msg.indexOf(s, a);
        //s="MERANIE RÝCHLOSTI"
        s="MERANIE RÝCHLOSTI";
        st=msg.indexOf(s, a);
        //s="MOBILNÉ RADARY"
        s="MOBILNÉ RADARY";
        mr=msg.indexOf(s, a);
        //s="DLHODOBÉ DOPRAVNÉ OBMEDZENIA"
        s="DLHODOBÉ DOPRAVNÉ OBMEDZENIA";
        ddo=msg.indexOf(s, a);
        
        if(ado>-1){
            ado=ado+s.length()+1; // posun o riadok
        
            //s="Hraničné priechody:"
            s="Hraničné priechody:";
            if((hp=msg.indexOf(s, ado))>-1){
                ado=ado+s.length()+1;
            }
            do{
                s="\n";
                eol=msg.indexOf(s, ado);
                
                newTI.name=msg.substring(ado, eol);
                newTI.description="";
                newTI.region="SVK";   
                newTI.type=RType.Actual;
                TI.add(newTI);        
                ado=eol+1;
            } while (ado < dn && ado < ck && ado < mr && ado < st && ado < ddo);
        }
            
        //s="DOPRAVNÉ NEHODY"
        s="DOPRAVNÉ NEHODY";    
        if(dn>-1){
            dn=dn+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, a);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, a);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, a);
            
            if(zs > -1 && zs < ck && zs < mr && zs < st && zs < ddo){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ado);

                    newTI.name=msg.substring(ado, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.Actual;
                    TI.add(newTI);        
                    ado=eol+1;
                } while (zs < ck && zs < mr && zs < st && zs < ddo);
            }
        }
            //s="CESTNÉ KONTROLY"
            //s="MERANIE RÝCHLOSTI"
            //s="MOBILNÉ RADARY"
            //s="MERANIE RÝCHLOSTI"
            //s=",\n"
            //s="DLHODOBÉ DOPRAVNÉ OBMEDZENIA"
            //s=".\n"
            //s="\n• "
            
            //geograficke regiony:
            //s="Západné Slovensko:"
            //s="Stredné Slovensko:"
            //s="Východné Slovensko:"
        
        
        //TItype newTI = new TItype();
        newTI.name="";
        newTI.description="";
        newTI.region="";
        newTI.type=RType.StaticST;
        TI.add(newTI);
    }
}
