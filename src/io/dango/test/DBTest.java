package io.dango.test;

import io.dango.config.DBConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

/**
 * Created by 54472 on 2017/7/5.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = {DBConfig.class})
public class DBTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate template;

    @Test
    public void testDataSource() {
        assertNotNull(dataSource);
    }

    @Test
    public void testJdbcTemplate() {
        template.query("SELECT username FROM user WHERE username = 'root'", resultSet -> {
            String username = resultSet.getString("username");
            System.out.println(username);

            assertNotNull(username);
        });

    }


}
