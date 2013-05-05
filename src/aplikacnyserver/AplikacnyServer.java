/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikacnyserver;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
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
    public int m1;  //moznost1 - hliadky
    public int m2;  //moznost2 - radary
    public int m3;  //moznost3 - prerabky
}

public class AplikacnyServer {

    //public static databaza_struct[] databaza = new databaza_struct[10];
    public static ArrayList<databaza_struct> databaza = new ArrayList<databaza_struct>();
   
      
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
        info = "MESSAGE " + to + " SIP/2.0\n";
        info = info + "Via: SIP/2.0/UDP 10.0.2.15:5061\n";
        info = info + "From: \"ADMIN\" <sip:admin@open-ims.test>\n";
        info = info + "To: <" + to + ">\n";
        info = info + "Call-ID: 222222222\n";
        info = info + "CSeq: 20 MESSAGE\n";
        info = info + "Content-Type: text/plain\n";
        info = info + "Max-Forwards: 70\n";
        info = info + "User-Agent: UCT IMS Client\n";
        //info = info + "P-Preferred Identity: \"Alice\" <alice@open-ims.test>\n";
        info = info + "P-Access-Network-Info: IEEE-802.11a\n";
        info = info + "Content-Length: " + obsah.length() + "\n";
        info = info + "\n" + obsah + "\n";
        info = info + "\n";
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
                if (parameter.equals("m1")) {
                    next.m1 = 1;
                }
                if (parameter.equals("m2")) {
                    next.m2 = 1;
                }
                if (parameter.equals("m3")) {
                    next.m3 = 1;
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
                    + ", m1: " + novy_zaznam.m1 + ", m2: " + novy_zaznam.m2
                    + ", m3: " + novy_zaznam.m3);

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
                if (novy_zaznam.m1 == 1) {
                    sprava = sprava + "Hliadky\n";
                }
                if (novy_zaznam.m2 == 1) {
                    sprava = sprava + "Radary\n";
                }
                if (novy_zaznam.m3 == 1) {
                    sprava = sprava + "Obmedzenia\n";
                }
                return sprava;
            }
        }
        return "";
    }
   
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            DatagramSocket ServerSocket = new DatagramSocket(22222);
           
            String reg_sprava = new String();
            databaza_struct novy_zaznam;
           
            while (true)
            {
               
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                ServerSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData());
                //System.out.println("Received: " + sentence);
               
                InetAddress ip;
                ip = receivePacket.getAddress();
                int port;
                port = receivePacket.getPort();
               
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
                            sendData[0] = "SIP/2.0 200 OK\n";

                            //vytvorim novy socket na odpoved
                            for (int x=0; x < pole_riadkov.length; x++){
                                riadok_spravy = pole_riadkov[x].split(" ");

                                typ = riadok_spravy[0].trim();
                                if (typ.equals("Content-Length:")) {
                                    //System.out.println("content");
                                    byte[] sendData_bytes;
                                    String tmp_str="";
                                    String help_str = "";
                                    for (int jj=0; jj<x+1; jj++) {
                                        if (jj==x) {
                                            tmp_str = tmp_str+ "Content-Length: 0\n";
                                        } else {
                                            help_str = sendData[jj].split(" ")[0];
                                            //System.out.println("help_str: "+ help_str);
                                            if (help_str.equals("Content-Type:")) {

                                            } else {
                                                if (help_str.equals("Max-Forwards:")) {

                                                } else {
                                                    if (help_str.equals("P-Access-Network-Info:")) {

                                                    } else{
                                                        if (help_str.equals("Route:")) {

                                                        } else{
                                                        //System.out.println(" else");
                                                            tmp_str = tmp_str + sendData[jj];
                                                        }
                                                    }
                                                }
                                            }
                                            //tmp_str = tmp_str + sendData[jj];
                                        }
                                    }
                                    tmp_str = tmp_str + "\n";
                                    //System.out.println("TMP str:");
                                    //System.out.println(tmp_str);
                                    sendData_bytes = tmp_str.getBytes();
                                    //System.out.println("posielam bytes na ip:" + ip + ", port: " + port);

                                    //poslem spat udp datagram

                                    DatagramSocket s = new DatagramSocket();
                                    DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                    s.send(sendPacket);
                                    //System.out.println("poslane");
                                    s.close();
                                }
                            }

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
                        }
                   
                        String tmp_add_subscribe = "";
                        if (text_spravy.length() > 3) {
                            tmp_add_subscribe = text_spravy.substring(0,3);
                                   System.out.println("viac ako 3 dlzka");
                                   System.out.println("tmp:" + tmp_add_subscribe);
;                            if (tmp_add_subscribe.equals("ADD")) {
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
                                            update_par_db(from_adr,"m1");
                                        }
                                        if (prijate_pole[g].equals("2")) {
                                            update_par_db(from_adr,"m2");
                                        }
                                        if (prijate_pole[g].equals("3")) {
                                            update_par_db(from_adr,"m3");
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

                                    byte[] sendData_bytes;

                                    sendData_bytes = message.getBytes();
                                    DatagramSocket s = new DatagramSocket();
                                    DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                    s.send(sendPacket);
                                    System.out.println("poslane potvrdenie add parametrov");

                                    s.close();
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
                                        + "Vychodne Slovensko: VS\nHliadky: 1\nRadary: 2\nObmedzenia: 3\n"
                                        + "\nVZOR:\nZS 1 2\n(spravy o hliadkach a radaroch na zapadnom Slovensku)\n");
                               
                                //System.out.println("posielam ano");
                                //System.out.println(info);
                               
                                byte[] sendData_bytes;
                                //System.out.println("from_adr; " + from_adr);
                                sendData_bytes = message.getBytes();
                                DatagramSocket s = new DatagramSocket();
                                DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                s.send(sendPacket);
                                System.out.println("poslane vyber");
                                s.close();
                               
                                //zapisem do db ze bol poslany vyber
                                novy_zaznam.poslany_vyber = 1;
                                databaza.add(novy_zaznam);
                               
                            } else {
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
                               
                            }
                           
                            continue;
                        } else if ((!text_spravy.equals("")) && (!text_spravy.substring(0,2).equals("ADD"))){
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
                            byte[] sendData_bytes;
                            //System.out.println("from_adr; " + from_adr);
                            sendData_bytes = message.getBytes();
                            DatagramSocket s = new DatagramSocket();
                            DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                            s.send(sendPacket);
                            System.out.println("poslane odhlasenie");
                            s.close();
                           
                            continue;
                        } else {
                           
                            //rozparsovanie prijatej spravy - odpoved na ponuku

                            String[] prijate_pole;
                            prijate_pole = text_spravy.split(" ");


                            //if (text_spravy.equals("1") || text_spravy.equals("2") || text_spravy.equals("3")) {
                                //asi prisla odpoved na vyzvu
                                //check, ci bola vyzva poslana

                                //najdem odosielatela v db
                                if (is_in_db(from_adr)) {
                                    //zistim ci mu bola poslana vyzva a odpoveda prvy krat
                                    //a ak ano, zmeni parameter pre daneho usera
                                    int prvy = 0;
                                    prvy = challenge_sent(from_adr);
                                    System.out.println("je prvy: " + prvy + ", " + text_spravy);

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
                                                update_par_db(from_adr,"m1");
                                            }
                                            if (prijate_pole[g].equals("2")) {
                                                update_par_db(from_adr,"m2");
                                            }
                                            if (prijate_pole[g].equals("3")) {
                                                update_par_db(from_adr,"m3");
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

                                        byte[] sendData_bytes;

                                        sendData_bytes = message.getBytes();
                                        DatagramSocket s = new DatagramSocket();
                                        DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, ip, port);
                                        s.send(sendPacket);
                                        System.out.println("poslane potvrdenie parametrov");
                                        s.close();
                                }
                                    continue;
                            }
                            }
                        }  //koniec else
                       
                       
                       
                    }
                }
                   
               
            }
            }catch (IOException e) {}
           
        }
    }