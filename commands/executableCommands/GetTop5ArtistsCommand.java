package commands.executableCommands;

import commands.constants.Constants;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.GetTop5Output;
import fileio.input.ArtistInput;
import fileio.input.LibraryInput;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5ArtistsCommand implements Executable {

  /**
   * The method gets the top 5 artists from the library based on the total likes. It sorts the
   * artists in descending order of total likes and returns the usernames of the top 5 artists.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A GetTop5Output object containing the usernames of the top 5 artists.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {

    ArrayList<ArtistInput> top5Artists = new ArrayList<>(LibraryInput.getInstance().getArtists());
    ArrayList<String> artistNames = new ArrayList<>();

    top5Artists.sort(Comparator.comparingInt(ArtistInput::getTotalLikes).reversed());

    for (ArtistInput artist : top5Artists) {
      artistNames.add(artist.getUsername());

      if (artistNames.size() == Constants.TOP5) {
        break;
      }
    }

    return new GetTop5Output(command, artistNames);
  }
}
