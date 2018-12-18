package technify;

import org.junit.Test;
import technify.business.*;
import static org.junit.Assert.assertEquals;
import static technify.business.ReturnValue.*;


public class BasicAPITests extends AbstractTest {
    //GENERAL
    private static final int ID = 1;
    private static final int ID2 = 2;
    private static final int BAD_ID = -1;
    //MUSIC
    private static final String GENRE = "GENRE";
    private static final String GENRE2 = "GENRE2";
    //SONG
    private static final String S_NAME = "SONG";
    private static final String S_NAME2 = "SONG2";
    //PLAYLIST
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String DESCRIPTION2 = "DESCRIPTION2";
    //USER
    private static final String U_NAME = "USER";
    private static final String U_NAME2 = "USER2";
    //LOCATION
    private static final String COUNTRY = "COUNTRY";
    private static final String COUNTRY2 = "COUNTRY2";

    @Test
    public void songPlayTest() {
        ReturnValue res;
        Song s = new Song();
        s.setId(1);
        s.setName("Despacito");
        s.setGenre("Latin");
        s.setCountry("Spain");

        res = Solution.addSong(s);
        assertEquals(OK, res);

        res = Solution.songPlay(1, 1);
        assertEquals(OK, res);

        res = Solution.songPlay(1, -3);
        assertEquals(BAD_PARAMS, res);
    }

    @Test
    public void followPlaylistTest() {

        ReturnValue res;
        Playlist p = new Playlist();
        p.setId(10);
        p.setGenre("Pop");
        p.setDescription("Best pop songs of 2018");

        res = Solution.addPlaylist(p);
        assertEquals(OK, res);

        User u = new User();
        u.setId(100);
        u.setName("Nir");
        u.setCountry("Israel");
        u.setPremium(false);

        res = Solution.addUser(u);
        assertEquals(OK, res);

        res = Solution.followPlaylist(100, 10);
        assertEquals(OK , res);

        res = Solution.followPlaylist(100, 10);
        assertEquals(ALREADY_EXISTS , res);

        res = Solution.followPlaylist(101, 10);
        assertEquals(NOT_EXISTS , res);
    }
}

