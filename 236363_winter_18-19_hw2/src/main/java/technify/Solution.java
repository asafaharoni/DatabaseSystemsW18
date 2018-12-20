package technify;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import technify.business.Playlist;
import technify.business.ReturnValue;
import technify.business.Song;
import technify.business.User;
import technify.data.DBConnector;
import technify.data.PostgreSQLErrorCodes;


public class Solution {

    // TABLE NAMES
    private static final String SONG_TABLE_NAME = "Songs ";
    private static final String USERS_TABLE_NAME = "Users ";
    private static final String PLAYLISTS_TABLE_NAME = "Playlists ";
    private static final String INCLUDES_TABLE_NAME = "Includes ";
    private static final String FOLLOWS_TABLE_NAME = "Follows ";
    private static final String PLAYLISTS_SONGS_VIEW = "PlaylistsSongs ";
//    private static final String PLAYLISTS_SONGS_VIEW = "PlaylistsUsers ";
    private static final String PLAYLIST_RATING_VIEW = "PlaylistRating ";
    // COMMANDS
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String CREATE_VIEW = "CREATE VIEW ";
    private static final String SELECT_ALL_FIELDS = "SELECT * ";
    private static final String SELECT = "SELECT ";
    // STRING RESULTS
    private static final String NO_SONGS = "No songs";


    public static void createTables()
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try{
            pstmt = connection.prepareStatement(CREATE_TABLE + SONG_TABLE_NAME +
                    "(\n" +
                    "    id integer,\n" +
                    "    name text NOT NULL,\n" +
                    "    genre text NOT NULL,\n" +
                    "    country text,\n" +
                    "    playCount integer DEFAULT 0,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    CHECK (id > 0),\n" +
                    "    CHECK (playCount >= 0)\n" +
                    ");");
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_TABLE + USERS_TABLE_NAME +
                    "(\n" +
                    "    id integer,\n" +
                    "    name text NOT NULL,\n" +
                    "    country text NOT NULL,\n" +
                    "    premium boolean NOT NULL,\n" +
                    "    PRIMARY KEY (id),\n" +
                    "    CHECK (id > 0)\n" +
                    ");");
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_TABLE + PLAYLISTS_TABLE_NAME +
                    "(\n" +
                    "    id integer,\n" +
                    "    genre text NOT NULL," +
                    "    description text NOT NULL," +
                    "    PRIMARY KEY (id),\n" +
                    "    CHECK (id > 0)\n" +
                    ")");
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_TABLE + INCLUDES_TABLE_NAME +
                    "(\n" +
                    "    pid integer NOT NULL,\n" +
                    "    sid integer NOT NULL,\n" +
                    "    CONSTRAINT song_exists FOREIGN KEY (sid) REFERENCES " + SONG_TABLE_NAME + "(id) ON DELETE CASCADE,\n" +
                    "    CONSTRAINT playlist_exists FOREIGN KEY (pid) REFERENCES " + PLAYLISTS_TABLE_NAME + "(id) ON DELETE CASCADE,\n" +
                    "    PRIMARY KEY (sid,pid)" +
                    ")");
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_TABLE + FOLLOWS_TABLE_NAME +
                    "(\n" +
                    "    uid integer NOT NULL,\n" +
                    "    pid integer NOT NULL ,\n" +
                    "    CONSTRAINT user_exists FOREIGN KEY (uid) REFERENCES " + USERS_TABLE_NAME + "(id) ON DELETE CASCADE,\n" +
                    "    CONSTRAINT playlist_exists FOREIGN KEY (pid) REFERENCES " + PLAYLISTS_TABLE_NAME + "(id) ON DELETE CASCADE,\n" +
                    "    PRIMARY KEY (uid,pid)" +
                    ")");
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_VIEW + PLAYLISTS_SONGS_VIEW + "as " +
                    SELECT + "id as pid, sid, name, " + PLAYLISTS_TABLE_NAME + ".genre, country, COALESCE(playcount, 0) as playcount, description\n" +
                            "from " + PLAYLISTS_TABLE_NAME + " LEFT JOIN (\n" +
                            "\tSELECT pid, sid, name, genre, country, playcount\n" +
                            "\tFROM " + SONG_TABLE_NAME + " S JOIN " + INCLUDES_TABLE_NAME + " P\n" +
                            "\tON S.id = P.sid\n" +
                            ") AS playlistssongs\n" +
                            "on playlistssongs.pid = " + PLAYLISTS_TABLE_NAME + ".id"
                        );
            pstmt.execute();
            pstmt = connection.prepareStatement(CREATE_VIEW + PLAYLIST_RATING_VIEW + "as " +
                    "    SELECT pid, songcount, playcount, COALESCE(playcount/NULLIF(songcount, 0), 0) as rating FROM ( \n" +
                    "       SELECT pid, COUNT(sid) as songcount, SUM(playcount) as playcount\n" +
                    "       FROM PlaylistsSongs \n" +
                    "       GROUP BY pid\n) AS counters");
            pstmt.execute();
            //TODO: check if DELETE ON CASCADE works
        } catch (SQLException var15) {
            System.out.println("error in createTables: " + var15);
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException var14) {
                System.out.println("error in createTables(finally,pstmt): " + var14);
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException var13) {
                System.out.println("error in createTables(finally,connection): " + var13);
                //e.printStackTrace()();
            }
        }
    }

    public static void clearTables()
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + SONG_TABLE_NAME);
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE FROM " + USERS_TABLE_NAME);
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE FROM " + PLAYLISTS_TABLE_NAME);
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE FROM " + INCLUDES_TABLE_NAME);
            pstmt.execute();
            pstmt = connection.prepareStatement("DELETE FROM " + FOLLOWS_TABLE_NAME);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void dropTables()
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + SONG_TABLE_NAME + "CASCADE");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + USERS_TABLE_NAME + "CASCADE");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + PLAYLISTS_TABLE_NAME + "CASCADE");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + INCLUDES_TABLE_NAME + "CASCADE");
            pstmt.execute();
            pstmt = connection.prepareStatement("DROP TABLE IF EXISTS " + FOLLOWS_TABLE_NAME + "CASCADE");
            pstmt.execute();
        } catch (SQLException e) {
            //e.printStackTrace()();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace()();
            }
            try {
                connection.close();
            } catch (SQLException e) {

            }
        }
    }

    public static ReturnValue addUser(User user)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + USERS_TABLE_NAME +
                    " VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getCountry());
            pstmt.setBoolean(4, user.getPremium());

            pstmt.execute();
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.BAD_PARAMS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static User getUserProfile(Integer userId)
    {
        User user = new User();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(SELECT_ALL_FIELDS +
                    "FROM " + USERS_TABLE_NAME + " " +
                    "WHERE id = (?)");
            pstmt.setInt(1, userId);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                user.setId(res.getInt(1));
                user.setName(res.getString(2));
                user.setCountry(res.getString(3));
                user.setPremium(res.getBoolean(4));
            } else {
                user = User.badUser();
            }
            res.close();
        } catch (SQLException e) {
            return User.badUser();
        } catch (NullPointerException e) {
            return User.badUser();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return User.badUser();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return User.badUser();
            }
        }
        return user;
    }

    public static ReturnValue deleteUser(User user)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + USERS_TABLE_NAME + //", " + FOLLOWS_TABLE_NAME +
                    " WHERE id=?");
            pstmt.setInt(1, user.getId());
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.BAD_PARAMS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue updateUserPremium(Integer userId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "UPDATE " + USERS_TABLE_NAME +
                            "SET premium=?\n" +
                            "WHERE id=(?)");
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, userId);
            User user = getUserProfile(userId);
            if (user.getPremium() && !user.equals(User.badUser())) {
                return ReturnValue.ALREADY_EXISTS;
            }
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.BAD_PARAMS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue updateUserNotPremium(Integer userId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "UPDATE " + USERS_TABLE_NAME +
                            "SET premium=?\n" +
                            "WHERE id=?");
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, userId);
            User user = getUserProfile(userId);
            if (!user.getPremium() && !user.equals(User.badUser())) {
                return ReturnValue.ALREADY_EXISTS;
            }
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.BAD_PARAMS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue addSong(Song song)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + SONG_TABLE_NAME +
                    "VALUES (?, ?, ?, ?, ?)");
            pstmt.setInt(1, song.getId());
            pstmt.setString(2, song.getName());
            pstmt.setString(3, song.getGenre());
            pstmt.setString(4, song.getCountry());
//            pstmt.setInt(5, song.getPlayCount());
            pstmt.setInt(5, 0); //TODO: CHECK

            pstmt.execute();
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static Song getSong(Integer songId)
    {
        Song song = new Song();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(SELECT_ALL_FIELDS +
                    "FROM " + SONG_TABLE_NAME +
                    "WHERE id = (?)");
            pstmt.setInt(1, songId);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                song.setId(res.getInt(1));
                song.setName(res.getString(2));
                song.setGenre(res.getString(3));
                song.setCountry(res.getString(4));
                song.setPlayCount(res.getInt(5));
            } else {
                song = Song.badSong();
            }

            res.close();
        } catch (SQLException e) {
            return Song.badSong();
        } catch (NullPointerException e) {
            return Song.badSong();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return Song.badSong();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return Song.badSong();
            }
        }
        return song;
    }

    public static ReturnValue deleteSong(Song song)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + SONG_TABLE_NAME + //", " + INCLUDES_TABLE_NAME +
                    " WHERE id=?");
            pstmt.setInt(1, song.getId());
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue updateSongName(Song song)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "UPDATE " + SONG_TABLE_NAME +
                            "SET name=?\n" +
                            "WHERE id=?");
            pstmt.setString(1, song.getName());
            pstmt.setInt(2, song.getId());
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue addPlaylist(Playlist playlist)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + PLAYLISTS_TABLE_NAME +
                    " VALUES (?, ?, ?)");
            pstmt.setInt(1, playlist.getId());
            pstmt.setString(2, playlist.getGenre());
            pstmt.setString(3, playlist.getDescription());

            pstmt.execute();
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static Playlist getPlaylist(Integer playlistId)
    {
        Playlist playlist = new Playlist();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(SELECT_ALL_FIELDS +
                    "FROM " + PLAYLISTS_TABLE_NAME +
                    "WHERE id = (?)");
            pstmt.setInt(1, playlistId);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                playlist.setId(res.getInt(1));
                playlist.setGenre(res.getString(2));
                playlist.setDescription(res.getString(3));
            }

            res.close();
        } catch (SQLException e) {
            return Playlist.badPlaylist();
        } catch (NullPointerException e) {
            return Playlist.badPlaylist();
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return Playlist.badPlaylist();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return Playlist.badPlaylist();
            }
        }
        return playlist;
    }

    public static ReturnValue deletePlaylist(Playlist playlist)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + PLAYLISTS_TABLE_NAME + //", " + INCLUDES_TABLE_NAME +
                    " WHERE id=?");
            pstmt.setInt(1, playlist.getId());
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue updatePlaylist(Playlist playlist)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(
                    "UPDATE " + PLAYLISTS_TABLE_NAME +
                            "SET description=?\n" +
                            "WHERE id=?");
            pstmt.setString(1, playlist.getDescription());
            pstmt.setInt(2, playlist.getId());
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue addSongToPlaylist(Integer songid, Integer playlistId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        Song song = getSong(songid);
        Playlist playlist = getPlaylist(playlistId);
        if (song.getGenre() == null || !song.getGenre().equals(playlist.getGenre())) {
            return ReturnValue.BAD_PARAMS; //TODO: check if allowed
        }
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + INCLUDES_TABLE_NAME +
                    " VALUES (?, ?)");
            pstmt.setInt(1,playlistId);
            pstmt.setInt(2,songid);


            pstmt.execute();

        } catch (SQLException e) {
            if (sqlStateMatches(e, PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION)) {
                return ReturnValue.BAD_PARAMS; //TODO: check
            }
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.BAD_PARAMS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue removeSongFromPlaylist(Integer songid, Integer playlistId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + INCLUDES_TABLE_NAME +
                    " WHERE sid=? AND pid=?");
            pstmt.setInt(1, songid);
            pstmt.setInt(2, playlistId);
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.NOT_EXISTS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue followPlaylist(Integer userId, Integer playlistId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("INSERT INTO " + FOLLOWS_TABLE_NAME +
                    " VALUES (?, ?)");
            pstmt.setInt(1,userId);
            pstmt.setInt(2,playlistId);


            pstmt.execute();

        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.NOT_EXISTS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue stopFollowPlaylist(Integer userId, Integer playlistId)
    {
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("DELETE FROM " + FOLLOWS_TABLE_NAME +
                    " WHERE uid=? AND pid=?");
            pstmt.setInt(1, userId);
            pstmt.setInt(2, playlistId);
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        } catch (NullPointerException e) {
            return ReturnValue.NOT_EXISTS;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static ReturnValue songPlay(Integer songId, Integer times)
    {
        Connection connection = DBConnector.getConnection();
        Song s = new Song();
        if (songId == null) {
            return ReturnValue.BAD_PARAMS; //TODO: check
        }
        s.setId(songId);
        PreparedStatement songPlayCount = null;
        try {
            songPlayCount = connection.prepareStatement("SELECT playCount " +
                    "FROM " + SONG_TABLE_NAME +
                    "WHERE id = (?)");
            songPlayCount.setInt(1, songId);
            ResultSet res = songPlayCount.executeQuery();
            if (res.next()) {
                s.setPlayCount(res.getInt(1));
            }
            res.close();
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }

        PreparedStatement pstmt = null;
        int newPlayCount = s.getPlayCount() + times;

        try {
            pstmt = connection.prepareStatement(
                    "UPDATE " + SONG_TABLE_NAME +
                            "SET playCount=?\n" +
                            "WHERE id=?");
            pstmt.setInt(1,newPlayCount);
            pstmt.setInt(2,songId); //TODO: WHY playcount + times DOESNT WORK?
            int count = pstmt.executeUpdate();
            if (count == 0) {
                return ReturnValue.NOT_EXISTS;
            }
        } catch (SQLException e) {
            return sqlExceptionToReturnValue(e);
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return ReturnValue.ERROR;
            }
        }
        return ReturnValue.OK;
    }

    public static Integer getPlaylistTotalPlayCount(Integer playlistId)
    {
        int count = 0;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT playcount " +
                    "FROM " + PLAYLIST_RATING_VIEW +
                    "WHERE pid=?");
            pstmt.setInt(1, playlistId);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                count = res.getInt(1);
            }

            res.close();
        } catch (SQLException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return 0;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return 0;
            }
        }
        return count;
    }

    public static Integer getPlaylistFollowersCount(Integer playlistId)
    {
        int count = 0;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT COUNT(uid)" +
                    "FROM " + FOLLOWS_TABLE_NAME +
                    "WHERE pid=?");
            pstmt.setInt(1, playlistId);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                count = res.getInt(1);
            }

            res.close();
        } catch (SQLException e) {
            return 0;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return 0;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return 0;
            }
        }
        return count;
    }

    public static String getMostPopularSong(){
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        String name = null;
        try {
            pstmt = connection.prepareStatement(SELECT + "name, sid, COUNT(pid) as playlistcount " +
                    "FROM " + PLAYLISTS_SONGS_VIEW +
                    "GROUP BY sid, name" +
                    "ORDER BY playlistcount DESC, sid DESC " +
                    "LIMIT 1");

            ResultSet res = pstmt.executeQuery();

            if (getIncludesCount() == 0) {
                name = NO_SONGS;
            } else if (res.next()) {
                name = res.getString(1);
            }

            res.close();
        } catch (SQLException e) {
            name =  null;
        } catch (NullPointerException e) {
            name =  null;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                name =  null;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                name =  null;
            }
        }
        return name;
    }

    public static Integer getMostPopularPlaylist(){
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        Integer pid = 0;
        try {
            pstmt = connection.prepareStatement(SELECT + "pid, SUM(playcount) as playcount " +
                    "FROM " + PLAYLISTS_SONGS_VIEW +
                    "GROUP BY pid " +
                    "ORDER BY playcount DESC, pid DESC " +
                    "LIMIT 1");

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                pid = res.getInt(1);
            }

            res.close();
        } catch (SQLException e) {
            pid =  0;
        } catch (NullPointerException e) {
            pid =  0;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                pid =  0;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                pid =  0;
            }
        }
        return pid;
    }

    public static ArrayList<Integer> hottestPlaylistsOnTechnify(){
        ArrayList<Integer> arr = new ArrayList<>();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(SELECT + "pid FROM " + PLAYLIST_RATING_VIEW +
                    "WHERE playcount > 0 " +
                    "ORDER BY rating DESC, pid ASC " +
                    "LIMIT 10");

            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int s = res.getInt(1);
                arr.add(s);
            }

            res.close();
        } catch (SQLException e) {
            return arr;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return arr;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return arr;
            }
        }
        return arr;
    }

    public static ArrayList<Integer> getSimilarUsers(Integer userId)
    {
        ArrayList<Integer> arr = new ArrayList<>();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        String relevantPlaylistQuery = "(SELECT L2.uid AS uid, COUNT(L2.pid) AS c" +
                " FROM " + FOLLOWS_TABLE_NAME + " L1," + FOLLOWS_TABLE_NAME + " L2" +
                " WHERE L1.uid=? AND L2.uid<>? AND L1.pid=L2.pid \n" +
                " GROUP BY L2.uid)";

        String userCountQuery = "(SELECT COUNT(pid) FROM " + FOLLOWS_TABLE_NAME + " WHERE uid=?)*0.75";
        try {
            pstmt = connection.prepareStatement(
                    "SELECT R.uid FROM "+ relevantPlaylistQuery +" AS R\n" +
                            " WHERE R.c>="+ userCountQuery + " \n" +
                            " ORDER BY R.uid ASC" +
                            " LIMIT 10");
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);

            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int s = res.getInt(1);
                arr.add(s);
            }

            res.close();
        } catch (SQLException e) {
            return arr;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return arr;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return arr;
            }
        }
        return arr;
    }

    public static ArrayList<Integer> getTopCountryPlaylists(Integer userId) {
        ArrayList<Integer> arr = new ArrayList<>();
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        User user = getUserProfile(userId);
        if (user.equals(User.badUser()) || !user.getPremium()) {
            return arr;
        }
        try {
            pstmt = connection.prepareStatement(SELECT + "pid\nFROM (\n" +
                    "   SELECT L1.pid, SUM(playcount) as playcount\n" +
                    "   FROM " + PLAYLIST_RATING_VIEW + "L1 RIGHT JOIN (\n" +
                    "           SELECT pid\n" +
                    "           FROM " + PLAYLISTS_SONGS_VIEW + "\n" +
                    "           WHERE country=?\n" +
                    "       ) AS L2 \n" +
                    "   ON L1.pid = L2.pid\n" +
                    "   WHERE L1.pid = L2.pid\n" +
                    "   GROUP BY L1.pid \n" +
                    ") AS L3\n" +
                    "ORDER BY playcount DESC, pid ASC \n" +
                    "LIMIT 10");
            pstmt.setString(1, user.getCountry());

            ResultSet res = pstmt.executeQuery();

            while (res.next()) {
                int s = res.getInt(1);
                arr.add(s);
            }

            res.close();
        } catch (SQLException e) {
            return arr;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return arr;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return arr;
            }
        }
        return arr;
    }

    public static ArrayList<Integer> getPlaylistRecommendation (Integer userId){
        return null;
    }

    public static ArrayList<Integer> getSongsRecommendationByGenre(Integer userId, String genre){
        return null;
    }
    private static ReturnValue sqlExceptionToReturnValue(SQLException e) {
        if (sqlStateMatches(e, PostgreSQLErrorCodes.FOREIGN_KEY_VIOLATION)) {
            return ReturnValue.NOT_EXISTS;
        } else if (sqlStateMatches(e, PostgreSQLErrorCodes.UNIQUE_VIOLATION)) {
            return ReturnValue.ALREADY_EXISTS;
        } else if (sqlStateMatches(e, PostgreSQLErrorCodes.CHECK_VIOLATION) || sqlStateMatches(e, PostgreSQLErrorCodes.NOT_NULL_VIOLATION)) {
            return ReturnValue.BAD_PARAMS;
        } else {
            return ReturnValue.ERROR;
        }
    }

    private static boolean sqlStateMatches(SQLException e, PostgreSQLErrorCodes error) {
        String errorString = Integer.toString(error.getValue());
        return e.getSQLState().equals(errorString);
    }

    private static Integer getIncludesCount() {
        Integer count = 0;
        Connection connection = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement("SELECT COUNT(*)" +
                    "FROM " + INCLUDES_TABLE_NAME);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                count = res.getInt(1);
            }

            res.close();
        } catch (SQLException e) {
            return 0;
        }
        finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                return 0;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                return 0;
            }
        }
        return count;
    }
}

