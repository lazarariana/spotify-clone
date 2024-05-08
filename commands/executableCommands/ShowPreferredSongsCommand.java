package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.PreferedSongsOutput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public final class ShowPreferredSongsCommand implements Executable {

  /**
   * The method first retrieves the user associated with the command. It then iterates over the
   * user's liked songs and adds each song name to a list. Finally, it returns a
   * PreferredSongsOutput object containing the command and the list of liked songs.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return A PrefferedSongsOutput object containing the command and the list of liked songs.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<String> likedSongs = new ArrayList<>();
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    assert user != null;

    for (SongInput song : user.getLikedSongs()) {
      likedSongs.add(song.getName());
    }

    return new PreferedSongsOutput(command, likedSongs);
  }
}
