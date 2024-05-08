package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class PodcastInput {

  private String name;
  private List<EpisodeInput> episodes;

  private String owner;

  public PodcastInput() {

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
  public PodcastInput(final Command command) {
    this.name = command.getName();
    this.episodes = command.getEpisodes();
    this.owner = command.getUsername();
  }

  /**
   * The method iterates through each podcast in the library and checks if its name matches the
   * provided name. If a match is found, the method returns the podcast and stops searching.
   *
   * @param name The name of the podcast to be searched for.
   * @return The PodcastInput object that matches the provided name. If no match is found, the
   *     method returns null.
   */
  public static PodcastInput getPodcastByName(final String name) {
    for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
      if (podcast.getName().equals(name)) {
        return podcast;
      }
    }

    return null;
  }

  /**
   * The method iterates through each episode in the podcast and adds its duration to a running
   * total. The total duration is then returned.
   *
   * @return The total duration of all episodes in the podcast.
   */
  public Integer getSummedDurationsPodcast() {
    Integer durationSum = 0;
    for (EpisodeInput episode : this.getEpisodes()) {
      durationSum += episode.getDuration();
    }

    return durationSum;
  }

  /**
   * The method iterates through each episode in the podcast, adding up their durations. When the
   * cumulative duration equals or exceeds the current timestamp, the method returns the current
   * episode.
   *
   * @param currentTimestamp The current timestamp in the podcast.
   * @return The EpisodeInput object that is currently playing. If no episode is found, the method
   *     returns null.
   */
  public EpisodeInput getCurrentPlayingEpisode(final Integer currentTimestamp) {
    Integer durationSum = 0;
    for (EpisodeInput episode : this.getEpisodes()) {
      durationSum += episode.getDuration();
      if (durationSum >= currentTimestamp) {
        return episode;
      }
    }

    return null;
  }

  /**
   * The method iterates through each episode in the podcast, adding up their durations. When the
   * cumulative duration equals or exceeds the current timestamp, the method returns the total
   * duration of the played episodes.
   *
   * @param currentTimestamp The current timestamp in the podcast.
   * @return The total duration of all played episodes. If no episodes have been played, the method
   *     returns 0.
   */
  public int getPlayedEpisodesTotalDuration(final Integer currentTimestamp) {
    int durationSum = 0;

    for (EpisodeInput episode : this.getEpisodes()) {
      if (durationSum + episode.getDuration() >= currentTimestamp) {
        return durationSum;
      }
      durationSum += episode.getDuration();
    }

    return 0;
  }

  @Override
  public String toString() {
    StringBuilder output = new StringBuilder();
    for (EpisodeInput episode : episodes) {
      output.append(episode.getName()).append(", ");
    }
    return output.toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PodcastInput that = (PodcastInput) o;
    return Objects.equals(name, that.name)
        && Objects.equals(owner, that.owner)
        && Objects.equals(episodes, that.episodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, owner, episodes);
  }

  /**
   * The method iterates over all the episodes in the given podcast and adds their names to a list.
   * Then returns the names of all episodes in a podcast.
   *
   * @param podcast The podcast whose episode names to get. This should be an instance of
   *     PodcastInput.
   * @return An ArrayList of Strings containing the names of all episodes in the podcast.
   */
  public static List<String> getEpisodesNames(final PodcastInput podcast) {
    List<String> episodesNames = new ArrayList<>();

    for (EpisodeInput episode : podcast.getEpisodes()) {
      episodesNames.add(episode.getName());
    }

    return episodesNames;
  }
}
