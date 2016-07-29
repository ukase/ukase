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

        if (str != null) {
            Matcher matcher = pattern.matcher(result);

            Set<String> tokens = new HashSet<>();

            while (matcher.find()) {
                tokens.add(matcher.group(1));
            }

            for(String token: tokens) {
                result = result.replaceAll("(" + token + ")[^;]", "$1;");
            }

            return result;
        } else {
            return null;
        }
    }
}