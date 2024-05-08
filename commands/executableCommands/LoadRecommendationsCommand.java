package commands.executableCommands;

import commands.constants.PlayerEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.PlaylistInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import static commands.constants.Constants.RANDOM_PLAYLIST;
import static commands.constants.Constants.RANDOM_SONG;
import static commands.constants.UserCommandsEnums.LoadRecommendationsMessagesEnum.NOT_AVAILABLE;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.executableCommands.LoadCommand.initLoad;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public class LoadRecommendationsCommand implements Executable {
    /**
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user is online, it updates the audio trackbar and initializes the load.
     * It then checks the user's last recommendation type and loads the last recommended song or
     * playlist into the player.
     * If the user is offline or if there are no recommendations, it returns an appropriate
     * message.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String username = command.getUsername();
        String message = NOT_AVAILABLE.getName();
        int index;
        UserInput user = LibraryInput.getInstance().getUserByName(username);

        if (user != null) {
            Player player = user.getPlayer();
            if (user.isOnline()) {
                updateAudioTrackbar(command, player, command.getUsername());
            } else {
                player.setLastCommandTimestamp(command.getTimestamp());
                message = user.getUsername() + IS_OFFLINE.getName();
                return new Output(command, message);
            }

            initLoad(command, player);

            if (user.getLastRecommendationType() != null) {
                if (user.getLastRecommendationType().equals(RANDOM_SONG)) {
                    index = user.getSongRecommendations().size() - 1;

                    if (index < 0 || user.getSongRecommendations().get(index).isEmpty()) {
                        message = NOT_AVAILABLE.getName();
                        return new Output(command, message);
                    }

                    String songName = user.getSongRecommendations().get(index);
                    SongInput lastRecommendation = LibraryInput.getInstance()
                            .getSongByName(songName);

                    player.setLoadedSong(lastRecommendation);

                    user.getListens().add(lastRecommendation);
                    if (user.isPremium()) {
                        user.getPremiumSongs().add(lastRecommendation);
                    } else {
                        user.getFreeSongs().add(lastRecommendation);
                    }

                    message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();

                    player.setWaitingForAd(false);
                    player.setStartedAd(false);

                    return new Output(command, message);
                } else if (user.getLastRecommendationType().equals(RANDOM_PLAYLIST)) {
                    index = user.getPlaylistRecommendations().size() - 1;

                    if (index < 0 || user.getPlaylistRecommendations().get(index).getSongs()
                            .isEmpty()) {
                        message = NOT_AVAILABLE.getName();
                        return new Output(command, message);
                    }

                    PlaylistInput lastRecommendation = user.getPlaylistRecommendations().get(index);

                    player.setLoadedPlaylist(lastRecommendation);
                    user.getListens().add(lastRecommendation.getSongs().get(0));
                    if (user.isPremium()) {
                        user.getPremiumSongs().add(lastRecommendation.getSongs().get(0));
                    } else {
                        user.getFreeSongs().add(lastRecommendation.getSongs().get(0));
                    }

                    message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();

                    player.setWaitingForAd(false);
                    player.setStartedAd(false);

                    return new Output(command, message);
                } else {
                    index = user.getFansRecommendations().size() - 1;

                    if (index < 0 || user.getFansRecommendations().get(index).getSongs().
                            isEmpty()) {
                        message = NOT_AVAILABLE.getName();
                        return new Output(command, message);
                    }

                    PlaylistInput lastRecommendation = user.getFansRecommendations().get(index);

                    player.setLoadedPlaylist(lastRecommendation);
                    user.getListens().add(lastRecommendation.getSongs().get(0));
                    if (user.isPremium()) {
                        user.getPremiumSongs().add(lastRecommendation.getSongs().get(0));
                    } else {
                        user.getFreeSongs().add(lastRecommendation.getSongs().get(0));
                    }

                    message = PlayerEnums.LoadMessagesEnum.LOAD_MESSAGE.getName();

                    player.setWaitingForAd(false);
                    player.setStartedAd(false);

                    return new Output(command, message);
                }
            }
        }

        return new Output(command, message);
    }
}
