package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.PageInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import pages.HomePage;
import pages.LikedContentPage;

import java.util.Objects;

import static commands.constants.PageConstants.ACCESS;
import static commands.constants.PageConstants.ARTIST;
import static commands.constants.PageConstants.HOME;
import static commands.constants.PageConstants.HOST;
import static commands.constants.PageConstants.LIKE_CONTENT;
import static commands.constants.PageConstants.NON_EXISTING_PAGE;
import static commands.constants.PageConstants.SUCCESS;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class ChangePageCommand implements Executable {

    /**
     * The method sets the user's page to the new page if the next page is valid, it . If the user
     * is offline, it sets the last command timestamp and returns a message that the user is
     * offline.
     *
     * @param command The command to execute. This should be an instance of Command.
     * @return A BaseOutput object containing the result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());

        if (user == null) {
            return null;
        }

        Player player = user.getPlayer();
        if (user.isOnline()) {
            updateAudioTrackbar(command, player, command.getUsername());
        } else {
            player.setLastCommandTimestamp(command.getTimestamp());
            message = user.getUsername() + IS_OFFLINE.getName();
            return new Output(command, message);
        }

        if (user.isOnline()) {
            if (!command.getNextPage().equals(HOME) && !command.getNextPage().equals(LIKE_CONTENT)
                    && !command.getNextPage().equals(ARTIST)
                    && !command.getNextPage().equals(HOST)) {
                message = user.getUsername() + NON_EXISTING_PAGE;
                return new Output(command, message);
            }

            switch (command.getNextPage()) {
                case HOME:
                    user.setPage(new HomePage(command));
                    break;
                case LIKE_CONTENT:
                    user.setPage(new LikedContentPage(command));
                    break;
                case ARTIST:
                    user.setPage(getArtistPage(command));
                    break;
                case HOST:
                    user.setPage(getHostPage(command));
                    break;
                default:
                    break;
            }

            user.getPageNavigationHistory().add(user.getPage());
            user.setLastPageIndex(user.getPageNavigationHistory().size() - 1);

            message = user.getUsername() + ACCESS + command.getNextPage() + SUCCESS;
            return new Output(command, message);
        }

        message = command.getUsername() + IS_OFFLINE.getName();
        return new Output(command, message);
    }

    /**
     * Retrieves the page of the host associated with the currently loaded podcast.
     * <p>
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * It then retrieves the loaded podcast from the user's player, gets the owner of the podcast,
     * and returns the owner's page.
     *
     * @param command The command containing the username of the user whose player's loaded
     *                podcast's host's page is to be retrieved.
     * @return A PageInput object representing the page of the host associated with the currently
     * loaded podcast.
     */
    public PageInput getHostPage(final Command command) {
        UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
        String podcastOwner = user.getPlayer().getLoadedPodcast().getOwner();
        HostInput host = LibraryInput.getInstance().getHostByName(podcastOwner);

        return Objects.requireNonNull(host).getPage();
    }

    /**
     * Retrieves the page of the artist associated with the currently loaded song or playlist.
     * <p>
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * It then checks if a song is loaded into the user's player. If a song is loaded, it retrieves
     * the artist of the song and returns the artist's page.
     * If no song is loaded, it retrieves the owner of the loaded playlist and returns the owner's
     * page.
     *
     * @param command The command containing the username of the user whose player's loaded song or
     *                playlist's artist's page is to be retrieved.
     * @return A PageInput object representing the page of the artist associated with the currently
     * loaded song or playlist.
     */
    public PageInput getArtistPage(final Command command) {
        UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());
        PlaylistInput playlist = user.getPlayer().getLoadedPlaylist();
        SongInput song = user.getPlayer().getLoadedSong();
        ArtistInput artistSong = LibraryInput.getInstance().getArtistByName(song.getArtist());
        ArtistInput artistPlaylist = null;

        if (playlist != null) {
            artistPlaylist = LibraryInput.getInstance().getArtistByName(playlist.getOwner());
        }

        if (song != null) {
            return Objects.requireNonNull(
                    artistSong.getPage());
        }

        return Objects.requireNonNull(
                artistPlaylist.getPage());
    }
}
