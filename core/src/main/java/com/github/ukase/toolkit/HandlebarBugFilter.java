package com.github.ukase.toolkit;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alvov on 29.07.2016.
 */
@Service
public class HandlebarBugFilter {

    private Pattern pattern = Pattern.compile("(&[a-z0-9]{2,6})[^;a-z0-9|$]");

    public String doFilter(String str) {
        String result = str;

        Matcher matcher = pattern.matcher(result);

        while (matcher.find()) {
            result = result.replaceAll("(" + matcher.group(1) + ")([^;])", "$1;$2");
        }

        return result;
    }
}