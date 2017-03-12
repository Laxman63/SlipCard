package com.silpe.vire.slip;

/**
 * Created by eugene on 3/12/2017.
 */

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginFieldUnitTest {

    @Test
    public void QRFragmentStringTest() {
        assertTrue(MainActivity.QR_FRAGMENT == "fragment_qr");
    }

}