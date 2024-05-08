package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.AlbumInput;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.List;

import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.ALREADY_EXISTS_ALBUM;
import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.NOT_ARTIST;
import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.SUCCESS_ADD_ALBUM;
import static commands.constants.ArtistCommandsEnums.AddAlbumMessagesEnum.TWICE_SONG;
import static commands.constants.Constants.DOT;
import static commands.constants.NotificationsConstants.NOTIFICATION_ALBUM;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class AddAlbumCommand implements Executable {

    /**
     * The method adds an album to the library owned by the specified artist if the artist exists
     * and the album doesn't already exist. Then it checks if the album has any duplicate songs.
     * If so, it returns an error message.
     * If all checks pass, it adds the album to the library and returns a
     * success message.
     *
     * @param command The command to execute. This should be an instance of Command with the
     * username of the artist and the name of the album.
     * @return A BaseOutput object containing the result of the command with a relevant message.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);
        ArtistInput artist = LibraryInput.getInstance().getArtistByName(command.getUsername());
        HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());

        if (user == null && artist == null && host == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        if (artist == null) {
            message = username + NOT_ARTIST.getName();
            return new Output(command, message);
        }

        List<AlbumInput> albums = artist.getArtistAlbums();
        for (AlbumInput album : albums) {
            if (album.getName().equals(command.getName())) {
                message = command.getUsername() + ALREADY_EXISTS_ALBUM.getName();
                return new Output(command, message);
            }
        }

        AlbumInput album = new AlbumInput(command);
        List<String> songs = album.getSongsNames();

        if (!LibraryInput.hasUniqueElements(songs)) {
            message = command.getUsername() + TWICE_SONG.getName();
            return new Output(command, message);
        }

        album.setDateCreated(command.getTimestamp());

        LibraryInput.getInstance().getAlbums().add(album);
        for (SongInput song : album.getSongs()) {
            LibraryInput.getInstance().getSongs().add(song);
        }

        artist.notifyObservers(NOTIFICATION_ALBUM + artist.getUsername() + DOT);

        message = command.getUsername() + SUCCESS_ADD_ALBUM.getName();
        return new Output(command, message);
    }
}
