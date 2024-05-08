package monetization;

import fileio.input.ArtistInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static commands.constants.UserCommandsEnums.CREDIT;

public class PremiumMonetizationStrategy implements MonetizationStrategy {
    /**
     * This method calculates and distributes revenue to artists based on the number of times their
     * songs have been listened to by a premium user.
     *
     * @param user The premium user whose listened songs will be used to distribute revenue. After
     * the method execution, the user will no longer be premium and their list of premium songs will
     * be cleared. A HashMap is created to store each ArtistInput and the number of times their
     * songs have been listened to and the list of songs that the premium user has listened to is
     * retrieved. For each song the corresponding artist is retrieved and counted in HashMap.
     * Then the total revenue is calculated based on the number of times their songs were listened
     * to, and the individual song revenue by dividing the total revenue by the number of songs.
     */
    @Override
    public void monetize(final UserInput user) {
        HashMap<ArtistInput, Double> listensArtists = new HashMap<>();

        List<SongInput> listenedSongs = user.getPremiumSongs();

        for (SongInput song : listenedSongs) {
            ArtistInput artist = LibraryInput.getInstance().getArtistByName(song.getArtist());

            listensArtists.put(artist, listensArtists.getOrDefault(artist, 0.0) + 1);
        }

        for (Map.Entry<ArtistInput, Double> entry : listensArtists.entrySet()) {
            ArtistInput artist = entry.getKey();

            List<SongInput> artistListenedSongs = listenedSongs.stream().filter(song ->
                    song.getArtist().equals(artist.getUsername())).toList();

            double artistRevenue = CREDIT * artistListenedSongs.size() / listenedSongs.size();
            double individualSongRevenue = artistRevenue / artistListenedSongs.size();

            artist.setSongRevenue(artistRevenue + artist.getSongRevenue());

            for (SongInput song : artistListenedSongs) {
                artist.getSongsRevenues().put(song, artist.getSongsRevenues().getOrDefault(song,
                        0.0) + individualSongRevenue);
            }
        }

        user.setPremium(false);
        user.setPremiumSongs(new ArrayList<>());
    }
}
