package technify;

import org.junit.Test;
import technify.business.ReturnValue;
import technify.business.Playlist;

import static org.junit.Assert.*;
import static technify.business.ReturnValue.*;

/**
 * Created by Asaf on 18-Dec-18.
 */
public class PlaylistsTest extends AbstractTest {
    private static final int ID = 1;
    private static final int ID2 = 2;
    private static final int BAD_ID = -1;
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String DESCRIPTION2 = "DESCRIPTION2";
    private static final String GENRE = "GENRE1";

    @Test
    public void testAddPlaylist()
    {
        Playlist playlist = new Playlist();
        playlist.setId(BAD_ID);
        playlist.setGenre(GENRE);
        playlist.setDescription(DESCRIPTION);
        ReturnValue ret = Solution.addPlaylist(playlist);
        assertEquals(BAD_PARAMS, ret);
        playlist.setId(ID);
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        ret = Solution.addPlaylist(playlist);
        assertEquals(ALREADY_EXISTS, ret);
        playlist.setId(ID2);
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
    }

    @Test
    public void testGetPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setId(ID);
        playlist.setGenre(GENRE);
        playlist.setDescription(DESCRIPTION);
        Playlist retrieved = Solution.getPlaylist(ID);
        assertEquals(Playlist.badPlaylist(), retrieved);
        ReturnValue ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        retrieved = Solution.getPlaylist(ID);
        assertEquals(playlist, retrieved);
        retrieved = Solution.getPlaylist(BAD_ID);
        assertEquals(Playlist.badPlaylist(), retrieved);
    }

    @Test
    public void testDeletePlaylist(){ //TODO: TEST THIS DOES NOT DELETE SONGS
        Playlist playlist = new Playlist();
        playlist.setId(ID);
        playlist.setGenre(GENRE);
        playlist.setDescription(DESCRIPTION);
        ReturnValue ret = Solution.deletePlaylist(playlist);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        Playlist retrieved = Solution.getPlaylist(ID);
        assertEquals(playlist, retrieved);
        ret = Solution.deletePlaylist(playlist);
        assertEquals(OK, ret);
        retrieved = Solution.getPlaylist(ID);
        assertEquals(Playlist.badPlaylist(), retrieved);
    }

    @Test
    public void testUpdatePlaylistName(){
        Playlist playlist = new Playlist();
        playlist.setId(ID);
        playlist.setGenre(GENRE);
        playlist.setDescription(null);
        ReturnValue ret = Solution.updatePlaylist(playlist); //TODO: NOT EXISTS BEFORE BAD_PARAMS?
        assertEquals(NOT_EXISTS, ret);
        playlist.setDescription(DESCRIPTION);
        ret = Solution.updatePlaylist(playlist);
        assertEquals(NOT_EXISTS, ret);
        ret = Solution.addPlaylist(playlist);
        assertEquals(OK, ret);
        Playlist retrieved = Solution.getPlaylist(ID);
        assertEquals(DESCRIPTION, retrieved.getDescription());
        playlist.setDescription(null);
        ret = Solution.updatePlaylist(playlist);
        assertEquals(BAD_PARAMS, ret);
        playlist.setDescription(DESCRIPTION2);
        ret = Solution.updatePlaylist(playlist);
        assertEquals(OK, ret);
        retrieved = Solution.getPlaylist(ID);
        assertEquals(DESCRIPTION2, retrieved.getDescription());
        ret = Solution.updatePlaylist(playlist);
        assertEquals(OK, ret); //TODO: IS THIS CORRECT?
    }
}
