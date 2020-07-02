package org.apache.syncope.core.provisioning.api.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertEquals;

@RunWith(Parameterized.class)
public class FormatUtilsDateTest {

    private Date date;
    private String conversionPattern;
    //leniente viene settato nella trasformazione del formato della data per essere più clemente nell uso del formato
    private boolean lenient;
    private String expected;

    @Parameterized.Parameters
    public static Collection<?> getParameter() {
        String result1 = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        //invalid pattern
        String result2 = new SimpleDateFormat("ddddddsd").format(Calendar.getInstance().getTime());


        return Arrays.asList(new Object[][] {
                {Calendar.getInstance().getTime(), false, "dd/MM/yyyy", result1  },
                {null, true, null, null  },
                {Calendar.getInstance().getTime(), true, "ddddddsd", result2  },

        });
    }


    public FormatUtilsDateTest(Date date, boolean lenient, String conversionPattern, String expected){
        this.date = date;
        this.lenient = lenient;
        this.conversionPattern = conversionPattern;
        this.expected = expected;
    }


    @Test
    public void format() {
        String result;
        try{
            result= FormatUtils.format(this.date, this.lenient, this.conversionPattern);
        }catch(Exception e){
            e.printStackTrace();
            result = null;
        }
        assertEquals(this.expected, result);

    }

}
