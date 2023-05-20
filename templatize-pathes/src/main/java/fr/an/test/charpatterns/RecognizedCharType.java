package fr.an.test.charpatterns;

public enum RecognizedCharType {

    DIGIT,
    HEXA_LOWER, // 0-9a-f
    HEXA_UPPER, // 0-9A-F
    LETTER_LOWER, // a-z
    LETTER_UPPER, // A-Z
    IDENT, // A-Za-z$_
    SEPARATOR, // -:,; .+*~
    OTHER; // ..

    public static RecognizedCharType charTypeOf(char ch) {
        RecognizedCharType chType;
        if (Character.isDigit(ch)) {
            chType = RecognizedCharType.DIGIT;
        } else if (Character.isLetter(ch)) {
            chType = RecognizedCharType.IDENT;
            if ('a' <= ch) {
                if (ch <= 'f') {
                    chType = RecognizedCharType.HEXA_LOWER;
                } else if (ch <= 'z') {
                    chType = RecognizedCharType.LETTER_LOWER;
                }
            } else if ('A' <= ch) {
                if (ch <= 'F') {
                    chType = RecognizedCharType.HEXA_UPPER;
                } else if (ch <= 'Z') {
                    chType = RecognizedCharType.LETTER_UPPER;
                }
            }
        } else if (Character.isJavaIdentifierPart(ch)) {
            chType = RecognizedCharType.IDENT;
        } else if (ch == '-' || ch == ':' || ch == ',' || ch == ';' || ch == ' ' || ch == '.'
                || ch == '+' || ch == '*' || ch == '~') {
            chType = RecognizedCharType.SEPARATOR;
        } else {
            chType = RecognizedCharType.OTHER;
        }
        return chType;
    }

    public static RecognizedCharType and(RecognizedCharType t1, RecognizedCharType t2) {
        switch (t1) {
            case DIGIT:
                switch (t2) {
                    case DIGIT:
                        return DIGIT;
                    case HEXA_LOWER:
                        return HEXA_LOWER;
                    case HEXA_UPPER:
                        return HEXA_UPPER;
                    case LETTER_LOWER:
                        return IDENT;
                    case LETTER_UPPER:
                        return IDENT;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case HEXA_LOWER:
                switch (t2) {
                    case DIGIT:
                        return DIGIT;
                    case HEXA_LOWER:
                        return HEXA_LOWER;
                    case HEXA_UPPER:
                        return IDENT;
                    case LETTER_LOWER:
                        return LETTER_LOWER;
                    case LETTER_UPPER:
                        return IDENT;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case HEXA_UPPER:
                switch (t2) {
                    case DIGIT:
                        return DIGIT;
                    case HEXA_LOWER:
                        return IDENT;
                    case HEXA_UPPER:
                        return HEXA_UPPER;
                    case LETTER_LOWER:
                        return IDENT;
                    case LETTER_UPPER:
                        return IDENT;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case LETTER_LOWER:
                switch (t2) {
                    case DIGIT:
                        return IDENT;
                    case HEXA_LOWER:
                        return IDENT;
                    case HEXA_UPPER:
                        return IDENT;
                    case LETTER_LOWER:
                        return LETTER_LOWER;
                    case LETTER_UPPER:
                        return IDENT;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case LETTER_UPPER:
                switch (t2) {
                    case DIGIT:
                        return IDENT;
                    case HEXA_LOWER:
                        return IDENT;
                    case HEXA_UPPER:
                        return IDENT;
                    case LETTER_LOWER:
                        return IDENT;
                    case LETTER_UPPER:
                        return LETTER_UPPER;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case IDENT:
                switch (t2) {
                    case DIGIT:
                        return IDENT;
                    case HEXA_LOWER:
                        return IDENT;
                    case HEXA_UPPER:
                        return IDENT;
                    case LETTER_LOWER:
                        return IDENT;
                    case LETTER_UPPER:
                        return IDENT;
                    case IDENT:
                        return IDENT;
                    case SEPARATOR:
                        return OTHER;
                    case OTHER:
                        return OTHER;
                }
            case SEPARATOR:
                switch (t2) {
                    case DIGIT:
                        return OTHER;
                    case HEXA_LOWER:
                        return OTHER;
                    case HEXA_UPPER:
                        return OTHER;
                    case LETTER_LOWER:
                        return OTHER;
                    case LETTER_UPPER:
                        return OTHER;
                    case IDENT:
                        return OTHER;
                    case SEPARATOR:
                        return SEPARATOR;
                    case OTHER:
                        return OTHER;
                }
            case OTHER:
                return OTHER;
        }
        return OTHER;
    }

    public static String toChTypesPattern(String text) {
        return toChTypesPattern(text, 0, text.length());
    }

    public static String toChTypesPattern(String text, int fromIndex, int toIndex) {
        StringBuilder sb = new StringBuilder();
        appendChTypesPattern(sb, text, fromIndex, toIndex);
        return sb.toString();
    }

    public static void appendChTypesPattern(StringBuilder out, String text, int fromIndex, int toIndex) {
        char ch = text.charAt(fromIndex);
        RecognizedCharType prevChType = RecognizedCharType.OTHER;
        int prevTypeCount = 0;
        for(int i = fromIndex; i < toIndex; i++) {
            ch = text.charAt(i);
            RecognizedCharType chType = RecognizedCharType.charTypeOf(ch);

            if (chType == RecognizedCharType.OTHER || chType == RecognizedCharType.SEPARATOR) {
                // non standard char or separator .. write explicitely
                if (prevTypeCount > 0) {
                    appendChTypePattern(out, prevChType, prevTypeCount);
                }
                out.append(ch);
                prevTypeCount = 0;
                prevChType = chType;
                continue;
            }

            if (prevTypeCount == 0) {
                // start new sequence
                prevChType = chType;
                prevTypeCount++;
                continue;
            }
            if (chType == prevChType) {
                prevTypeCount++;
                continue;
            } else {
                RecognizedCharType andChType = RecognizedCharType.and(prevChType, chType);
                if (andChType != RecognizedCharType.OTHER) {
                    // ok, upgrade prev type (example 'digit', 'hexa' => 'hexa')
                    prevChType = andChType;
                    prevTypeCount++;
                } else {
                    // no type coalesce?.. flush prev type
                    appendChTypePattern(out, prevChType, prevTypeCount);
                    if (chType == RecognizedCharType.OTHER || chType == RecognizedCharType.SEPARATOR) {
                        // non standard char or separator .. write it explicitely???
                        out.append(ch);
                        prevTypeCount = 0;
                    } else {
                        prevChType = chType;
                        prevTypeCount = 1;
                    }
                }
            }
        }
        if (prevTypeCount != 0) {
            appendChTypePattern(out, prevChType, prevTypeCount);
        }
    }

    protected static void appendChTypePattern(StringBuilder out, RecognizedCharType chType, int count) {
        appendChTypePattern(out, chType);
        if (count != 1) {
            if (count > 15) {
                out.append("*");
            } else { // if (prevTypeCount > 1) {
                out.append("{" + count + "}");
            }
        }
    }

    protected static void appendChTypePattern(StringBuilder out, RecognizedCharType chType) {
        switch(chType) {
            case DIGIT:
                out.append("\\d");
                break;
            case HEXA_LOWER:
                out.append("[0-9a-f]"); // ??
                break;
            case HEXA_UPPER:
                out.append("[0-9A-F]");
                break;
            case LETTER_LOWER:
                out.append("[a-z]");
                break;
            case LETTER_UPPER:
                out.append("[A-Z]");
                break;
            case IDENT:
                out.append("\\w");
                break;
            case SEPARATOR:
                out.append("(.)"); // ??
                break;
            case OTHER:
                out.append(".");
                break;
        }
    }

}
