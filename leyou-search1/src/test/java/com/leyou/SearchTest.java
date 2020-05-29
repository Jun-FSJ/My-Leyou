package com.leyou;

import com.leyou.search.LeyouSearchApplication;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jun
 * @create 2020/5/29 - 10:20
 */
@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class SearchTest {
    @Autowired
    private CategoryClient categoryClient;
    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(74L, 75L, 76L));
        names.forEach(System.out::println);
    }
}
