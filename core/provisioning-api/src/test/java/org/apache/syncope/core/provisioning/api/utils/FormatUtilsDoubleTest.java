package org.apache.syncope.core.provisioning.api.utils;

import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.provisioning.api.AbstractTest;
import org.junit.jupiter.api.Test;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(Parameterized.class)
public class FormatUtilsDoubleTest extends AbstractTest {

    private final Calendar calendar = Calendar.getInstance();
    private final Date date = calendar.getTime();
    private String conversionPattern;

    public FormatUtilsDoubleTest(){

    }


//    @Parameterized.Parameters
//    public static Collection<?> getParameter() throws IOException {
//
//        return Arrays.asList(new Object[][] {
//                {null, false, "dd/MM/yyyy", true},
//                {null, false, null, false},
//        });
//    }

    @Test
    public void format() {
        assertEquals(new SimpleDateFormat(SyncopeConstants.DEFAULT_DATE_PATTERN).
                        format(date), FormatUtils.format(date));

        conversionPattern = "dd/MM/yyyy";
        assertEquals(new SimpleDateFormat(conversionPattern).format(date),
                FormatUtils.format(date, false, conversionPattern));
    }




}
