package technify;

import org.junit.Test;
import technify.business.ReturnValue;
import technify.business.User;

import static org.junit.Assert.*;
import static technify.business.ReturnValue.*;

/**
 * Created by Asaf on 18-Dec-18.
 */
public class UsersTest extends AbstractTest {
    @Test
    public void testAddUser()
    {
        User v = new User();
        v.setId(-5);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        ReturnValue ret= Solution.addUser(v);
        assertEquals(BAD_PARAMS, ret);
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        ret= Solution.addUser(v);
        assertEquals(OK, ret);
        v.setId(1);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        ret= Solution.addUser(v);
        assertEquals(ALREADY_EXISTS, ret);
        v.setId(2);
        v.setName("Dani");
        v.setCountry("Israel");
        v.setPremium(true);
        ret= Solution.addUser(v);
        assertEquals(OK, ret);
    }

    @Test
    public void testGetUserProfile() {
        User user = new User();
        int id = 1;
        user.setId(id);
        user.setName("Dani");
        user.setCountry("Israel");
        user.setPremium(true);
        ReturnValue ret = Solution.addUser(user);
        assertEquals(OK, ret);
        User retrieved = Solution.getUserProfile(id);
        assertEquals(user, retrieved);
        retrieved = Solution.getUserProfile(id+1);
        assertEquals(User.badUser(), retrieved);

    }

    @Test
    public void testDeleteUser(){
        User user = new User();
        int id = 1;
        user.setId(1);
        user.setName("Dani");
        user.setCountry("Israel");
        user.setPremium(true);
        ReturnValue ret = Solution.deleteUser(user);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addUser(user);
        assertEquals(OK, ret);
        User retrieved = Solution.getUserProfile(id);
        assertEquals(user, retrieved);
        ret = Solution.deleteUser(user);
        assertEquals(OK, ret);
        retrieved = Solution.getUserProfile(id);
        assertEquals(User.badUser(), retrieved);
    }

    @Test
    public void testUpdateUserToPremium(){
        User user = new User();
        int id = 1;
        user.setId(id);
        user.setName("Dani");
        user.setCountry("Israel");
        user.setPremium(false);
        ReturnValue ret = Solution.updateUserPremium(id);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addUser(user);
        assertEquals(OK, ret);
        User retrieved = Solution.getUserProfile(id);
        assertFalse(retrieved.getPremium());
        ret = Solution.updateUserPremium(id);
        assertEquals(OK, ret);
        retrieved = Solution.getUserProfile(id);
        assertTrue(retrieved.getPremium());
        ret = Solution.updateUserPremium(id);
        assertEquals(ALREADY_EXISTS, ret);
        retrieved = Solution.getUserProfile(id);
        assertTrue(retrieved.getPremium());
    }

    @Test
    public void testUpdateUserToNotPremium(){
        User user = new User();
        int id = 1;
        user.setId(id);
        user.setName("Dani");
        user.setCountry("Israel");
        user.setPremium(true);
        ReturnValue ret = Solution.updateUserNotPremium(id);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addUser(user);
        assertEquals(OK, ret);
        User retrieved = Solution.getUserProfile(id);
        assertTrue(retrieved.getPremium());
        ret = Solution.updateUserNotPremium(id);
        assertEquals(OK, ret);
        retrieved = Solution.getUserProfile(id);
        assertFalse(retrieved.getPremium());
        ret = Solution.updateUserNotPremium(id);
        assertEquals(ALREADY_EXISTS, ret);
        retrieved = Solution.getUserProfile(id);
        assertFalse(retrieved.getPremium());
    }
}
