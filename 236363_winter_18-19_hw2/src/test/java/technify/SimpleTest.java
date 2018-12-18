package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static technify.business.ReturnValue.*;


public class SimpleTest extends  AbstractTest {


    @Test
    public void testCreateTables()
    {
    createTables();
    }

    @Test
    public void testClearTables()
    {
        clearTables();
    }


}