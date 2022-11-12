package sg.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserInputProcessingTest {

    @Test
    public void bestMatchByLevenshteinDistance() {
        List<String> properWords = new ArrayList<String>();
        properWords.add("seng kang");
        properWords.add("hougang");
        properWords.add("kovan");
        properWords.add("dhoby ghaut");
        properWords.add("little india");

        String match = UserInputProcessing.bestMatchByLevenshteinDistance("dhoy ghuay", properWords);
        assertEquals("dhoby ghaut", match);
    }
}