package monetization;

import fileio.input.ArtistInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMonetizationStrategy implements MonetizationStrategy {
    /**
     * Monetizes the songs listened to by a free user.
     *
     * This method calculates the revenue generated by each artist's songs listened to by the user.
     * It then updates the artist's total revenue and the revenue for each of the artist's songs.
     * Finally, it clears the list of songs the user has listened to and copies it to the list of
     * last listened songs.
     *
     * @param user The user whose listened songs are to be monetized.
     */
    @Override
    public void monetize(final UserInput user) {
        HashMap<String, Double> listensArtists = new HashMap<>();
        List<SongInput> listenedSongs = user.getFreeSongs();

        for (SongInput listenedSong : listenedSongs) {
            listensArtists.put(listenedSong.getArtist(),
                    listensArtists.getOrDefault(listenedSong.getArtist(), 0.0) + 1);
        }

        for (Map.Entry<String, Double> entry : listensArtists.entrySet()) {
            ArtistInput artist = LibraryInput.getInstance().getArtistByName(entry.getKey());

            List<SongInput> artistListenedSongs = listenedSongs.stream()
                    .filter(artistListenedSong -> artistListenedSong.getArtist()
                            .equals(artist.getUsername())).toList();

            List<Integer> adsPlayedPrices = user.getPlayer().getAdsPlayedPrices();

            double artistRevenue = (double) (adsPlayedPrices
                    .get(adsPlayedPrices.size() - 1) * artistListenedSongs.size())
                    / listenedSongs.size();

            double individualSongRevenue = artistRevenue / artistListenedSongs.size();

            artist.setSongRevenue(artistRevenue + artist.getSongRevenue());


            for (SongInput artistListenedSong : artistListenedSongs) {
                artist.getSongsRevenues().put(artistListenedSong, artist
                        .getSongsRevenues().getOrDefault(artistListenedSong, 0.0)
                        + individualSongRevenue);
            }
        }


        user.setFreeSongs(new ArrayList<>());
    }
}
