package fileio.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;
import notifications.BalanceObserver;
import pages.ArtistPage;

import notifications.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static commands.constants.PageConstants.ARTIST_PAGE;

@Getter
@Setter
public final class ArtistInput extends AccountInput implements Subject {
    private List<EventInput> events;
    private List<MerchInput> merch;
    private List<AlbumInput> artistAlbums;

    private List<BalanceObserver> subscribers = new ArrayList<>();
    private List<UserInput> fans = new ArrayList<>();

    private Double songRevenue;
    private Double merchRevenue;

    private PageInput page;

    @JsonIgnore private Map<SongInput, Double> songsRevenues;

    public ArtistInput(final Command command) {
        super(command);
        setEvents(new ArrayList<>());
        setMerch(new ArrayList<>());
        setArtistAlbums(new ArrayList<>());
        setPage(new ArtistPage(ARTIST_PAGE, getUsername()));
        setSongsRevenues(new HashMap<>());
        setSongRevenue(0.0);
        setMerchRevenue(0.0);
    }

    /**
     * The method iterates over all the albums in the library and adds to a list those that are
     * owned by this artist. It returns the albums owned by this artist.
     *
     * @return An ArrayList of AlbumInput objects that are owned by this artist.
     */
    public List<AlbumInput> getArtistAlbums() {
        List<AlbumInput> albums = new ArrayList<>();
        for (AlbumInput album : LibraryInput.getInstance().getAlbums()) {
            if (album.getOwner().equals(this.getUsername())) {
                albums.add(album);
            }
        }
        return albums;
    }

    /**
     * The method iterates over all the albums in the library and adds up the total likes of those
     * that are owned by this user. It returns the total number of likes for all albums owned by
     * this user.
     *
     * @return An Integer representing the total number of likes for all albums owned by this user.
     */
    public Integer getTotalLikes() {
        Integer totalLikes = 0;

        for (AlbumInput album : LibraryInput.getInstance().getAlbums()) {
            if (album.getOwner().equals(this.getUsername())) {
                totalLikes += album.getTotalLikesAlbum();
            }
        }

        return totalLikes;
    }

    @Override
    public void subscribe(final BalanceObserver observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(final BalanceObserver observer) {
        subscribers.remove(observer);
    }

    @Override
    public void notifyObservers(final String update) {
        for (BalanceObserver observer : subscribers) {
            observer.update(update);
        }
    }
}
