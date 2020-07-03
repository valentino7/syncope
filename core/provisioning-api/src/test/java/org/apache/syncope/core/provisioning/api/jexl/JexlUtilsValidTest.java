package org.apache.syncope.core.provisioning.api.jexl;


import org.junit.jupiter.api.Test;
import org.junit.Assert;


public class JexlUtilsValidTest {


    public static String[] getExpressions() {

        String[] expressions = new String[]{"13 + 4 * 1 / 5.2", "invalid expression", null};

        return expressions;
//        return Arrays.asList(new Object[][] {
//                {"13 + 4 * 1 / 5.2", true},
//                {"invalid exp", false},
//                {null, false}
//        });

    }

    public static Boolean[] getExpected(){
        Boolean[] expecteds = new Boolean[]{true,false,false};
        return expecteds;
    }



    @Test
    public void isExpressionValid() {
        Boolean[] expecteds = getExpected();
        String[] expressions = getExpressions();
        for(int i=0; i!= expressions.length; i++){
            boolean result;
            try{
                result = JexlUtils.isExpressionValid(expressions[i]);
            }catch(Exception e){
                e.printStackTrace();
                result = false;
            }
            Assert.assertEquals(expecteds[i], result);
        }
    }
}
