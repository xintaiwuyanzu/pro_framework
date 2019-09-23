package com.dr;

import java.util.regex.Pattern;

public class MatcherTest {
    public static void main(String[] args) {
        System.out.println(Pattern.matches("ab*b", "abb"));
        System.out.println(Pattern.matches("aaa*", "ab"));
    }
}
