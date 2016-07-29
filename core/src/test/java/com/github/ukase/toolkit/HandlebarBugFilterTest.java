package com.github.ukase.toolkit;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by User on 29.07.2016.
 */
public class HandlebarBugFilterTest {

    HandlebarBugFilter handlebarBugFilter = new HandlebarBugFilter();

    @Test
    public void test1() {
        String example1 = "likglkfdsalewaj sdlfjdslkfds &amp dsasadsa &lt; asddffdshgdgfterwqq &quot dsafsafd &quot ";

        String res1 = handlebarBugFilter.doFilter(example1);

        Assert.assertEquals(res1, "likglkfdsalewaj sdlfjdslkfds &amp; dsasadsa &lt; asddffdshgdgfterwqq &quot; dsafsafd &quot; ");
    }
}