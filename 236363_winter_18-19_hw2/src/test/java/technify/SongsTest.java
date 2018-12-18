package technify;

import org.junit.Test;
import technify.business.ReturnValue;
import technify.business.Song;
import technify.business.Song;

import static org.junit.Assert.*;
import static technify.business.ReturnValue.*;

/**
 * Created by Asaf on 18-Dec-18.
 */
public class SongsTest extends AbstractTest {
    private static final int ID = 1;
    private static final int ID2 = 2;
    private static final int BAD_ID = -1;
    private static final String NAME = "SONG1";
    private static final String NAME2 = "SONG2";
    private static final String GENRE = "GENRE1";
    private static final String COUNTRY = "COUNTRY1";
    private static final int PLAYCOUNT = 0;

    @Test
    public void testAddSong()
    {
        Song song = new Song();
        song.setId(BAD_ID);
        song.setName(NAME);
        song.setGenre(GENRE);
        song.setCountry(COUNTRY);
        song.setPlayCount(PLAYCOUNT + 1); //TODO: SHOULD THIS BE AN ERROR?
        ReturnValue ret = Solution.addSong(song);
        assertEquals(BAD_PARAMS, ret);
        song.setId(ID);
//        ret = Solution.addSong(song);  //TODO: MAYBE UNTRUE
//        assertEquals(BAD_PARAMS, ret);
//        song.setPlayCount(PLAYCOUNT);
        ret = Solution.addSong(song);
        assertEquals(OK, ret);
        assertEquals(0, Solution.getSong(ID).getPlayCount());
        ret = Solution.addSong(song);
        assertEquals(ALREADY_EXISTS, ret);
        song.setId(ID2);
        ret = Solution.addSong(song);
        assertEquals(OK, ret);
    }

    @Test
    public void testGetSong() {
        Song song = new Song();
        song.setId(ID);
        song.setName(NAME);
        song.setGenre(GENRE);
        song.setCountry(COUNTRY);
        Song retrieved = Solution.getSong(ID);
        assertEquals(Song.badSong(), retrieved);
        ReturnValue ret = Solution.addSong(song);
        assertEquals(OK, ret);
        retrieved = Solution.getSong(ID);
        assertEquals(song, retrieved);
        retrieved = Solution.getSong(BAD_ID);
        assertEquals(Song.badSong(), retrieved);
    }

    @Test
    public void testDeleteSong(){
        Song song = new Song();
        song.setId(ID);
        song.setName(NAME);
        song.setGenre(GENRE);
        song.setCountry(COUNTRY);
        ReturnValue ret = Solution.deleteSong(song);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addSong(song);
        assertEquals(OK, ret);
        Song retrieved = Solution.getSong(ID);
        assertEquals(song, retrieved);
        ret = Solution.deleteSong(song);
        assertEquals(OK, ret);
        retrieved = Solution.getSong(ID);
        assertEquals(Song.badSong(), retrieved);
    }

    @Test
    public void testUpdateSongName(){
        Song song = new Song();
        song.setId(ID);
        song.setName(null);
        song.setGenre(GENRE);
        song.setCountry(COUNTRY);
        ReturnValue ret = Solution.updateSongName(song); //TODO: WHAT TO DO WHEN OTHER FIELDS ARE DIFFERENT?
        assertEquals(NOT_EXISTS, ret);
        song.setName(NAME);
        ret = Solution.updateSongName(song);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addSong(song);
        assertEquals(OK, ret);
        Song retrieved = Solution.getSong(ID);
        assertEquals(NAME, retrieved.getName());
        song.setName(null);
        ret = Solution.updateSongName(song);
        assertEquals(BAD_PARAMS, ret);
        song.setName(NAME2);
        ret = Solution.updateSongName(song);
        assertEquals(OK, ret);
        retrieved = Solution.getSong(ID);
        assertEquals(NAME2, retrieved.getName());
        ret = Solution.updateSongName(song);
        assertEquals(OK, ret); //TODO: IS THIS CORRECT?
    }
}
