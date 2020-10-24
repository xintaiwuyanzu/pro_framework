package com.dr.framework.core.security.bo;

import com.dr.framework.core.security.entity.Permission;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 权限持有对象，封装权限相关的操作
 *
 * @author dr
 */
public class PermissionHolder implements Comparable<PermissionHolder> {
    private Permission permission;
    private int order;
    static final String defaultMatcher = "*";
    static final String partSplit = ":";
    static final String groupSplit = ";";
    List<PermissionMatcher> permissionMatchers;

    public PermissionHolder(Permission permission) {
        this.permission = permission;
        Assert.isTrue(!StringUtils.isEmpty(permission.getCode()), "权限编码不能为空！");
        permissionMatchers = Stream.of(permission.getCode().split(groupSplit))
                .map(PermissionMatcher::new)
                .sorted()
                .collect(Collectors.toList());
        order = permissionMatchers.get(0).index;
    }

    public boolean match(String permission) {
        for (PermissionMatcher matcher : permissionMatchers) {
            if (matcher.match(permission)) {
                return true;
            }
        }
        return false;
    }

    public Permission getPermission() {
        return permission;
    }

    @Override
    public int compareTo(PermissionHolder o) {
        return order - o.order;
    }

    class PermissionMatcher implements Comparable<PermissionMatcher> {
        int index = 0;
        Pattern first, second, third;

        PermissionMatcher(String permissionCode) {
            Assert.isTrue(!StringUtils.isEmpty(permissionCode), "权限编码不能为空！");
            String firstStr = defaultMatcher;
            String secondStr = defaultMatcher;
            String thirdStr = defaultMatcher;
            if (!StringUtils.isEmpty(permissionCode)) {
                String[] arr = permissionCode.split(partSplit);
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
            first = Pattern.compile(firstStr);
            second = Pattern.compile(secondStr);
            third = Pattern.compile(thirdStr);
        }

        public boolean match(String code) {
            if (!StringUtils.isEmpty(code)) {
                String[] arr = code.split(":");
                int length = arr.length;
                if (length == 1) {
                    return first.matcher(arr[0]).matches();
                } else if (length == 2) {
                    return first.matcher(arr[0]).matches()
                            && second.matcher(arr[1]).matches();
                } else if (length == 3) {
                    return first.matcher(arr[0]).matches()
                            && second.matcher(arr[1]).matches()
                            && third.matcher(arr[2]).matches();
                }
            }
            return false;
        }

        @Override
        public int compareTo(PermissionMatcher o) {
            return index - o.index;
        }
    }

}
