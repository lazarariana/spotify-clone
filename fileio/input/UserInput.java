package fileio.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.jsonReader.Command;
import commands.player.History;
import commands.player.Player;
import commands.searchBar.SearchBar;
import lombok.Getter;
import lombok.Setter;
import notifications.Notification;
import notifications.BalanceObserver;
import pages.HomePage;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class UserInput extends AccountInput implements BalanceObserver {

  @JsonIgnore private SearchBar searchBar;
  @JsonIgnore private Player player;
  @JsonIgnore private List<PlaylistInput> followedPlaylists;
  @JsonIgnore private List<SongInput> likedSongs;
  @JsonIgnore private List<PlaylistInput> createdPlaylists;
  private boolean online;
  private PageInput page;

  private List<MerchInput> boughtMerch;
  private boolean premium;

  private List<Notification> notifications;

  private List<String> songRecommendations;
  private List<PlaylistInput> playlistRecommendations;
  private List<PlaylistInput> fansRecommendations;
  private String lastRecommendationType;

  private List<PageInput> pageNavigationHistory;
  private int lastPageIndex;

  private History listens;

  private List<SongInput> premiumSongs;
  private List<SongInput> freeSongs;

  public UserInput() {
    searchBar = new SearchBar();
    player = new Player();
    followedPlaylists = new ArrayList<>();
    likedSongs = new ArrayList<>();
    createdPlaylists = new ArrayList<>();
    boughtMerch = new ArrayList<>();
    notifications = new ArrayList<>();
    songRecommendations = new ArrayList<>();
    playlistRecommendations = new ArrayList<>();
    fansRecommendations = new ArrayList<>();
    premiumSongs = new ArrayList<>();
    freeSongs = new ArrayList<>();
    pageNavigationHistory = new ArrayList<>();
    listens = new History();
    online = true;
    this.premium = false;
  }

  public UserInput(final Command command) {
    super(command);
    searchBar = new SearchBar();
    player = new Player();
    followedPlaylists = new ArrayList<>();
    likedSongs = new ArrayList<>();
    createdPlaylists = new ArrayList<>();
    boughtMerch = new ArrayList<>();
    songRecommendations = new ArrayList<>();
    playlistRecommendations = new ArrayList<>();
    fansRecommendations = new ArrayList<>();
    pageNavigationHistory = new ArrayList<>();
    premiumSongs = new ArrayList<>();
    freeSongs = new ArrayList<>();
    notifications = new ArrayList<>();
    listens = new History();
    online = true;
    setPage(new HomePage(this));
    this.premium = false;
  }

    @Override
    public void update(final String update) {
      if (notifications == null) {
        notifications = new ArrayList<>();
      }
      notifications.add(new Notification(update));
    }
}
