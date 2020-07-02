package org.apache.syncope.core.provisioning.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.core.provisioning.api.AbstractTest;
import org.apache.syncope.core.provisioning.api.jexl.JexlUtils;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(Parameterized.class)
public class FormatUtilsDoubleTest extends AbstractTest {

    private final Calendar calendar = Calendar.getInstance();
    private final Date date = calendar.getTime();
    private String conversionPattern;



    @Test
    public void prova() {
        double number = date.getTime();
        System.out.println("ciao");
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        assertEquals(df.format(number), FormatUtils.format(number));

        conversionPattern = "###,###";
        df = new DecimalFormat(conversionPattern);
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        assertEquals(df.format(number), FormatUtils.format(number, conversionPattern));
    }
}
