package com.github.ukase.toolkit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Created by alvov on 29.07.2016.
 */
public class HandlebarBugFilterTest {

    private HandlebarBugFilter handlebarBugFilter;

    @Before
    public void setUp(){
        this.handlebarBugFilter = new HandlebarBugFilter();
    }

    @Test
    public void simpleBugFilterTest() {
        String example = "likglkfdsalewaj sdlfjdslkfds &amp dsasadsa &lt; asddffdshgdgfterwqq &quot dsafsafd &quot ";

        String res = handlebarBugFilter.doFilter(example);

        Assert.assertEquals(res, "likglkfdsalewaj sdlfjdslkfds &amp; dsasadsa &lt; asddffdshgdgfterwqq &quot; dsafsafd &quot; ");
    }
}