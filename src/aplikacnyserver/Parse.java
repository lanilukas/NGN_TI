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
        
        //s="AKTUÁLNE DOPRAVNÉ OBMEDZENIA"
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
            } while ((ado < dn || dn==-1) && (ado < ck || ck==-1) && (ado < mr || mr==-1) && (ado < st || st==-1) && (ado < ddo|| ddo==-1));
        }
            
        //s="DOPRAVNÉ NEHODY"
        s="DOPRAVNÉ NEHODY";    
        if(dn>-1){
            dn=dn+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, dn);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, dn);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, dn);
            //ZSVK
            if(zs > -1 && (zs < ck || ck==-1) && (zs < mr || mr==-1) && (zs < st || st==-1) && (zs < ddo|| ddo==-1)){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, dn);

                    newTI.name=msg.substring(dn, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.Actual;
                    TI.add(newTI);        
                    zs=eol+1;
                } while (zs < ss && zs < vs && (zs < ck|| ck==-1) && (zs < mr || mr==-1) && (zs < st || st==-1) && (zs < ddo|| ddo==-1));
                dn=zs;
            }
            //SSVK
            if(ss > -1 && (ss < ck|| ck==-1) && (ss < mr || mr==-1) && (ss < st || st==-1) && (ss < ddo || ddo==-1)){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, dn);

                    newTI.name=msg.substring(dn, eol);
                    newTI.description="";
                    newTI.region="SSVK";   
                    newTI.type=RType.Actual;
                    TI.add(newTI);        
                    ss=eol+1;
                } while (ss < vs && (ss < ck || ck==-1) && (ss < mr || mr==-1) && (ss < st || st==-1) && (ss < ddo|| ddo==-1));
                dn=ss;
            }
            //VSVK
            if(vs > -1 && (vs < ck || ck==-1) && (vs < mr || mr==-1) && (vs < st || st==-1) && (vs < ddo || ddo==-1)){
                do{
                    s="\n";
                    eol=msg.indexOf(s, dn);

                    newTI.name=msg.substring(dn, eol);
                    newTI.description="";
                    newTI.region="VSVK";   
                    newTI.type=RType.Actual;
                    TI.add(newTI);        
                    vs=eol+1;
                } while ((vs < ck || ck==-1) && (vs < mr || mr==-1) && (vs < st || st==-1) && (vs < ddo|| ddo==-1));
               dn=vs;
            }
        }
        //s="CESTNÉ KONTROLY"
        s="CESTNÉ KONTROLY";    
        if(ck>-1){
            ck=ck+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, ck);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, ck);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, ck);
            //ZSVK
            if(zs > -1 && (zs < mr|| mr==-1) && (zs < st|| st==-1) && (zs < ddo|| ddo==-1)){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ck);

                    newTI.name=msg.substring(ck, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    zs=eol+1;
                } while (zs < ss && zs < vs && (zs < mr || mr==-1) && (zs < st || st==-1)&& (zs < ddo|| ddo==-1));
                ck=zs;
            }
            //SSVK
            if(ss > -1 && (ss < mr || mr==-1) && (ss < st || st==-1) && (ss < ddo || ddo==-1)){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ck);

                    newTI.name=msg.substring(ck, eol);
                    newTI.description="";
                    newTI.region="SSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    ss=eol+1;
                } while (ss < vs && (ss < mr || mr==-1) && (ss < st || st==-1) && (ss < ddo || ddo==-1));
                ck=ss;
            }
            //VSVK
            if(vs > -1 && (vs < mr || mr==-1) && (vs < st || st==-1) && (vs < ddo || ddo==-1)){
                do{
                    s="\n";
                    eol=msg.indexOf(s, ck);

                    newTI.name=msg.substring(ck, eol);
                    newTI.description="";
                    newTI.region="VSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    vs=eol+1;
                } while ((vs < mr || mr==-1) && (vs < st || st==-1) && (vs < ddo || ddo==-1));
               ck=vs;
            }
        }        
        //s="MERANIE RÝCHLOSTI"
        s="MERANIE RÝCHLOSTI";    
        if(mr>-1){
            mr=mr+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, mr);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, mr);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, mr);
            //ZSVK
            if(zs > -1 && (zs < st || st==-1) && (zs < ddo || ddo==-1)){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, mr);

                    newTI.name=msg.substring(mr, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.StaticST;
                    TI.add(newTI);        
                    zs=eol+1;
                } while (zs < ss && zs < vs && (zs < st || st==-1) && (zs < ddo || ddo==-1));
                mr=zs;
            }
            //SSVK
            if(ss > -1 && (ss < st || st==-1) && (ss < ddo || ddo==-1)){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, mr);

                    newTI.name=msg.substring(mr, eol);
                    newTI.description="";
                    newTI.region="SSVK";   
                    newTI.type=RType.StaticST;
                    TI.add(newTI);        
                    ss=eol+1;
                } while (ss < vs && (ss < st || st==-1) && (ss < ddo || ddo==-1));
                mr=ss;
            }
            //VSVK
            if(vs > -1 && (vs < st || st==-1) && (vs < ddo || ddo==-1)){
                do{
                    s="\n";
                    eol=msg.indexOf(s, mr);

                    newTI.name=msg.substring(mr, eol);
                    newTI.description="";
                    newTI.region="VSVK";   
                    newTI.type=RType.StaticST;
                    TI.add(newTI);        
                    vs=eol+1;
                } while ((vs < st || st==-1) && (vs < ddo || ddo==-1));
               mr=vs;
            }
        }
        //s="MOBILNÉ RADARY"
        s="MOBILNÉ RADARY";    
        if(st>-1){
            st=st+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, st);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, st);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, st);
            //ZSVK
            if(zs > -1 && (zs < ddo || ddo==-1)){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, st);

                    newTI.name=msg.substring(st, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.MobileST;
                    TI.add(newTI);        
                    zs=eol+1;
                } while (zs < ss && zs < vs && (zs < ddo || dn==-1));
                st=zs;
            }
            //SSVK
            if(ss > -1 && (ss < ddo || ddo==-1)){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, st);

                    newTI.name=msg.substring(st, eol);
                    newTI.description="";
                    newTI.region="SSVK";   
                    newTI.type=RType.MobileST;
                    TI.add(newTI);        
                    ss=eol+1;
                } while (ss < vs && (ss < ddo || ddo==-1));
                st=ss;
            }
            //VSVK
            if(vs > -1 && (vs < ddo || ddo==-1)){
                do{
                    s="\n";
                    eol=msg.indexOf(s, st);

                    newTI.name=msg.substring(st, eol);
                    newTI.description="";
                    newTI.region="VSVK";   
                    newTI.type=RType.MobileST;
                    TI.add(newTI);        
                    vs=eol+1;
                } while (vs < ddo || ddo==-1);
               st=vs;
            }
        }
        //s=",\n"
        
        //s="DLHODOBÉ DOPRAVNÉ OBMEDZENIA"
        s="DLHODOBÉ DOPRAVNÉ OBMEDZENIA";    
        if(ddo>-1){
            ddo=ddo+s.length()+1; // posun o riadok
        
            //s="Západné Slovensko:"
            s="Západné Slovensko:";
            zs=msg.indexOf(s, ddo);
            //s="Stredné Slovensko:"
            s="Stredné Slovensko:";
            ss=msg.indexOf(s, ddo);
            //s="Východné Slovensko:"
            s="Východné Slovensko:";
            vs=msg.indexOf(s, ddo);
            //ZSVK
            if(zs > -1 && zs < msg.length()){
                if (ss == -1)
                    ss=msg.length();
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ddo);

                    newTI.name=msg.substring(ddo, eol);
                    newTI.description="";
                    newTI.region="ZSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    zs=eol+1;
                } while (zs < ss && zs < vs && zs < msg.length());
                ddo=zs;
            }
            //SSVK
            if(ss > -1 && ss < msg.length()){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ddo);

                    newTI.name=msg.substring(ddo, eol);
                    newTI.description="";
                    newTI.region="SSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    ss=eol+1;
                } while (ss < vs && ss < msg.length());
                ddo=ss;
            }
            //VSVK
            if(vs > -1 && vs < msg.length()){
                do{
                    s="\n";
                    eol=msg.indexOf(s, ddo);

                    newTI.name=msg.substring(ddo, eol);
                    newTI.description="";
                    newTI.region="VSVK";   
                    newTI.type=RType.RoadCheck;
                    TI.add(newTI);        
                    vs=eol+1;
                } while (vs < msg.length());
               ddo=vs;
            }
        }
        
        //s=".\n"
        //s="\n• "
    }
}
