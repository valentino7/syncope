package org.apache.syncope.core.provisioning.api.jexl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class JexlUtilsValidTest {
    private boolean expected;
    private String expression;


    @Parameterized.Parameters
    public static Collection<?> getParameter() {


        return Arrays.asList(new Object[][] {
                {"13 + 4 * 1 / 5.2", true},
                {"invalid exp", false},
                {null, false}
        });

    }



    public JexlUtilsValidTest(String expression, boolean expected){
        this.expected = expected;
        this.expression = expression;
    }

    @Test
    public void isExpressionValid() {
        boolean result;
        try{
            result = JexlUtils.isExpressionValid(this.expression);
        }catch(Exception e){
            e.printStackTrace();
            result = false;
        }
        assertEquals(this.expected, result);
    }
}
