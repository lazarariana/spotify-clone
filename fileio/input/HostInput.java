package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;
import notifications.BalanceObserver;
import notifications.Subject;
import pages.HostPage;

import java.util.ArrayList;
import java.util.List;

import static commands.constants.PageConstants.HOST_PAGE;

@Getter
@Setter
public final class HostInput extends AccountInput implements Subject {
  private List<AnnouncementInput> announcements = new ArrayList<>();
  private List<PodcastInput> hostPodcasts = new ArrayList<>();
  private List<BalanceObserver> subscribers = new ArrayList<>();

  private PageInput page;

  public HostInput(final Command command) {
    super(command);
    setAnnouncements(new ArrayList<>());
    setHostPodcasts(new ArrayList<>());
    setPage(new HostPage(HOST_PAGE, getUsername()));
  }

  /**
   * The method iterates over all the podcasts in the library and adds to a list those that are
   * owned by this host. It returns the podcasts owned by this host.
   *
   * @return A List of PodcastInput objects that are owned by this host.
   */
  public List<PodcastInput> getHostPodcasts() {
    ArrayList<PodcastInput> podcasts = new ArrayList<>();
    for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
      if (podcast.getOwner().equals(this.getUsername())) {
        podcasts.add(podcast);
      }
    }
    return podcasts;
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
