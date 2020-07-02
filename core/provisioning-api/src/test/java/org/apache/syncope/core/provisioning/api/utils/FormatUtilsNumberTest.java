package org.apache.syncope.core.provisioning.api.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(Parameterized.class)
public class FormatUtilsNumberTest {

    private String source;
    private String conversionPattern;
    private Number expected;

    @Parameterized.Parameters
    public static Collection<?> getParameter() {
        String source1 =  String.valueOf(Calendar.getInstance().getTime().getTime());
        String pattern1 = "###,###";
        String invalidPattern = "sgdfhd";
        String invalidSource = "sgdfhd";
        Number expected1 = Long.valueOf(source1);

        return Arrays.asList(new Object[][] {
                {source1, pattern1, expected1},
                {null, invalidPattern, null},
                {invalidSource, null, null}
        });
    }

    public FormatUtilsNumberTest(String source, String conversionPattern, Number expected){
        this.expected = expected;
        this.source = source;
        this.conversionPattern = conversionPattern;
    }

    @Test
    public void parseNumber() {
        Number result;
        try{
            result = FormatUtils.parseNumber(this.source, this.conversionPattern);
//            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
            result = null;
        }
        assertEquals(this.expected, result);
    }
}