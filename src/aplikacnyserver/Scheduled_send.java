/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package aplikacnyserver;
import static aplikacnyserver.AplikacnyServer.create_sip_msg;
import java.net.*;
import java.io.*;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author ivana
 */
public class Scheduled_send extends TimerTask{
    private ArrayList<databaza_struct> sch_databaza;
    private InetAddress sch_ip;
    private int sch_port;
    private ArrayList<TItype> sch_info;
   
    //konstruktor s nastavenim parametrov
    public Scheduled_send(ArrayList<databaza_struct> databaza, InetAddress ip,int port, ArrayList<TItype> info) {
        sch_databaza = databaza;
        sch_ip = ip;
        sch_port = port;
        sch_info = info;
    }
   
    //public String check_info(Iterator it, TItype next_info, databaza_struct next, String region) {
   public String check_info(ArrayList<TItype> sch_inf, databaza_struct next, String region) {
        String text_spravy = "";
        Iterator it = sch_inf.iterator();
       
        TItype next_info = new TItype();
        System.out.println("check_info");
        System.out.println("0: "+ sch_inf.get(0).region);
        //System.out.println("1: " + sch_inf.get(1).region);
       
        while (it.hasNext()) {
            next_info = (TItype) it.next();
            System.out.println("ziadany region: " + region);
            System.out.println("next_info.region v info: " + next_info.region);
           
            if (next_info.region.equals(region)) {
                //zaznam o regione
                //System.out.println("region, next.act= " + next.act);
                System.out.println("typ="+ next_info.type);
                String typ = "";
                typ = next_info.type.name();
                System.out.println("typ v enum: "+ typ);
                if (next.act == 1) {
                    System.out.println("next.act = 1");
                    if (typ.matches(".*Actual.*")) {
                        System.out.println("equals");
                    //user ma nastavene act
                        System.out.println("next_info.name=" + next_info.name);
                        text_spravy = text_spravy + next_info.name + "\n" + next_info.description + "\n";
                    }
                }
                if ((next.stat == 1) && (next_info.type.name().matches(".*StaticST.*"))) {
                    //user ma nastavene stat
                        text_spravy = text_spravy + next_info.name + "\n" + next_info.description + "\n";
                }
                if ((next.mob == 1) && (next_info.type.name().matches(".*MobileST.*"))) {
                    //user ma nastavene act
                        text_spravy = text_spravy + next_info.name + "\n" + next_info.description + "\n";
                }
                if ((next.road == 1) && (next_info.type.name().matches(".*RoadCheck.*"))) {
                    //user ma nastavene act
                        text_spravy = text_spravy + next_info.name + "\n" + next_info.description + "\n";
                }
                if ((next.longt == 1) && (next_info.type.name().matches(".*LongTerm.*"))) {
                    //user ma nastavene act
                        text_spravy = text_spravy + next_info.name + "\n" + next_info.description + "\n";
                }
            }
        }
       
        return text_spravy;
    }
   
    public void run(){
        //todo posielanie informacii na zaklade parametrov
       
        String message;
        String text_spravy = "";
        String from_adr;
        try{
            System.out.println("AAAAAAAAAAAAAAAAAAAAA");
            //DatagramSocket ServerSocket = new DatagramSocket(22222);
            //System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBB");
            //while (true){
                Iterator e = sch_databaza.iterator();
                //databaza_struct next = new databaza_struct();
                //message = "";
                //from_adr = "";
                databaza_struct next;
                while (e.hasNext()){
                    System.out.println("CCCCCCCCCCCCCC");
                    next = (databaza_struct) e.next();
                    from_adr = next.adresa.split(";")[0];
                    System.out.println(from_adr);
                    //vyseknem adresu prijemcu
                    String adresat = from_adr.substring(1,(from_adr.length())-1);
                   
                    //check nastavenia usera v db
                    if (next.zs == 1) {
                        //zapadne Slovensko
                        System.out.println("user ma zs");
                        Iterator it = sch_info.iterator();
                        //TItype next_info;
                        //next_info = (TItype) it.next();
                        //System.out.println("next_info.region pred funkciou: " + next_info.region);
                        //prehladam info pre zs
                        text_spravy += "Zapadne Slovensko:\n";
                        text_spravy = text_spravy + check_info(sch_info, next, "ZSVK");
                        System.out.println("sprava: " + text_spravy);
                    }
                   
                    if (next.ss == 1) {
                        //zapadne Slovensko
                        System.out.println("ss ");
                        Iterator it = sch_info.iterator();
                        //TItype next_info;
                        //next_info = (TItype) it.next();
                        //prehladam info pre zs
                        text_spravy += "Stredne Slovensko:\n";
                        text_spravy = text_spravy + check_info(sch_info, next, "SSVK");
                        System.out.println("sprava: " + text_spravy);
                    }
                   
                    if (next.vs == 1) {
                        //zapadne Slovensko
                       
                        Iterator it = sch_info.iterator();
                        //TItype next_info;
                        //next_info = (TItype) it.next();
                        //prehladam info pre zs
                        text_spravy += "Vychodne Slovensko:\n";
                        text_spravy = text_spravy + check_info(sch_info, next, "VSVK");
                    }
                   
                    //posielam spravu
                    if (!text_spravy.equals("")) {
                        message = create_sip_msg(adresat, text_spravy);

                        byte[] sendData_bytes;
                        //System.out.println("posielam buu na adresu " + adresat);
                        sendData_bytes = message.getBytes();
                        DatagramSocket s = new DatagramSocket();
                        DatagramPacket sendPacket = new DatagramPacket(sendData_bytes, sendData_bytes.length, sch_ip, sch_port);
                        s.send(sendPacket);

                        s.close();
                    }
             //   }
            }
        }catch (IOException e2) {}
    }
}