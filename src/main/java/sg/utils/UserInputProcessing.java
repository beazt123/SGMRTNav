package sg.utils;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserInputProcessing {
    public static int bestScoreByLevenshteinDistance(String s, List<String> l) {
        ExtractedResult result = FuzzySearch.extractOne(s, l);
        return result.getScore();
    }

    public static int levenshteinDistance(String s1, String s2) {
        return FuzzySearch.ratio(s1, s2);
    }

    public static String bestMatchByLevenshteinDistance(String s, List<String> l) {
        ExtractedResult result = FuzzySearch.extractOne(s, l);
        return result.getString();
    }
    public static <T> T bestMatchByLevenshteinDistance(String s, List<T> l, Function<T, String> getStrRepresentation) {

        BoundExtractedResult<T> result = FuzzySearch.extractOne(s, l, t -> getStrRepresentation.apply(t));
        return result.getReferent();
    }

    public static String formatListOfChoices(List choices) {
        if (choices.size() == 1) return choices.get(0).toString();
        else if (choices.size() == 2) {
            return choices.get(0).toString() + " or " + choices.get(1).toString();
        } else {
            return choices.get(0).toString() + ", " + formatListOfChoices(choices.subList(1, choices.size()));
        }
    }

}
