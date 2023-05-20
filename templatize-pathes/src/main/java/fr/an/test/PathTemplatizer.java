package fr.an.test;

import fr.an.test.charpatterns.RecognizedCharType;

public class PathTemplatizer {

    public String templatize(String line) {
        String[] elts = line.split("/"); //TODO
        int len = elts.length;
        for (int i = 0; i < len; i++) {
            elts[i] = templatizeElt(elts[i]);
        }
        return String.join("/", elts);
    }

    private String templatizeElt(String elt) {
        String res = elt;
        int indexEq = elt.indexOf('=');
        int eltLen = elt.length();
        if (indexEq != -1 && indexEq < eltLen) {
            String name = elt.substring(0, indexEq);
            String value = elt.substring(indexEq+1, eltLen);
            String maybeTemplatizeName = name;
            if (maybeTemplatizeName.length() > 15) {
                maybeTemplatizeName = templatizeValue(maybeTemplatizeName);
            }
            // TODO... test if name ends with "date", "day", ... => use special
            String templatizeValue = templatizeValue(value);
            res = maybeTemplatizeName + "=" + templatizeValue;
        } else {
            // TODO?
            res = templatizeValue(elt);
        }
        return res;
    }

    private String templatizeValue(String text) {
        String res = text;
        int textLen = text.length();
        // test known special words
        if (textLen == 8) {
            if ("_SUCCESS".equals(text)) {
                return text;
            }
        } else if (textLen == 10) {
            if ("_temporary".equals(text)) {
                return text;
            }
        }

        if (textLen == 10) {
            // maybe a date "yyyy-mm-dd"
            if (text.charAt(4) == '-' && text.charAt(7) == '-'
                && match4DigitsAt(text, 0)
                && match2DigitsAt(text, 5)
                && match2DigitsAt(text, 8)) {
                int year = extractInt4DigitsAt(text, 0);
                int month = extractInt2DigitsAt(text, 5);
                int day = extractInt2DigitsAt(text, 8);
                // TOADD compute if d, d-1, d-2, <=d-1w, <=d-2w, ...
                return "{date}";
            }
        }
        String prefix = "";
        String suffix = "";
        String remain = text;
        if (remain.startsWith("part-")) {
            prefix += "part-";
            remain = remain.substring(5);
        }
        if (remain.endsWith(".gz")) {
            suffix = ".gz" + suffix;
            remain = remain.substring(0, remain.length()-3);
        }
        if (remain.endsWith(".parquet")) {
            suffix = ".parquet" + suffix;
            remain = remain.substring(0, remain.length()-8);
        }
        String remainTemplate = templatizeSimple(remain);
        return prefix + remainTemplate + suffix;
    }

    public static String templatizeSimple(String text) {
        // test if number
        int len = text.length();
        if (len == 1) {
            char ch = text.charAt(0);
            if (Character.isDigit(ch)) {
                return "\\d";
            }
        }
        if (matchNDigitsAt(text, 0, len)) {
            return "\\d{" + len + "}";
        }
        // test if UUID: "8-4-4-4-12"
        // 123e4567 - e89b -  12d3 -  a456 - 426614174000
        // 0        8      13      18      23
        if (len == 36) {
            if (text.charAt(8) == '-' && text.charAt(13) == '-' && text.charAt(18) == '-' && text.charAt(23) == '-') {
                if (matchNHexaUpperAt(text, 0, 8)
                        && matchNHexaUpperAt(text, 9, 4)
                        && matchNHexaUpperAt(text, 14, 4)
                        && matchNHexaUpperAt(text, 19, 4)
                        && matchNHexaUpperAt(text, 24, 12)
                ) {
                    return "{UUID}";
                }
                if (matchNHexaLowerAt(text, 0, 8)
                        && matchNHexaLowerAt(text, 9, 4)
                        && matchNHexaLowerAt(text, 14, 4)
                        && matchNHexaLowerAt(text, 19, 4)
                        && matchNHexaLowerAt(text, 24, 12)
                ) {
                    return "{uuid}";
                }
            }
        }


        // test if remain is a known word??
        // else templatize..
        return RecognizedCharType.toChTypesPattern(text);
    }

    public static boolean matchNDigitsAt(String text, int fromIndex, int n) {
        int toIndex = fromIndex + n;
        for(int i = fromIndex; i < toIndex; i++) {
            if (! Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchNDigitsOrDashAt(String text, int fromIndex, int n) {
        int toIndex = fromIndex + n;
        for(int i = fromIndex; i < toIndex; i++) {
            char ch = text.charAt(i);
            if (! (Character.isDigit(ch) || ch == '-')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHexaUpperAt(char ch) {
        return Character.isDigit(ch) || ('A' <= ch && ch <= 'F');
    }
    public static boolean isHexaLowerAt(char ch) {
        return Character.isDigit(ch) || ('a' <= ch && ch <= 'f');
    }

    public static boolean matchNHexaUpperAt(String text, int fromIndex, int n) {
        int toIndex = fromIndex + n;
        for(int i = fromIndex; i < toIndex; i++) {
            if (! isHexaUpperAt(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchNHexaLowerAt(String text, int fromIndex, int n) {
        int toIndex = fromIndex + n;
        for(int i = fromIndex; i < toIndex; i++) {
            if (! isHexaLowerAt(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static boolean match4DigitsAt(String text, int fromIndex) {
        return Character.isDigit(text.charAt(fromIndex)) && Character.isDigit(text.charAt(fromIndex+1))
                && Character.isDigit(text.charAt(fromIndex+2)) && Character.isDigit(text.charAt(fromIndex+3));
    }

    public static boolean match2DigitsAt(String text, int fromIndex) {
        return Character.isDigit(text.charAt(fromIndex)) && Character.isDigit(text.charAt(fromIndex+1));
    }

    public static int extractInt4DigitsAt(String text, int fromIndex) {
        int a0 = text.charAt(fromIndex) - '0';
        int a1 = text.charAt(fromIndex+1) - '0';
        int a2 = text.charAt(fromIndex+2) - '0';
        int a3 = text.charAt(fromIndex+3) - '0';
        return a0*1000 + a1*100 + a2*10 + a3;
    }

    public static int extractInt2DigitsAt(String text, int fromIndex) {
        int a0 = text.charAt(fromIndex) - '0';
        int a1 = text.charAt(fromIndex+1) - '0';
        return a0*10 + a1;
    }

}
