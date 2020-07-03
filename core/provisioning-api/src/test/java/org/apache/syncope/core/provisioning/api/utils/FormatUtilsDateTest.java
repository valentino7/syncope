package org.apache.syncope.core.provisioning.api.utils;


import org.junit.jupiter.api.Test;
import org.junit.Assert;
import java.text.SimpleDateFormat;
import java.util.*;


public class FormatUtilsDateTest {




    public static String[] getPattern() {

        String[] pattern = new String[]{"dd/MM/yyyy", null, "ddddddsd"};

        return pattern;
    }
    
    
    
    public static Boolean[] getLenient() {

        Boolean[] lenient = new Boolean[]{false, true, true};

        return lenient;

    }
    
    public static Date[] getDate() {

        Date[] date = new Date[]{Calendar.getInstance().getTime(), null, Calendar.getInstance().getTime()};

        return date;
//        return Arrays.asList(new Object[][] {
//                {"13 + 4 * 1 / 5.2", true},
//                {"invalid exp", false},
//                {null, false}
//        });

    }

    public static String[] getExpected(){
        String[] expecteds = new String[]{new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()),null,new SimpleDateFormat("ddddddsd").format(Calendar.getInstance().getTime())};
        return expecteds;
    }

    @Test
    public void format() {
        String[] expecteds = getExpected();
        Boolean[] lenients = getLenient();
        Date[] dates = getDate();
        String[] patterns = getPattern();
        for(int i=0; i!= expecteds.length; i++){
            String result;
            try{
                result= FormatUtils.format(dates[i], lenients[i], patterns[i]);
            }catch(Exception e){
                e.printStackTrace();
                result = null;
            }
            Assert.assertEquals(expecteds[i], result);
        }
    }

}
