package technify;

import technify.business.*;

import static technify.business.ReturnValue.*;

import technify.data.DBConnector;
import technify.data.PostgreSQLErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Solution {

    public static void createTables() {

    }


    public static void clearTables()
    {

    }


    public static void dropTables()
    {

    }


    public static ReturnValue addUser(User user)
    {

        return null;

    }

    public static User getUserProfile(Integer userId)
    {

        return null;
    }

    public static ReturnValue deleteUser(User user)
    {
        return null;
    }

    public static ReturnValue updateUserPremium(Integer userId)
    {
        return null;
    }

    public static ReturnValue updateUserNotPremium(Integer userId)
    {
        return null;
    }
    public static ReturnValue addSong(Song song)
    {
        return null;
    }

    public static Song getSong(Integer songId)
    {
        return null;
    }

    public static ReturnValue deleteSong(Song song)
    {
        return null;
    }

    public static ReturnValue updateSongName(Song song)
    {
        return null;
    }

    public static ReturnValue addPlaylist(Playlist playlist)
    {
        return null;
    }

    public static Playlist getPlaylist(Integer playlistId)
    {
        return null;
    }

    public static ReturnValue deletePlaylist(Playlist playlist)
    {
        return null;
    }

    public static ReturnValue updatePlaylist(Playlist playlist)
    {
        return null;
    }

    public static ReturnValue addSongToPlaylist(Integer songid, Integer playlistId){
        return null;
    }

    public static ReturnValue removeSongFromPlaylist(Integer songid, Integer playlistId){
        return null;
    }

    public static ReturnValue followPlaylist(Integer userId, Integer playlistId){
        return null;
    }

    public static ReturnValue stopFollowPlaylist(Integer userId, Integer playlistId){
        return null;
    }

    public static ReturnValue songPlay(Integer songId, Integer times){
        return null;
    }

    public static Integer getPlaylistTotalPlayCount(Integer playlistId){
        return null;
    }

    public static Integer getPlaylistFollowersCount(Integer playlistId){
        return null;
    }

    public static String getMostPopularSong(){
        return null;
    }

    public static Integer getMostPopularPlaylist(){
        return null;
    }

    public static ArrayList<Integer> hottestPlaylistsOnTechnify(){
        return null;
    }

    public static ArrayList<Integer> getSimilarUsers(Integer userId){
        return null;
    }

    public static ArrayList<Integer> getTopCountryPlaylists(Integer userId) {
        return null;
    }

    public static ArrayList<Integer> getPlaylistRecommendation (Integer userId){
        return null;
    }

    public static ArrayList<Integer> getSongsRecommendationByGenre(Integer userId, String genre){
        return null;
    }


}

