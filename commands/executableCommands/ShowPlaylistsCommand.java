package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.jsonReader.Result;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public final class ShowPlaylistsCommand implements Executable {

  /**
   * The method first retrieves the user associated with the command. It then iterates over the
   * user's created playlists and for each playlist, it creates a Result object containing the
   * playlist's name, visibility, followers, and songs. It returns an Output object
   * containing the command and the list of Result objects representing the user's owned playlists.
   *
   * @param command The command object which specifies the user and current timestamp in order to
   *     identify the current audio file playing.
   * @return An Output object containing the result of the command execution and the user's owned
   *     playlists.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
    assert user != null;
    ArrayList<Result> ownedPlaylists = new ArrayList<>();

    for (int i = 0; i < user.getCreatedPlaylists().size(); i++) {
      PlaylistInput playlist = user.getCreatedPlaylists().get(i);
      String name = playlist.getName();
      String visibility = playlist.getVisibility();
      Integer followers = playlist.getFollowers();

      Result result = new Result();
      result.setName(name);
      result.setVisibility(visibility);
      result.setFollowers(followers);

      for (SongInput song : playlist.getSongs()) {
        result.getSongs().add(song.getName());
      }

      ownedPlaylists.add(result);
    }

    return new Output(command, ownedPlaylists, null);
  }
}
