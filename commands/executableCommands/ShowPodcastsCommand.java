package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.ShowPodcastOutput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;

import java.util.ArrayList;
import java.util.List;

import static fileio.input.PodcastInput.getEpisodesNames;

public final class ShowPodcastsCommand implements Executable {

  /**
   * <p>The method first gets the host with the given username. It then iterates over all podcasts
   * in the library and checks if the host owns the podcast. If the host owns the podcast, it adds
   * the podcast to the list of owned podcasts. Finally, it returns a ShowPodcastOutput object
   * containing the list of owned podcasts.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A ShowPodcastOutput object containing the list of podcasts owned by the host.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());
    assert host != null;
    ArrayList<ShowPodcastOutput.ShowAudioCollection> ownedPodcasts = new ArrayList<>();

    for (PodcastInput podcast : LibraryInput.getInstance().getPodcasts()) {
      if (podcast.getOwner().equals(command.getUsername())) {
        List<String> episodesNames = getEpisodesNames(podcast);
        ShowPodcastOutput.ShowAudioCollection resultPodcast =
            new ShowPodcastOutput.ShowAudioCollection(podcast.getName(), episodesNames);
        ownedPodcasts.add(resultPodcast);
      }
    }

    return new ShowPodcastOutput(command, ownedPodcasts);
  }
}
