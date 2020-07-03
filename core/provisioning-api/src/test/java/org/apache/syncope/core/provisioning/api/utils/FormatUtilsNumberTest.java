package org.apache.syncope.core.provisioning.api.utils;

import org.junit.jupiter.api.Test;
import java.util.Calendar;
import org.junit.Assert;

public class FormatUtilsNumberTest {


    
    public static Number[] getExpected(Long time){
        Number[] expecteds = new Number[]{Long.valueOf(time), null, null};
        return expecteds;
    }
    
    
    public static String[] getSources(Long time) {
        String[] pattern = new String[]{String.valueOf(time), null, "invalid"};
        return pattern;
    }

   
    public static String[] getPattern() {
        String[] pattern = new String[]{"###,###", "invalid", null};
        return pattern;
    }

    @Test
    public void parseNumber() {
        Long time = Calendar.getInstance().getTime().getTime();
        Number[] expecteds = getExpected(time);
        String[] sources = getSources(time);
        String[] patterns = getPattern();
        for(int i=0; i!= expecteds.length; i++){

            Number result;
            try{
                result = FormatUtils.parseNumber(sources[i], patterns[i]);
    //            System.out.println(result);
            }catch (Exception e){
                e.printStackTrace();
                result = null;
            }
            Assert.assertEquals(expecteds[i], result);
        }
    }
}
