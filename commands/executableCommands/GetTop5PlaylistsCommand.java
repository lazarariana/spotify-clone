package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.GetTop5Output;
import commands.constants.Constants;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5PlaylistsCommand implements Executable {

  /**
   * This method retrieves the top 5 playlists from the library. It first gets a list of all
   * playlists from the library. It then sorts the playlists in descending order by the number of
   * followers and then by the date created. It adds the names of the top 5 playlists to a list and
   * returns a GetTop5Output object containing the command and the list of playlist names.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return GetTop5Output object containing the command and the list of top 5 playlist names.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<PlaylistInput> top5Playlists =
        new ArrayList<>(LibraryInput.getInstance().getPlaylists());
    ArrayList<String> playlistNames = new ArrayList<>();

    top5Playlists.sort(
        Comparator.comparing(PlaylistInput::getFollowers)
            .reversed()
            .thenComparing(PlaylistInput::getDateCreated));

    for (PlaylistInput playlist : top5Playlists) {
      playlistNames.add(playlist.getName());

      if (playlistNames.size() == Constants.TOP5) {
        break;
      }
    }

    return new GetTop5Output(command, playlistNames);
  }
}
