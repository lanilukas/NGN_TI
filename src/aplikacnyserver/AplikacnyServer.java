/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikacnyserver;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import org.jsoup.*;
import org.jsoup.nodes.Document;

/**
 *
 * @author ivana
 */


class databaza_struct
{
    public String adresa;
    public int poslany_vyber;
    public int zs;  //zapadne slovensko
    public int ss;  //stredne slovensko
    public int vs;  //vychodne slovensko
    public int act;  // aktualne obmedzenia
    public int stat;  //staticke radary
    public int mob;  // mobilne radary
    public int road;   //cestne kontroly
    public int longt;  //dlhodobe obmedzenia
}

public class AplikacnyServer {

    //public static databaza_struct[] databaza = new databaza_struct[10];
    public static ArrayList<databaza_struct> databaza = new ArrayList<databaza_struct>();
    public static ArrayList<TItype> info = new ArrayList<TItype>();
      
    public static boolean is_in_db(String zdroj) {
       
        Iterator e = databaza.iterator();
        databaza_struct next = new databaza_struct();
        String adr;
        while (e.hasNext()){
            next = (databaza_struct) e.next();
            adr = next.adresa;
            if (adr.equals(zdroj)) {
                return true;
            }

        }
        return false;
    }
   
    public static void delete_from_db(String zdroj){
        Iterator e = databaza.iterator();
        databaza_struct zaznam;
        String adr= "";
        while (e.hasNext()){
            zaznam = (databaza_struct) e.next();
            adr = zaznam.adresa;
            System.out.println("adr="+adr+"zdroj="+zdroj);
            if (adr.equals(zdroj)) {
                System.out.println("remove");
                e.remove();
            }

        }
       
       
    }
   
    public static String create_sip_msg(String to, String obsah) {
        //vytvori SIP spravu s danym obsahom a adresatom
        String info;
        info = "MESSAGE " + to + " SIP/2.0\r\n";
        info = info + "Via: SIP/2.0/UDP 10.0.2.15:5061\r\n";
        info = info + "From: \"ADMIN\" <sip:admin@open-ims.test>\r\n";
        info = info + "To: <" + to + ">\r\n";
        info = info + "Call-ID: 222222222\r\n";
        info = info + "CSeq: 20 MESSAGE\r\n";
        info = info + "Content-Type: text/plain\r\n";
        info = info + "Max-Forwards: 70\r\n";
        info = info + "User-Agent: UCT IMS Client\r\n";
        //info = info + "P-Preferred Identity: \"Alice\" <alice@open-ims.test>\n";
        info = info + "P-Access-Network-Info: IEEE-802.11a\r\n";
        info = info + "Content-Length: " + obsah.length() + "\r\n";
        info = info + "\r\n" + obsah + "\r\n";
        info = info + "\r\n";
        return info;
    }
   
    public static int challenge_sent(String from) {
        //zisti, ci userovi uz bola poslana ponuka
        //ak ano, zmeni priznak na 0
        Iterator e = databaza.iterator();
        databaza_struct next = new databaza_struct();
        String adr;
        int vyber;
        while (e.hasNext()){
            next = (databaza_struct) e.next();
            adr = next.adresa;
            vyber = next.poslany_vyber;
            if (adr.equals(from)) {
                if (vyber == 1) {
                    next.poslany_vyber = 0;         //  zmeni toto zaznam v db???
                    return 1;
                }
            }
        }
        return 0;
    }
   
    public static void update_par_db(String zdroj, String parameter) {
        //update db
        //userovi zdroj zmen parameter
        System.out.println("update db pre " + zdroj + "parameter: " + parameter);        Iterator e = databaza.iterator();
        databaza_struct next = new databaza_struct();
        String adr;
       
        while (e.hasNext()){
            next = (databaza_struct) e.next();
            adr = next.adresa;
            if (adr.equals(zdroj)) {
                //next.parameter = Integer.parseInt(parameter);
                if (parameter.equals("ZS")) {
                    next.zs = 1;
                }
                if (parameter.equals("SS")) {
                    next.ss = 1;
                }
                if (parameter.equals("VS")) {
                    next.vs = 1;
                }
                if (parameter.equals("1")) {
                    next.act = 1;
                }
                if (parameter.equals("2")) {
                    next.stat = 1;
                }
                if (parameter.equals("3")) {
                    next.mob = 1;
                }
                if (parameter.equals("4")) {
                    next.road = 1;
                }
                if (parameter.equals("5")) {
                    next.longt = 1;
                }
                return;
            }
        }
    }
   
    public static void vypis_db() {
        databaza_struct novy_zaznam = new databaza_struct();
        Iterator e=databaza.iterator();
        while(e.hasNext()) {
            novy_zaznam = (databaza_struct) e.next();
            System.out.println(novy_zaznam.adresa + ", zs:" + novy_zaznam.zs
                    + ", ss: " + novy_zaznam.ss + ", vs: " + novy_zaznam.vs
                    + ", act:: " + novy_zaznam.act + ", stat: " + novy_zaznam.stat
                    + ", mob: " + novy_zaznam.mob + ", road: " + novy_zaznam.road
                    + ", longt: " + novy_zaznam.longt);

        }
    }
   
    public static String aktualne_nastavenie(String zdroj) {
        String sprava = "Aktualne nastavenia:\n";
        databaza_struct novy_zaznam = new databaza_struct();
        Iterator e=databaza.iterator();
        while(e.hasNext()) {
            novy_zaznam = (databaza_struct) e.next();
            if (novy_zaznam.adresa.equals(zdroj)) {
                vypis_db();
                if (novy_zaznam.zs == 1) {
                    sprava = sprava + "Zapadne Slovensko\n";
                }
                if (novy_zaznam.ss == 1) {
                    sprava = sprava + "Stredne Slovensko\n";
                }
                if (novy_zaznam.vs == 1) {
                    sprava = sprava + "Vychodne Slovensko\n";
                }
                if (novy_zaznam.act == 1) {
                    sprava = sprava + "Aktualne obmedzenia\n";
                }
                if (novy_zaznam.stat == 1) {
                    sprava = sprava + "Staticke radary\n";
                }
                if (novy_zaznam.mob == 1) {
                    sprava = sprava + "Mobilne radary\n";
                }
                if (novy_zaznam.road == 1) {
                    sprava = sprava + "Cestne kontroly\n";
                }
                if (novy_zaznam.longt == 1) {
                    sprava = sprava + "Dlhodobe obmedzenia\n";
                }
                return sprava;
            }
        }
        return "";
    }
   
    public static void main(String[] args) throws SocketException {
        // TODO code application logic here
        try{
            DatagramSocket ServerSocket = new DatagramSocket(22222);
           
            String reg_sprava = new String();
            databaza_struct novy_zaznam;
           
            int timer_control = 0;
           
            /*
            //demo informacie
            TItype nove_info = new TItype();
            nove_info.region= "ZSVK";
            nove_info.name = "aktualne obmedzenie1";
            nove_info.description = "desc1";
            nove_info.type = RType.Actual;
           
            info.add(nove_info);
           
            TItype nove_info2 = new TItype();
            nove_info2.region = "SSVK";
            nove_info2.description= "desc2";
            nove_info2.name = "staticky radar";
            nove_info2.type = RType.StaticST;
           
            info.add(nove_info2);
            */
            info=(ArrayList<TItype>) ParseStella();
            while (true) {
               
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                ServerSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                //System.out.println("Received: " + sentence);
               
                InetAddress ip;
                ip = receivePacket.getAddress();
                int port;
                port = receivePacket.getPort();
               
                if (timer_control == 0){
                    Timer time = new Timer();
                    Scheduled_send scheduled_task = new Scheduled_send(databaza, ip, port, info);
                    time.schedule(scheduled_task, 0, 10000);
                    timer_control = 1;
                }
               
                //parsovanie do pola
                String[] pole_riadkov;
                pole_riadkov = sentence.split("\n");
               
                String[] riadok_spravy;
                String typ;
                String from="";
                String from_adr = "";
                String from_tag = "";
                String to = "";
                Integer j;
                String text_spravy = "";
                String content_type = "";
                String temp="";
                Integer index = 0;
                   
                riadok_spravy = pole_riadkov[0].split(" ");
                typ = riadok_spravy[0].trim();
                //System.out.println("typ:" + typ);
                //System.out.println("dlzka: " + typ.length());
                if (typ.equals("MESSAGE")) {
                    //System.out.println("riadok message");
                    //treba poslat spat 200OK,aby nespamoval
                    //prvy riadok prepis na SIP/2.0 200 OK
                   
                   
                   
                    for (int i=1; i <  pole_riadkov.length; i++){
                        riadok_spravy = pole_riadkov[i].split(" ");
                       
                        typ = riadok_spravy[0].trim();
                        //System.out.println("typ: " + typ);
                       
                        if (typ.equals("From:")) {
                            from = riadok_spravy[2].trim();
                            from_adr = (from.split(";"))[0];
                            if (!from_adr.equals(from)){
                                from_tag = (from.split(";"))[1];
                            }
                           
                        }
                       
                         if (typ.equals("To:")) {
                            to = riadok_spravy[1].trim();
                        }
                       
                        if (typ.equals("Content-Type:")) {
                            content_type = riadok_spravy[1].trim();
                      //       System.out.println("riadok "+i+"content: " + content_type);
                         }
                    }
                   
                    if (to.equals("<sip:admin@open-ims.test>")){
                        if (content_type.equals("text/plain")) {

                            String[] sendData = new String[pole_riadkov.length];
                            sendData = pole_riadkov;
                            sendData[0] = "SIP/2.0 200 OK\r\n";

                            //vytvorim novy socket na odpoved
                            String tmp_str="";
                            byte[] sendData_bytes;
                            for (int x=0; x < pole_riadkov.length; x++){
                                riadok_spravy = pole_riadkov[x].split(" ");
                                if(pole_riadkov[x].isEmpty()) break;
                                typ = riadok_spravy[0].trim();
                                if (typ.equals("Content-Length:")) {
                                    //System.out.println("content");
                                   
                                   
                                    String help_str = "";
                                    //for (int jj=0; jj<x+1; jj++) {
                                    //    if (jj==x) {
                                       tmp_str = tmp_str + "Content-Length: 0\r\n";
                                }else{
                                          
                                      tmp_str = tmp_str + sendData[x].trim() + "\r\n";
                                }
                            } //koniec kopirovania povodnej spravy
                                    tmp_str = tmp_str  + "\r\n";
                                    //System.out.println("TMP str:");
                                    //System.out.println(tmp_str);
                                    sendData_bytes = tmp_str.getBytes();
                                    //System.out.println("posielam bytes na ip:" + ip + ", port: " + port);

                                    //poslem spat udp datagram

                                    //DatagramSocket s = new DatagramSocket();
                                    DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                    ServerSocket.send(sendPacket);
                                    //System.out.println("poslane");
                                    //s.close();

                            //System.out.println("text/plain: ");
                           
                            //zistim obsah spravy
                            for (j=pole_riadkov.length; j > 0; j--) {
                                temp = pole_riadkov[j-1].trim();
                                if (temp.equals("")) {
                                        j = 0;
                                }
                                text_spravy = temp + text_spravy;

                            }
                            //System.out.println("text spravy: " + text_spravy);
                        //} //koniec message
                   
                        String tmp_add_subscribe = "";
                        if (text_spravy.length() > 3) {
                            tmp_add_subscribe = text_spravy.substring(0,3);
                                   //System.out.println("viac ako 3 dlzka");
                                   //System.out.println("tmp:" + tmp_add_subscribe);
                            if (tmp_add_subscribe.equals("ADD")) {
                                if (is_in_db(from_adr)) {
                                    //pridanie noveho parametra k sluzbe
                                    String[] prijate_pole;
                                    prijate_pole = text_spravy.split(" ");

                                    //hladanie klucovych slov
                                    //ZS VS SS 1 2 3
                                    for (int g=1; g <prijate_pole.length; g++) {
                                        if (prijate_pole[g].equals("ZS")) {
                                            update_par_db(from_adr,"ZS");
                                        }
                                        if (prijate_pole[g].equals("SS")) {
                                            update_par_db(from_adr,"SS");
                                        }
                                        if (prijate_pole[g].equals("VS")) {
                                            update_par_db(from_adr,"VS");
                                        }
                                        if (prijate_pole[g].equals("1")) {
                                            update_par_db(from_adr,"1");
                                        }
                                        if (prijate_pole[g].equals("2")) {
                                            update_par_db(from_adr,"2");
                                        }
                                        if (prijate_pole[g].equals("3")) {
                                            update_par_db(from_adr,"3");
                                        }
                                        if (prijate_pole[g].equals("4")) {
                                            update_par_db(from_adr,"4");
                                        }
                                        if (prijate_pole[g].equals("5")) {
                                            update_par_db(from_adr,"5");
                                        }
                                    }

                                    System.out.println("Stav db po upadate:");
                                    vypis_db();
                                    String adresat = from_adr.substring(1,(from_adr.length())-1);
                                    String message = "";
                                    String obsah_spravy = "";
                                    String stav = aktualne_nastavenie(from_adr);
                                    obsah_spravy = "Pridanie parametrov uspesne.\n" + stav;
                                    message = create_sip_msg(adresat, obsah_spravy);

                                    //byte[] sendData_bytes;

                                    sendData_bytes = message.getBytes();
                                    //DatagramSocket s = new DatagramSocket();
                                    //DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                    sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                   
                                    ServerSocket.send(sendPacket);
                                    //System.out.println("poslane potvrdenie add parametrov");

                                    //s.close();
                                }
                            }
                        }
                       
                        if (text_spravy.equals("SUBSCRIBE")) {
                                //user poslal SUBSCRIBE spravu
                                //pridat ho do databazy
                                //todo to iste na unsubscribe
                                //todo spravit databazu = pole:
                                // prvky: adresa, parameter sprav - nasleduje za spravou subscribe
                                // user posle subscribe, AS odpovie zoznamom lokalit ak user neudal aj lokalitu
                                // user si vyberie bud lokalitu alebo all
                            System.out.println("prisiel subscribe od "+ from);
                           
                            //zistim, ci tento zdroj uz nie je v databaze
                            if (!is_in_db(from_adr)) {
                               
                                //zapisem do db
                                novy_zaznam = new databaza_struct();
                                novy_zaznam.adresa = from_adr;
                                //novy_zaznam.parameter = 1;
                               
                               
                                System.out.println("Pridane do db: " + novy_zaznam.adresa);
                                System.out.println("Stav db:");
                                vypis_db();
                                                               
                                //TODO poslem klientovi spat spravu s moznostami
                               
                                //zatial iba info sprava
                                String message = "";
                                //vyseknem adresu prijemcu
                                String adresat = from_adr.substring(1,(from_adr.length())-1);
                                //System.out.println(from +  to);
                                //vrati string s celou spravou a obsahom v parametri
                                message = create_sip_msg(adresat, "Boli ste uspesne pripojeny k sluzbe :) \nVyber si kraj: \n"
                                        + "Zapadne Slovensko: ZS\nStredne Slovensko: SS\n"
                                        + "Vychodne Slovensko: VS\nAktualne obmedzenia: 1\nStaticke radary: 2\n"
                                        + "Mobilne radary: 3\nCestne kontroly: 4\nDlhodobe obmedzenia: 5\n"
                                        + "\nVZOR:\nZS 1 2\n(spravy o aktualnych obmedzeniach a statickych radaroch na zapadnom Slovensku)\n");
                               
                                //System.out.println("posielam ano");
                                //System.out.println(info);
                               
                                //byte[] sendData_bytes;
                                //System.out.println("from_adr; " + from_adr);
                                sendData_bytes = message.getBytes();
                                //DatagramSocket s = new DatagramSocket();
                                //DatagramPacket
                                        sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                ServerSocket.send(sendPacket);
                                //System.out.println("poslane vyber");
                                //s.close();
                               
                                //zapisem do db ze bol poslany vyber
                                novy_zaznam.poslany_vyber = 1;
                                databaza.add(novy_zaznam);
                               
                            } else {
                                /*
                                // klient uz je v databaze
                                //zatial iba info sprava
                                String message = "";
                                //vyseknem adresu prijemcu
                                String adresat = from_adr.substring(1,(from_adr.length())-1);
                                //System.out.println(from +  to);
                                //vrati string s celou spravou a obsahom v parametri
                                String stav = aktualne_nastavenie(from_adr);
                                String obsah_spravy = "Uz si prihlaseny.\n" + stav;
                                message = create_sip_msg(adresat, obsah_spravy);
                               
                                //System.out.println("posielam nie");
                                //System.out.println(info);
                               
                                byte[] sendData_bytes;
                                //System.out.println("from_adr; " + from_adr);
                                sendData_bytes = message.getBytes();
                                DatagramSocket s = new DatagramSocket();
                                DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                s.send(sendPacket);
                                System.out.println("poslane nie");
                                s.close();
                                */
                            }
                           
                            continue;
                        } else if (!text_spravy.equals("")){
                        //nie je to subscribe
                           
                            if (text_spravy.equals("UNSUBSCRIBE")) {
                           
                            System.out.println("prisiel unsubscribe od "+ from);
                           
                            //vymaz ho z databazy
                            delete_from_db(from_adr);
                            novy_zaznam = new databaza_struct();
                            Iterator e=databaza.iterator();
                            while(e.hasNext()) {
                                novy_zaznam = (databaza_struct) e.next();
                                System.out.println(novy_zaznam.adresa + ", ");

                            }
                           
                            //poslem spravu o odhlaseni
                            String message = "";
                                //vyseknem adresu prijemcu
                            String adresat = from_adr.substring(1,(from_adr.length())-1);
                            //System.out.println(from +  to);
                            //vrati string s celou spravou a obsahom v parametri
                            message = create_sip_msg(adresat, "Odhlasenie zo sluzby prebehlo uspesne.");
                            //byte[] sendData_bytes;
                            //System.out.println("from_adr; " + from_adr);
                            sendData_bytes = message.getBytes();
                            //DatagramSocket s = new DatagramSocket();
                            //DatagramPacket
                                    sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                            ServerSocket.send(sendPacket);
                            //System.out.println("poslane odhlasenie");
                            //s.close();
                           
                            continue;
                        } else {
                            
                            //rozparsovanie prijatej spravy - odpoved na ponuku
                            System.out.println("spracovavam odpoved na menu");
                            String[] prijate_pole;
                            prijate_pole = text_spravy.split(" ");


                                //najdem odosielatela v db
                                if (is_in_db(from_adr)) {
                                    //zistim ci mu bola poslana vyzva a odpoveda prvy krat
                                    //a ak ano, zmeni parameter pre daneho usera
                                    int prvy = 0;
                                    prvy = challenge_sent(from_adr);
                                    //System.out.println("je prvy: " + prvy + ", " + text_spravy);

                                    if (prvy ==1) {
                                        //user odpoveda prvy krat

                                        //hladanie klucovych slov
                                        //ZS VS SS 1 2 3
                                        for (int g=0; g <prijate_pole.length; g++) {
                                            if (prijate_pole[g].equals("ZS")) {
                                                update_par_db(from_adr,"ZS");
                                            }
                                            if (prijate_pole[g].equals("SS")) {
                                                update_par_db(from_adr,"SS");
                                            }
                                            if (prijate_pole[g].equals("VS")) {
                                                update_par_db(from_adr,"VS");
                                            }
                                            if (prijate_pole[g].equals("1")) {
                                                update_par_db(from_adr,"1");
                                            }
                                            if (prijate_pole[g].equals("2")) {
                                                update_par_db(from_adr,"2");
                                            }
                                            if (prijate_pole[g].equals("3")) {
                                                update_par_db(from_adr,"3");
                                            }
                                            if (prijate_pole[g].equals("4")) {
                                                update_par_db(from_adr,"4");
                                            }
                                            if (prijate_pole[g].equals("5")) {
                                                update_par_db(from_adr,"5");
                                            }
                                        //update parametru v db
                                        //update_par_db(from_adr, text_spravy);
                                        System.out.println("Stav db:");
                                        vypis_db();
                                       
                                    }
                                        String adresat = from_adr.substring(1,(from_adr.length())-1);
                                        String message = "";
                                        String stav = aktualne_nastavenie(from_adr);
                                        String obsah_spravy = "Nastavenie parametrov uspesne.\n" + stav;
                                        message = create_sip_msg(adresat, obsah_spravy);

                                        //byte[] sendData_bytes;

                                        sendData_bytes = message.getBytes();
                                        //DatagramSocket s = new DatagramSocket();
                                        //DatagramPacket
                                                sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                        ServerSocket.send(sendPacket);
                                        //System.out.println("poslane potvrdenie parametrov");
                                        //s.close();
                                }
                                    continue;
                            }
                            }
                        }  //koniec else
                       
                       
                       
                    }
               
                    }
                }
            }
           
            }catch (IOException e) {}
           
        }
    public static List ParseStella() throws IOException{
    // ZACIATOK ulozenie a parsovanie m.stellacentrum.sk/aktual.php do TI - Arraylistu 
        int i,a,ado,hp,dn,ck,mr,st,ddo,eol,eol2,zs,ss,vs;
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
                newTI = new TItype();
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
                    eol=msg.indexOf(s, zs);
                    newTI = new TItype();
                    newTI.name=msg.substring(zs, eol);
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
                    eol=msg.indexOf(s, ss);
                    newTI = new TItype();
                    newTI.name=msg.substring(ss, eol);
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
                    eol=msg.indexOf(s, vs);
                    newTI = new TItype();
                    newTI.name=msg.substring(vs, eol);
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
                    eol=msg.indexOf(s, zs);
                    newTI = new TItype();
                    newTI.name=msg.substring(zs, eol);
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
                    eol=msg.indexOf(s, ss);
                    newTI = new TItype();
                    newTI.name=msg.substring(ss, eol);
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
                    eol=msg.indexOf(s, vs);
                    newTI = new TItype();
                    newTI.name=msg.substring(vs, eol);
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
                    eol=msg.indexOf(s, zs);
                    newTI = new TItype();
                    newTI.name=msg.substring(zs, eol);
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
                    eol=msg.indexOf(s, ss);
                    newTI = new TItype();
                    newTI.name=msg.substring(ss, eol);
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
                    eol=msg.indexOf(s,vs);
                    newTI = new TItype();
                    newTI.name=msg.substring(vs, eol);
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
                    eol=msg.indexOf(s, zs);
                    newTI = new TItype();
                    newTI.name=msg.substring(zs, eol);
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
                    eol=msg.indexOf(s, ss);
                    newTI = new TItype();
                    newTI.name=msg.substring(ss, eol);
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
                    eol=msg.indexOf(s, vs);
                    newTI = new TItype();
                    newTI.name=msg.substring(vs, eol);
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
                    eol=msg.indexOf(s, zs);
                    eol2=msg.indexOf(s, eol+1);
                    newTI = new TItype();
                    newTI.name=msg.substring(zs, eol);
                    newTI.description=msg.substring(eol, eol2);
                    newTI.region="ZSVK";   
                    newTI.type=RType.LongTerm;
                    TI.add(newTI);        
                    zs=eol2+2;
                } while (zs < ss && zs < vs && zs < msg.length());
                ddo=zs;
            }
            //SSVK
            if(ss > -1 && ss < msg.length()){
                if (vs == -1)
                    vs=msg.length();                
                do{
                    s="\n";
                    eol=msg.indexOf(s, ss);
                    eol2=msg.indexOf(s, eol+1);
                    newTI = new TItype();
                    newTI.name=msg.substring(ss, eol);
                    newTI.description=msg.substring(eol, eol2);
                    newTI.region="SSVK";   
                    newTI.type=RType.LongTerm;
                    TI.add(newTI);        
                    ss=eol2+2;
                } while (ss < vs && ss < msg.length());
                ddo=ss;
            }
            //VSVK
            if(vs > -1 && vs < msg.length()){
            s="Východné Slovensko:";
            vs=vs+s.length()+1; // posun o riadok
                do{
                    s="\n";
                    eol=msg.indexOf(s, vs);
                    if (eol==-1)
                        break;
                    eol2=msg.indexOf(s, eol+1);
                    newTI = new TItype();
                    newTI.name=msg.substring(vs, eol);
                    newTI.description=msg.substring(eol, eol2);
                    newTI.region="VSVK";   
                    newTI.type=RType.LongTerm;
                    TI.add(newTI);        
                    vs=eol2+2;
                } while (vs < msg.length());
               ddo=vs;
            }
        }
        
        return TI;
        //s=".\n"
        //s="\n• "
    }
}
