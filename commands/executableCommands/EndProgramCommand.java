package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.MonetizationArtist;
import commands.jsonReader.MonetizationResult;
import fileio.input.ArtistInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static commands.constants.UserCommandsEnums.ROUND;

public class EndProgramCommand implements Executable {
    /**
     * This method takes a Command object as input and retrieves all artists from the library.
     * It then iterates over each artist and if the artist has fans or has made merch revenue, it
     * adds the artist's song and merch revenue to the result.
     * The method also calculates the most profitable song for each artist and adds it to the
     * result.
     * The result is then sorted by total revenue in descending order and by artist name in
     * ascending order.
     * The method also sets the rank for each artist based on their position in the sorted result.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        MonetizationResult result = new MonetizationResult();

        for (ArtistInput artist : LibraryInput.getInstance().getArtists()) {
            if (!artist.getFans().isEmpty() || artist.getMerchRevenue() > 0.0) {
                if (artist.getSongRevenue() == null) {
                    artist.setSongRevenue(0.0);
                }

                if (artist.getMerchRevenue() == null) {
                    artist.setMerchRevenue(0.0);
                }

                String username = artist.getUsername();
                result.getResult().put(username, new MonetizationArtist());
                result.getResult().get(username).setSongRevenue(artist.getSongRevenue());

                result.getResult().get(username).setMerchRevenue(artist.getMerchRevenue());
                String mostProfitableSong = calculateMostProfitableSong(artist);
                if (mostProfitableSong != null) {
                    result.getResult().get(username)
                            .setMostProfitableSong(mostProfitableSong);
                } else {
                    result.getResult().get(username).setMostProfitableSong("N/A");
                }
            }
        }

        result.setResult(result.getResult().entrySet().stream()
                .sorted(Map.Entry.<String, MonetizationArtist>comparingByValue(
                                (MonetizationArtist a1, MonetizationArtist a2) -> Double.compare(
                                        a2.getSongRevenue() + a2.getMerchRevenue(),
                                        a1.getSongRevenue() + a1.getMerchRevenue()))
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ))
        );

        int rank = 1;
        for (Map.Entry<String, MonetizationArtist> entry : result.getResult().entrySet()) {
            Double songRevenue = Math.round(entry.getValue().getSongRevenue() * ROUND) / ROUND;
            Double merchRevenue = Math.round(entry.getValue().getMerchRevenue() * ROUND) / ROUND;

            entry.getValue().setSongRevenue(songRevenue);
            entry.getValue().setMerchRevenue(merchRevenue);
            entry.getValue().setRanking(rank);
            rank++;
        }

        return result;
    }

    /**
     * Calculates the most profitable song for a given artist.
     * <p>
     * This method takes an ArtistInput object as input and iterates over the artist's premium and
     * between ads songs revenues.
     * It then compares the revenue of each song with the current most profitable song revenue and
     * updates the most profitable song and its revenue if the current song's revenue is higher.
     * If the current song's revenue is equal to the most profitable song's revenue, it updates the
     * most profitable song if the current song's name is lexicographically smaller.
     * If the most profitable song's revenue is 0.0, it returns "N/A". Otherwise, it returns the
     * name of the most profitable song.
     *
     * @param artist The artist for whom the most profitable song is to be calculated.
     * @return The name of the most profitable song for the given artist.
     */
    public String calculateMostProfitableSong(final ArtistInput artist) {
        Double mostProfitableSongRevenue = 0.0;
        String mostProfitableSong = "N/A";

        Map<String, Double> songsRevenues = new LinkedHashMap<>();
        for (Map.Entry<SongInput, Double> entry : artist.getSongsRevenues().entrySet()) {
            songsRevenues.put(entry.getKey().getName(),
                    songsRevenues.getOrDefault(entry.getKey().getName(), 0.0) + entry.getValue());
        }

        for (Map.Entry<String, Double> entry : songsRevenues.entrySet()) {
            String song = entry.getKey();
            Double revenue = entry.getValue();

            if (revenue != null) {
                if (revenue.compareTo(mostProfitableSongRevenue) > 0) {
                    mostProfitableSongRevenue = revenue;
                    mostProfitableSong = song;
                } else if (revenue.compareTo(mostProfitableSongRevenue) == 0
                        && (song.compareTo(mostProfitableSong) < 0)) {
                    mostProfitableSong = song;

                }
            }
        }

        if (mostProfitableSongRevenue == 0.0) {
            return "N/A";
        }
        return mostProfitableSong;

    }
}
