package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;

import commands.jsonReader.ShowAudioCollectionOutput;
import fileio.input.AlbumInput;
import fileio.input.ArtistInput;
import fileio.input.LibraryInput;

import java.util.ArrayList;
import java.util.List;

public final class ShowAlbumsCommand implements Executable {
  /**
   * <p>The method first checks if the user, artist, or host with the given username exists. If not,
   * it returns an error message. If the user is online, it updates the audio trackbar. If the user
   * is offline, it sets the last command timestamp and returns a message that the user is offline.
   * If the user is not an artist, it returns an error message. It then checks if an event with the
   * given name exists for the artist. If not, it returns an error message. If the event exists, it
   * removes the event from the artist's events and returns a success message.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A BaseOutput object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());
    assert artist != null;
    ArrayList<ShowAudioCollectionOutput.ShowAudioCollection> ownedAlbums = new ArrayList<>();

    for (AlbumInput album : LibraryInput.getInstance().getAlbums()) {
      if (album.getOwner().equals(command.getUsername())) {
        List<String> songsNames = album.getSongsNames();
        ShowAudioCollectionOutput.ShowAudioCollection resultAlbum =
            new ShowAudioCollectionOutput.ShowAudioCollection(album.getName(), songsNames);
        ownedAlbums.add(resultAlbum);
      }
    }

    return new ShowAudioCollectionOutput(command, ownedAlbums);
  }
}
