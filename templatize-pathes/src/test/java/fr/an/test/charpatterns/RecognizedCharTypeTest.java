package fr.an.test.charpatterns;

import org.junit.Assert;
import org.junit.Test;

public class RecognizedCharTypeTest {

    @Test
    public void testToChTypesPattern_digits() {
        assertChTypesPattern("\\d{10}", "0123456789");
        assertChTypesPattern("\\d*", "01234567890123456789");
    }

    @Test
    public void testToChTypesPattern_hexaLower() {
        assertChTypesPattern("[0-9a-f]{12}", "abafabadaabc");
        assertChTypesPattern("[0-9a-f]*", "abafabadaabcaababababababf");
        assertChTypesPattern("[0-9a-f]{13}", "0123456789abc");
        assertChTypesPattern("[0-9a-f]*", "0123456789abcdef0123456789abcdef");
    }

    @Test
    public void testToChTypesPattern_hexaUpper() {
        assertChTypesPattern("[0-9A-F]{12}", "ABCDEFABCDEF");
        assertChTypesPattern("[0-9A-F]*", "ABCDEFABCDEFABCDEF");
        assertChTypesPattern("[0-9A-F]{13}", "0123456789ABC");
        assertChTypesPattern("[0-9A-F]*", "0123456789ABCDEF0123456789ABCDEF");
    }

    @Test
    public void testToChTypesPattern_ident() {
        assertChTypesPattern("\\w{5}", "hello");
        assertChTypesPattern("\\w{5}", "HELLO");
        assertChTypesPattern("\\w{5}", "Hello");
        assertChTypesPattern("\\w{11}", "Hello_world");
        assertChTypesPattern("\\w{14}", "Hello_world123");
        assertChTypesPattern("\\w{15}", "Hello_world123$");
    }

    @Test
    public void testToChTypesPattern_sep() {
        assertChTypesPattern(".", ".");
        assertChTypesPattern(",", ",");
        assertChTypesPattern(";", ";");
        assertChTypesPattern(",;:+", ",;:+");
    }

    @Test
    public void testToChTypesPattern_other() {
        assertChTypesPattern("&#\\", "&#\\");
    }

    @Test
    public void testToChTypesPattern_space() {
        assertChTypesPattern(" ", " ");
        assertChTypesPattern("  ", "  ");
        assertChTypesPattern("   ", "   ");
    }

    @Test
    public void testToChTypesPattern_compose() {
        assertChTypesPattern("\\w{4} [a-z]{2} \\w{5} \\w{3} \\w{9}. \\w{3} \\w{4} [a-z]{2} \\w{8} \\d.", "This is plain old sentence1. And here is sentence 2.");
        assertChTypesPattern("\\w{4}-\\w{5}-\\w{3}-\\d{3}-[a-z]{3}", "some-snake-var-123-xxx");
        assertChTypesPattern("\\w{13}", "someCamelCase");
    }

    protected static void assertChTypesPattern(String expected, String text) {
        String actual = RecognizedCharType.toChTypesPattern(text);
        Assert.assertEquals(expected, actual);
    }
}
