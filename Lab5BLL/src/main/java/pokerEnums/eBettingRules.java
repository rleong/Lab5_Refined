package pokerEnums;

import java.util.HashMap;
import java.util.Map;
 
public enum eBettingRules {
   
    NoLimit (1, true){
        @Override
        public String toString() {
            return "No Limit";
        }
    },
   
    PotLimit(2, false){
        @Override
        public String toString() {
            return "Pot Limit";
        }
    };
   
    private int ruleNbr;
    private boolean bDefault = false;
    private static Map<Integer, eBettingRules> map = new HashMap<Integer, eBettingRules>();
   
    static {
        for (eBettingRules bettingRules : eBettingRules.values()) {
            map.put(bettingRules.ruleNbr, bettingRules);
        }
    }
   
     public static eBettingRules getBettingRules(int ruleNbr) {
            return map.get(ruleNbr);
        }
   
    private eBettingRules(final int ruleNbr, boolean bDefault){
        this.ruleNbr = ruleNbr;
        this.bDefault = bDefault;
    }
   
    public int getBettingRule(){
        return ruleNbr;
    }
   
    public boolean getDefault(){
        return this.bDefault;
    }
}