package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.GetTop5Output;
import commands.constants.Constants;
import fileio.input.LibraryInput;
import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5SongsCommand implements Executable {

  /**
   * This method retrieves the top 5 songs from the library. It first gets a list of all songs from
   * the library. It then sorts the songs in descending order by the number of likes and then by the
   * order they were added to the library. It adds the names of the top 5 songs to a list and
   * returns a GetTop5Output object containing the command and the list of song names.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return GetTop5Output object containing the command and the list of top 5 song names.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<SongInput> top5Songs = new ArrayList<>(LibraryInput.getInstance().getSongs());
    ArrayList<String> songNames = new ArrayList<>();

    top5Songs.sort(Comparator.comparing(SongInput::getLikes).reversed());

    for (SongInput song : top5Songs) {
      songNames.add(song.getName());

      if (songNames.size() == Constants.TOP5) {
        break;
      }
    }

    return new GetTop5Output(command, songNames);
  }
}
