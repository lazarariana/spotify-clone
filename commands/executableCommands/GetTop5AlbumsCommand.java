package commands.executableCommands;

import commands.constants.Constants;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.GetTop5Output;
import fileio.input.AlbumInput;
import fileio.input.LibraryInput;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5AlbumsCommand implements Executable {

  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<AlbumInput> top5Albums = new ArrayList<>(LibraryInput.getInstance().getAlbums());
    ArrayList<String> albumNames = new ArrayList<>();

    top5Albums.sort(
        Comparator.comparing(AlbumInput::getTotalLikesAlbum)
            .reversed()
            .thenComparing(AlbumInput::getName));

    for (AlbumInput album : top5Albums) {
      albumNames.add(album.getName());

      if (albumNames.size() == Constants.TOP5) {
        break;
      }
    }

    return new GetTop5Output(command, albumNames);
  }
}
