/*
 * StringUtilities.java
 *
 * Copyright (c) 2012 Mike Strobel
 *
 * This source code is subject to terms and conditions of the Apache License, Version 2.0.
 * A copy of the license can be found in the License.html file at the root of this distribution.
 * By using this source code in any fashion, you are agreeing to be bound by the terms of the
 * Apache License, Version 2.0.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.strobel.core;

import com.strobel.util.ContractUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Mike Strobel
 */
public final class StringUtilities {
    public final static String EMPTY = "";

    private StringUtilities() {
        throw ContractUtils.unreachable();
    }

    private final static StringComparator[] _comparators = new StringComparator[] { StringComparator.Ordinal, StringComparator.OrdinalIgnoreCase };

    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.length() == 0;
    }

    public static boolean equals(final String s1, final String s2) {
        return StringComparator.Ordinal.equals(s1, s2);
    }

    public static boolean equals(final String s1, final String s2, final StringComparison comparison) {
        return _comparators[VerifyArgument.notNull(comparison, "comparison").ordinal()].equals(s1, s2);
    }

    public static int compare(final String s1, final String s2) {
        return StringComparator.Ordinal.compare(s1, s2);
    }

    public static int compare(final String s1, final String s2, final StringComparison comparison) {
        return _comparators[VerifyArgument.notNull(comparison, "comparison").ordinal()].compare(s1, s2);
    }

    public static int getHashCode(final String s) {
        if (isNullOrEmpty(s)) {
            return 0;
        }
        return s.hashCode();
    }

    public static int getHashCodeIgnoreCase(final String s) {
        if (isNullOrEmpty(s)) {
            return 0;
        }

        int hash = 0;

        for (int i = 0, n = s.length(); i < n; i++) {
            hash = 31 * hash + Character.toLowerCase(s.charAt(i));
        }

        return hash;
    }

    public static boolean isNullOrWhitespace(final String s) {
        if (isNullOrEmpty(s)) {
            return true;
        }
        for (int i = 0, length = s.length(); i < length; i++) {
            final char ch = s.charAt(i);
            if (!Character.isWhitespace(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean startsWith(final CharSequence value, final CharSequence prefix) {
        return substringEquals(
            VerifyArgument.notNull(value, "value"),
            0,
            VerifyArgument.notNull(prefix, "prefix"),
            0,
            prefix.length(),
            StringComparison.Ordinal
        );
    }

    public static boolean startsWithIgnoreCase(final CharSequence value, final String prefix) {
        return substringEquals(
            VerifyArgument.notNull(value, "value"),
            0,
            VerifyArgument.notNull(prefix, "prefix"),
            0,
            prefix.length(),
            StringComparison.OrdinalIgnoreCase
        );
    }

    public static boolean endsWith(final CharSequence value, final CharSequence suffix) {
        final int valueLength = VerifyArgument.notNull(value, "value").length();
        final int suffixLength = VerifyArgument.notNull(suffix, "suffix").length();
        final int testOffset = valueLength - suffixLength;

        return testOffset >= 0 &&
               substringEquals(
                   value,
                   testOffset,
                   suffix,
                   0,
                   suffixLength,
                   StringComparison.Ordinal
               );
    }

    public static boolean endsWithIgnoreCase(final CharSequence value, final String suffix) {
        final int valueLength = VerifyArgument.notNull(value, "value").length();
        final int suffixLength = VerifyArgument.notNull(suffix, "suffix").length();
        final int testOffset = valueLength - suffixLength;

        return testOffset >= 0 &&
               substringEquals(
                   value,
                   testOffset,
                   suffix,
                   0,
                   suffixLength,
                   StringComparison.OrdinalIgnoreCase
               );
    }

    public static String concat(final Iterable<String> values) {
        return join(null, values);
    }

    public static String concat(final String... values) {
        return join(null, values);
    }

    public static String join(final String separator, final Iterable<?> values) {
        VerifyArgument.notNull(values, "values");

        final StringBuilder sb = new StringBuilder();

        boolean appendSeparator = false;

        for (final Object value : values) {
            if (value == null) {
                continue;
            }

            if (appendSeparator) {
                sb.append(separator);
            }

            appendSeparator = true;

            sb.append(value);
        }

        return sb.toString();
    }

    public static String join(final String separator, final String... values) {
        if (ArrayUtilities.isNullOrEmpty(values)) {
            return EMPTY;
        }

        final StringBuilder sb = new StringBuilder();

        for (int i = 0, n = values.length; i < n; i++) {
            final String value = values[i];

            if (value == null) {
                continue;
            }

            if (i != 0 && separator != null) {
                sb.append(separator);
            }

            sb.append(value);
        }

        return sb.toString();
    }

    public static boolean substringEquals(
        final CharSequence value,
        final int offset,
        final CharSequence comparand,
        final int comparandOffset,
        final int substringLength) {

        return substringEquals(
            value,
            offset,
            comparand,
            comparandOffset,
            substringLength,
            StringComparison.Ordinal
        );
    }

    public static boolean substringEquals(
        final CharSequence value,
        final int offset,
        final CharSequence comparand,
        final int comparandOffset,
        final int substringLength,
        final StringComparison comparison) {

        VerifyArgument.notNull(value, "value");
        VerifyArgument.notNull(comparand, "comparand");

        VerifyArgument.isNonNegative(offset, "offset");
        VerifyArgument.isNonNegative(comparandOffset, "comparandOffset");

        VerifyArgument.isNonNegative(substringLength, "substringLength");

        final int valueLength = value.length();

        if (offset + substringLength > valueLength) {
            return false;
        }

        final int comparandLength = comparand.length();

        if (comparandOffset + substringLength > comparandLength) {
            return false;
        }

        final boolean ignoreCase = comparison == StringComparison.OrdinalIgnoreCase;

        for (int i = 0; i < substringLength; i++) {
            final char vc = value.charAt(offset + i);
            final char cc = comparand.charAt(comparandOffset + i);

            if (vc == cc || ignoreCase && Character.toLowerCase(vc) == Character.toLowerCase(cc)) {
                continue;
            }

            return false;
        }

        return true;
    }

    public static boolean isTrue(final String value) {
        if (isNullOrWhitespace(value)) {
            return false;
        }

        final String trimmedValue = value.trim();

        if (trimmedValue.length() == 1) {
            final char ch = Character.toLowerCase(trimmedValue.charAt(0));
            return ch == 't' || ch == 'y' || ch == '1';
        }

        return StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "true") ||
               StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "yes");
    }

    public static boolean isFalse(final String value) {
        if (isNullOrWhitespace(value)) {
            return false;
        }

        final String trimmedValue = value.trim();

        if (trimmedValue.length() == 1) {
            final char ch = Character.toLowerCase(trimmedValue.charAt(0));
            return ch == 'f' || ch == 'n' || ch == '0';
        }

        return StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "false") ||
               StringComparator.OrdinalIgnoreCase.equals(trimmedValue, "no");
    }

    public static String removeLeft(final String value, final String prefix) {
        return removeLeft(value, prefix, false);
    }

    public static String removeLeft(final String value, final String prefix, final boolean ignoreCase) {
        VerifyArgument.notNull(value, "value");

        if (isNullOrEmpty(prefix)) {
            return value;
        }

        final int prefixLength = prefix.length();
        final int remaining = value.length() - prefixLength;

        if (remaining < 0) {
            return value;
        }

        if (remaining == 0) {
            if (ignoreCase) {
                return value.equalsIgnoreCase(prefix) ? EMPTY : value;
            }
            return value.equals(prefix) ? EMPTY : value;
        }

        if (ignoreCase) {
            return startsWithIgnoreCase(value, prefix)
                   ? value.substring(prefixLength)
                   : value;
        }

        return value.startsWith(prefix)
               ? value.substring(prefixLength)
               : value;
    }

    public static String removeLeft(final String value, final char[] removeChars) {
        VerifyArgument.notNull(value, "value");
        VerifyArgument.notNull(removeChars, "removeChars");

        final int totalLength = value.length();
        int start = 0;

        while (start < totalLength && ArrayUtilities.contains(removeChars, value.charAt(start))) {
            ++start;
        }

        return start > 0 ? value.substring(start) : value;
    }

    public static String removeRight(final String value, final String suffix) {
        return removeRight(value, suffix, false);
    }

    public static String removeRight(final String value, final String suffix, final boolean ignoreCase) {
        VerifyArgument.notNull(value, "value");

        if (isNullOrEmpty(suffix)) {
            return value;
        }

        final int valueLength = value.length();
        final int suffixLength = suffix.length();
        final int end = valueLength - suffixLength;

        if (end < 0) {
            return value;
        }

        if (end == 0) {
            if (ignoreCase) {
                return value.equalsIgnoreCase(suffix) ? EMPTY : value;
            }
            return value.equals(suffix) ? EMPTY : value;
        }

        if (ignoreCase) {
            return endsWithIgnoreCase(value, suffix)
                   ? value.substring(0, end)
                   : value;
        }

        return value.endsWith(suffix)
               ? value.substring(0, end)
               : value;
    }

    public static String removeRight(final String value, final char[] removeChars) {
        VerifyArgument.notNull(value, "value");
        VerifyArgument.notNull(removeChars, "removeChars");

        final int totalLength = value.length();
        int length = totalLength;

        while (length > 0 && ArrayUtilities.contains(removeChars, value.charAt(length - 1))) {
            --length;
        }

        return length == totalLength ? value : value.substring(0, length);
    }

    public static String padLeft(final String value, final int length) {
        VerifyArgument.notNull(value, "value");
        VerifyArgument.isNonNegative(length, "length");

        if (length == 0) {
            return value;
        }

        return String.format("%1$" + length + "s", value);
    }

    public static String padRight(final String value, final int length) {
        VerifyArgument.notNull(value, "value");
        VerifyArgument.isNonNegative(length, "length");

        if (length == 0) {
            return value;
        }

        return String.format("%1$-" + length + "s", value);
    }

    public static String trimLeft(final String value) {
        VerifyArgument.notNull(value, "value");

        final int totalLength = value.length();
        int start = 0;

        while (start < totalLength && value.charAt(start) <= ' ') {
            ++start;
        }

        return start > 0 ? value.substring(start) : value;
    }

    public static String trimRight(final String value) {
        VerifyArgument.notNull(value, "value");

        final int totalLength = value.length();
        int length = totalLength;

        while (length > 0 && value.charAt(length - 1) <= ' ') {
            --length;
        }

        return length == totalLength ? value : value.substring(0, length);
    }

    public static String trimAndRemoveLeft(final String value, final String prefix) {
        return trimAndRemoveLeft(value, prefix, false);
    }

    public static String trimAndRemoveLeft(final String value, final String prefix, final boolean ignoreCase) {
        VerifyArgument.notNull(value, "value");

        final String trimmedValue = value.trim();
        final String result = removeLeft(trimmedValue, prefix, ignoreCase);

        //noinspection StringEquality
        if (result == trimmedValue) {
            return trimmedValue;
        }

        return trimLeft(result);
    }

    public static String trimAndRemoveLeft(final String value, final char[] removeChars) {
        VerifyArgument.notNull(value, "value");

        final String trimmedValue = value.trim();
        final String result = removeLeft(trimmedValue, removeChars);

        //noinspection StringEquality
        if (result == trimmedValue) {
            return trimmedValue;
        }

        return trimLeft(result);
    }

    public static String trimAndRemoveRight(final String value, final String suffix) {
        return trimAndRemoveRight(value, suffix, false);
    }

    public static String trimAndRemoveRight(final String value, final String suffix, final boolean ignoreCase) {
        VerifyArgument.notNull(value, "value");

        final String trimmedValue = value.trim();
        final String result = removeRight(trimmedValue, suffix, ignoreCase);

        //noinspection StringEquality
        if (result == trimmedValue) {
            return trimmedValue;
        }

        return trimRight(result);
    }

    public static String trimAndRemoveRight(final String value, final char[] removeChars) {
        VerifyArgument.notNull(value, "value");

        final String trimmedValue = value.trim();
        final String result = removeRight(trimmedValue, removeChars);

        //noinspection StringEquality
        if (result == trimmedValue) {
            return trimmedValue;
        }

        return trimRight(result);
    }

    public static int getUtf8ByteCount(final String value) {
        VerifyArgument.notNull(value, "value");

        if (value.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (int i = 0, n = value.length(); i < n; ++i, ++count) {
            final char c = value.charAt(i);
            if (c > 0x07FF) {
                count += 2;
            }
            else if (c > 0x007F) {
                ++count;
            }
        }

        return count;
    }

    public static String escape(final char ch) {
        switch (ch) {
            case '\\':
                return "\\\\";
            case '\0':
                return "\\0";
            case '\b':
                return "\\b";
            case '\f':
                return "\\f";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            case '"':
                return "\\\"";

            default:
                if (ch >= 192 ||
                    Character.isISOControl(ch) ||
                    Character.isSurrogate(ch) ||
                    Character.isWhitespace(ch) && ch != ' ') {

                    return format("\\u%1$04x", (int) ch);
                }
                else {
                    return String.valueOf(ch);
                }
        }
    }

    public static String escape(final char ch, final boolean quote) {
        if (quote) {
            switch (ch) {
                case '\\':
                    return "'\\\\'";
                case '\0':
                    return "'\\0'";
                case '\b':
                    return "'\\b'";
                case '\f':
                    return "'\\f'";
                case '\n':
                    return "'\\n'";
                case '\r':
                    return "'\\r'";
                case '\t':
                    return "'\\t'";
                case '"':
                    return "'\\\"'";

                default:
                    if (ch >= 192 ||
                        Character.isISOControl(ch) ||
                        Character.isSurrogate(ch) ||
                        Character.isWhitespace(ch) && ch != ' ') {

                        return format("'\\u%1$04x'", (int) ch);
                    }
                    else {
                        return "'" + ch + "'";
                    }
            }
        }

        return escape(ch);
    }

    public static String escape(final String value) {
        return escape(value, false);
    }

    public static String escape(final String value, final boolean quote) {
        final StringBuilder sb = new StringBuilder();

        if (quote) {
            sb.append('"');
        }

        for (int i = 0, n = value.length(); i < n; i++) {
            final char ch = value.charAt(i);

            switch (ch) {
                case '\t':
                    sb.append('\\');
                    sb.append('t');
                    break;
                case '\b':
                    sb.append('\\');
                    sb.append('b');
                    break;
                case '\n':
                    sb.append('\\');
                    sb.append('n');
                    break;
                case '\r':
                    sb.append('\\');
                    sb.append('r');
                    break;
                case '\f':
                    sb.append('\\');
                    sb.append('f');
                    break;
                case '\"':
                    sb.append('\\');
                    sb.append('"');
                    break;
                case '\\':
                    sb.append('\\');
                    sb.append('\\');
                    break;
                default:
                    if (ch >= 192) {
                        sb.append(String.format("\\u%1$04x;", (int) ch));
                    }
                    else {
                        sb.append(ch);
                    }
                    break;
            }
        }

        if (quote) {
            sb.append('"');
        }

        return sb.toString();
    }

    public static String repeat(final char ch, final int length) {
        VerifyArgument.isNonNegative(length, "length");
        final char[] c = new char[length];
        Arrays.fill(c, 0, length, ch);
        return new String(c);
    }

    public static List<String> split(
        final String value,
        final char firstDelimiter,
        final char... additionalDelimiters) {

        return split(value, true, firstDelimiter, additionalDelimiters);
    }

    public static List<String> split(
        final String value,
        final boolean removeEmptyEntries,
        final char firstDelimiter,
        final char... additionalDelimiters) {

        VerifyArgument.notNull(value, "value");

        final int end = value.length();
        final ArrayList<String> parts = new ArrayList<>();

        if (end == 0) {
            return parts;
        }

        int start = 0;
        int i = start;

        while (i < end) {
            final char ch = value.charAt(i);

            if (ch == firstDelimiter || contains(additionalDelimiters, ch)) {
                if (i != start || !removeEmptyEntries) {
                    parts.add(value.substring(start, i));
                }

                start = i + 1;

                if (!removeEmptyEntries && start == end) {
                    parts.add(EMPTY);
                }
            }

            ++i;
        }

        if (start < end) {
            parts.add(value.substring(start, end));
        }

        return parts;
    }

    public static List<String> split(final String value, final char[] delimiters) {
        return split(value, true, delimiters);
    }

    public static List<String> split(
        final String value,
        final boolean removeEmptyEntries,
        final char[] delimiters) {

        VerifyArgument.notNull(value, "value");
        VerifyArgument.notNull(delimiters, "delimiters");

        final int end = value.length();
        final ArrayList<String> parts = new ArrayList<>();

        if (end == 0) {
            return parts;
        }

        int start = 0;
        int i = start;

        while (i < end) {
            final char ch = value.charAt(i);

            if (contains(delimiters, ch)) {
                if (i != start || !removeEmptyEntries) {
                    parts.add(value.substring(start, i));
                }

                start = i + 1;
            }

            ++i;
        }

        if (start < end) {
            parts.add(value.substring(start, end));
        }

        return parts;
    }

    private static boolean contains(final char[] array, final char value) {
        for (final char c : array) {
            if (c == value) {
                return true;
            }
        }
        return false;
    }
}
