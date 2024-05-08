package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import static commands.constants.UserCommandsEnums.AdBreakMessagesEnum.NOT_MUSIC;
import static commands.constants.UserCommandsEnums.AdBreakMessagesEnum.SUCCESS_AD;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SwitchConnectionStatusMessagesEnum.IS_OFFLINE;
import static commands.constants.UserCommandsEnums.USERNAME;
import static commands.player.TimestampTrack.getShufflePlaylistRemainingTime;
import static commands.player.TimestampTrack.getSongRemainingTime;
import static commands.player.TimestampTrack.updateAudioTrackbar;

public final class AdBreakCommand implements Executable {
    /**
     * The method adds in queue an ad if the user is playing any music.
     *
     * @param command The command to be executed.
     * @return The output of the command execution. If the user does not exist, an error message is
     * returned. If the user is not a premium user and there is no loaded song or playlist with
     * remaining time, an error message is returned. If there is a loaded song or playlist with
     * remaining time, the method sets the ad in queue timestamp to zero, adds the price of the
     * command to the list of ads played prices, sets shouldPlayAd to true, and playingAd to false,
     * and then returns a success message. If the user is a premium user, the method returns null.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);
        assert user != null;

        Player player = user.getPlayer();
        if (user.isOnline()) {
            updateAudioTrackbar(command, player, command.getUsername());
        } else {
            player.setLastCommandTimestamp(command.getTimestamp());
            message = user.getUsername() + IS_OFFLINE.getName();
            return new Output(command, message);
        }

        if (user == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        if (!user.isPremium()) {
            if (user.getPlayer().getLoadedSong() != null
                    && getSongRemainingTime(user.getPlayer()) == 0) {
                message = username + NOT_MUSIC.getName();
                return new Output(command, message);
            }

            if (user.getPlayer().getLoadedPlaylist() != null
                    && getShufflePlaylistRemainingTime(user.getPlayer()) == 0) {
                message = username + NOT_MUSIC.getName();
                return new Output(command, message);
            }

            if (user.getPlayer().getLoadedPlaylist() == null
                    && user.getPlayer().getLoadedSong() == null) {
                message = username + NOT_MUSIC.getName();
                return new Output(command, message);
            }

            player.setAdInQueueTimestamp(0);
            user.getPlayer().getAdsPlayedPrices().add(command.getPrice());
            player.setWaitingForAd(true);
            player.setStartedAd(false);

            message = SUCCESS_AD.getName();
            return new Output(command, message);
        }
        return null;
    }
}
