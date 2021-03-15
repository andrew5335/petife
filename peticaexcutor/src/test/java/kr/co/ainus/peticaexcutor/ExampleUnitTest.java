package kr.co.ainus.peticaexcutor;

import org.junit.Before;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    Executor executor;

    @Before
    public void init() {
        executor = Executor.getInstance();
    }

    @Test
    public void 동작테스트() throws Exception {

    }
}