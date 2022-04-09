package com.dr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherTest {
    private static final Pattern inPattern = Pattern.compile("#\\{(\\$\\w+)}|#\\{(\\$\\w+#\\w+)}");
    private static Pattern inPattern1 = Pattern.compile("(\\$[in]#)");

    public static void main(String[] args) {
        Matcher matcher = inPattern.matcher("select #{columns} from  #{$table} #{$in#222} 222 ");
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println(matcher.groupCount());
            matcher.appendReplacement(stringBuffer, "111");
            System.out.println(stringBuffer);
        }
        matcher.appendTail(stringBuffer);
        System.out.println(stringBuffer);

        Pattern pattern = Pattern.compile("#\\w+");

        Matcher matcher1 = pattern.matcher("#{$in#aaa}");

        while (matcher1.find()) {
            System.out.println(matcher1.group());
        }
        System.out.println("aaa#bbb".split("#")[1]);


        System.out.printf("aaa  %s%n", "bbb");
        String aaa = "aaa $aaa() $aaa()ddd $aaa";
        System.out.println(aaa.replaceAll("\\$aaa", "bbb"));

    }

}
