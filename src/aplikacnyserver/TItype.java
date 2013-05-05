/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplikacnyserver;
import java.util.*;
/**
 *
 * @author lukas
 */
public class TItype {
        Date time;
        String region; // ZSVK,SSVK,VSVK,SVK
        String name;
        String description;
        RType type;
}

enum RType { //restriction type
        Actual,StaticST,MobileST,RoadCheck,LongTerm //ST= speed trap
    }