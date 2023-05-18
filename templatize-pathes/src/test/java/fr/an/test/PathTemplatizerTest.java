package fr.an.test;

import org.junit.Assert;
import org.junit.Test;

public class PathTemplatizerTest {

    @Test
    public void testTemplatizeSimple_uuid() {
        assertTemplatizeSimple("{uuid}", "123e4567-e89b-12d3-a456-426614174000");
        assertTemplatizeSimple("{UUID}", "123E4567-E89B-12D3-A456-426614174000");
    }

    private static void assertTemplatizeSimple(String expected, String actual) {
        Assert.assertEquals(expected, PathTemplatizer.templatizeSimple(actual));
    }
}
