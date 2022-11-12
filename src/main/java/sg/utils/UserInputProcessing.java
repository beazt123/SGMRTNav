package sg.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.util.List;

public class UserInputProcessing {
//    Number levenshteinDistance(String s1, String s2) {
//        return FuzzySearch.ratio(s1, s2);
//    }

    public static String bestMatchByLevenshteinDistance(String s, List<String> l) {
        ExtractedResult result = FuzzySearch.extractOne(s, l);
        return result.getString();
    }

}
