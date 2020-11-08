package com.dr.framework.core.security.bo;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dr.framework.core.security.service.SecurityManager.*;

/**
 * 权限持有对象，封装权限相关的操作
 *
 * @author dr
 */
public class PermissionMatcher implements Comparable<PermissionMatcher> {
    private int order;
    List<MatcherItem> permissionMatchers;

    public PermissionMatcher(String permissionCode) {
        Assert.isTrue(!StringUtils.isEmpty(permissionCode), "权限编码不能为空！");
        permissionMatchers = Stream.of(permissionCode.trim().split(groupSplit))
                .map(MatcherItem::new)
                .sorted()
                .collect(Collectors.toList());
        order = permissionMatchers.get(0).index;
    }

    public boolean match(String permissionCode) {
        if (StringUtils.isEmpty(permissionCode)) {
            return true;
        }
        for (MatcherItem matcher : permissionMatchers) {
            if (matcher.match(permissionCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(PermissionMatcher o) {
        return order - o.order;
    }

    class MatcherItem implements Comparable<MatcherItem> {
        int index = 0;
        Matcher first, second, third;

        MatcherItem(String permissionCode) {
            Assert.isTrue(!StringUtils.isEmpty(permissionCode), "权限编码不能为空！");
            permissionCode = permissionCode.trim();
            String firstStr = defaultMatcher;
            String secondStr = defaultMatcher;
            String thirdStr = defaultMatcher;
            if (!StringUtils.isEmpty(permissionCode)) {
                String[] arr = permissionCode.split(WILDCARD_PERMISSION_SEPARATOR);
                int length = arr.length;
                if (length == 1) {
                    firstStr = arr[0];
                }
                if (length == 2) {
                    secondStr = arr[1];
                }
                if (length == 3) {
                    thirdStr = arr[2];
                }
            }
            if (firstStr.contains(defaultMatcher)) {
                index++;
            }
            if (secondStr.contains(defaultMatcher)) {
                index++;
            }
            if (thirdStr.contains(defaultMatcher)) {
                index++;
            }
            first = newMatcher(firstStr);
            second = newMatcher(secondStr);
            third = newMatcher(thirdStr);
        }

        Matcher newMatcher(String str) {
            if (str.equalsIgnoreCase(defaultMatcher)) {
                return treeMatcher;
            } else {
                return new PatternMatcher(str);
            }
        }

        public boolean match(String code) {
            if (!StringUtils.isEmpty(code)) {
                code = code.trim();
                String[] arr = code.split(":");
                int length = arr.length;
                if (length == 1) {
                    return first.match(arr[0]);
                } else if (length == 2) {
                    return first.match(arr[0])
                            && second.match(arr[1]);
                } else if (length == 3) {
                    return first.match(arr[0])
                            && second.match(arr[1])
                            && third.match(arr[2]);
                }
            }
            return false;
        }

        @Override
        public int compareTo(MatcherItem o) {
            return index - o.index;
        }
    }


    /**
     * 工具类
     */
    interface Matcher {
        boolean match(String str);

    }

    Matcher treeMatcher = new Matcher() {
        @Override
        public boolean match(String str) {
            return true;
        }
    };

    class PatternMatcher implements Matcher {
        Pattern pattern;

        public PatternMatcher(String reg) {
            pattern = Pattern.compile(reg);
        }

        @Override
        public boolean match(String str) {
            return pattern.matcher(str).matches();
        }
    }

}