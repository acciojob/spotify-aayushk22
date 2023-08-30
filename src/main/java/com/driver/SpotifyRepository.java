package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        boolean flag = false;
        Artist req = new Artist(artistName);

        for (Artist artist: artists) {
            if (Objects.equals(artist.getName(), artistName)) {
                flag = true;
                req = artist;
                break;
            }
        }

        if (!flag) {
            artists.add(req);
        }

        Album album = new Album(title);
        albums.add(album);

        List<Album> albums1 = new ArrayList<>();

        if (artistAlbumMap.containsKey(req)) {
            albums1 = artistAlbumMap.get(req);
        }

        albums1.add(album);

        artistAlbumMap.put(req,albums1);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title,length);
        songs.add(song);

        boolean flag = false;
        Album req = null;
        for (Album album: albums) {
            if (Objects.equals(album.getTitle(), albumName)) {
                flag = true;
                req = album;
                break;
            }
        }

        if (!flag) throw new Exception("Album does not exist");

        List<Song> songs1 = new ArrayList<>();

        if (albumSongMap.containsKey(req)) {
            songs1 = albumSongMap.get(req);
        }

        songs1.add(song);
        albumSongMap.put(req,songs1);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> toBeAdded = new ArrayList<>();
        for (Song song: songs) {
            if (song.getLength() == length) {
                toBeAdded.add(song);
            }
        }

        playlistSongMap.put(playlist,toBeAdded);

        User req = null;
        for (User user: users) {
            if (Objects.equals(user.getMobile(), mobile)) {
                req = user;
                break;
            }
        }

        if (req == null) throw new Exception("User does not exist");

        List<User> l = new ArrayList<>();
        l.add(req);
        playlistListenerMap.put(playlist,l); //not sure about this one

        creatorPlaylistMap.put(req,playlist);

        List<Playlist> pL = new ArrayList<>();
        pL.add(playlist);
        userPlaylistMap.put(req,pL);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> toBeAdded = new ArrayList<>();
        for (Song song: songs) {
            if (songTitles.contains(song.getTitle())) {
                toBeAdded.add(song);
            }
        }
        playlistSongMap.put(playlist,toBeAdded);

        User req = null;
        for (User user: users) {
            if (Objects.equals(user.getMobile(), mobile)) {
                req = user;
                break;
            }
        }

        if (req == null) throw new Exception("User does not exist");

        List<User> l = new ArrayList<>();
        l.add(req);
        playlistListenerMap.put(playlist,l); //not sure about this one

        creatorPlaylistMap.put(req,playlist);

        List<Playlist> pL = new ArrayList<>();
        pL.add(playlist);
        userPlaylistMap.put(req,pL);

        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist req1 = null;
        for (Playlist p: playlists) {
            if (Objects.equals(p.getTitle(),playlistTitle)) {
                req1 = p;
                break;
            }
        }

        User req2 = null;
        for (User u: users) {
            if (Objects.equals(u.getMobile(),mobile)) {
                req2 = u;
                break;
            }
        }

        if(req1 == null) throw new Exception("Playlist does not exist");
        if (req2 == null) throw new Exception("User does not exist");

        if (creatorPlaylistMap.containsKey(req2)) {
            return req1;
        }

        if (playlistListenerMap.get(req1).contains(req2)) {
            return req1;
        }

        List<User> users1 = new ArrayList<>();
        if (playlistListenerMap.containsKey(req1)) {
            users1 = playlistListenerMap.get(req1);
        }

        users1.add(req2);

        playlistListenerMap.put(req1,users1);

        List<Playlist> playlists1 = new ArrayList<>();
        if (userPlaylistMap.containsKey(req2)) {
            playlists1 = userPlaylistMap.get(req2);
        }

        playlists1.add(req1);
        userPlaylistMap.put(req2,playlists1);

        return req1;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song song = null;
        for (Song s: songs) {
            if (Objects.equals(s.getTitle(),songTitle)) {
                song = s;
            }
        }

        User user = null;
        for (User u: users) {
            if (Objects.equals(u.getMobile(),mobile)) {
                user = u;
            }
        }

        if (user == null) throw new Exception("User does not exist");

        if (song == null) throw new Exception("Song does not exist");

        List<User> userList = new ArrayList<>();
        boolean flag = false;

        if (songLikeMap.containsKey(song)) {
            userList = songLikeMap.get(song);
        }

        if (userList.size()>0 && userList.contains(user)) {
            return song;
        }

        userList.add(user);
        songLikeMap.put(song,userList);
        int currLikes = song.getLikes() + 1;
        song.setLikes(currLikes);

        Album album = null;

        for (Album a: albumSongMap.keySet()) {
            if (albumSongMap.get(a).contains(song)) {
                album = a;
                break;
            }
        }

        Artist artist = null;

        for (Artist a: artistAlbumMap.keySet()) {
            if (artistAlbumMap.get(a).contains(album)) {
                artist = a;
                break;
            }
        }

        if (artist != null) artist.setLikes(artist.getLikes()+1);

        return song;
    }

    public String mostPopularArtist() {

        String ans = "";
        int maxLikes = 0;

        for (Artist artist: artists) {
            if (artist.getLikes() > maxLikes) {
                maxLikes = artist.getLikes();
                ans = artist.getName();
            }
        }

        return ans;
    }

    public String mostPopularSong() {
        String ans = "";
        int maxLikes = 0;

        for (Song song: songs) {
            if (song.getLikes() > maxLikes) {
                maxLikes = song.getLikes();
                ans = song.getTitle();
            }
        }

        return ans;
    }
}
