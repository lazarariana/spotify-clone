package fileio.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class EpisodeInput {
  private String name;
  private Integer duration;
  private String description;

  public EpisodeInput() {

  }

  /**
   * Retrieves the owner of the podcast that contains the current episode.
   *
   * This method iterates over all podcasts in the library. For each podcast, it checks if the
   * podcast's episodes contain the current episode. If it does, it returns the owner of the
   * podcast.
   *
   * @return The owner of the podcast that contains the current episode. If no such podcast is
   * found, it returns null.
   */
  public String getPodcastOwner() {
    for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
      if (podcast.getEpisodes().contains(this)) {
        return podcast.getOwner();
      }
    }
    return null;
  }
}
