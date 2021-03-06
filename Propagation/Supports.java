package com.test.SpringTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FirstService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SecondService secondService;


    // @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void goMethod() throws Exception {

        jdbcTemplate.batchUpdate("INSERT INTO TB_MYTEST(MYKEY, MYVALUE) VALUES ('start', 'start')");

        for (int i = 1; i <= 5; i++) {

            try {

                secondService.subMethod(i);

            } catch (Exception e) {

                System.err.println(e);

            }

        }

        jdbcTemplate.batchUpdate("INSERT INTO TB_MYTEST(MYKEY, MYVALUE) VALUES ('end', 'end')");

    }

}

----------------------------------------------------------------------------------------------------------------

package com.test.SpringTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecondService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public void subMethod(int i) throws Exception {

        String sqlString = String.format("INSERT INTO TB_MYTEST(MYKEY, MYVALUE) VALUES ('%d', '%d')", i, i);
        jdbcTemplate.batchUpdate(sqlString);

        if (i % 2 == 0) {

            throw new Exception("Failure");

        }

    }

}

----------------------------------------------------------------------------------------------------------------

/*
 *  Result: (If DataSource AutoCommit set to true)
 *  | MYKEY | MYVALUE |
 *  | start |  start  |
 *  |   1   |    1    |
 *  |   2   |    2    |
 *  |   3   |    3    |
 *  |   4   |    4    |
 *  |   5   |    5    |
 *  |  end  |   end   |
 *
 *  Result: (If DataSource AutoCommit set to false)
 *  | MYKEY | MYVALUE |
 * 
 */
