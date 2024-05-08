package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.MerchInput;
import fileio.input.UserInput;

import static commands.constants.ArtistCommandsEnums.BuyMerchMessagesEnum.MERCH;
import static commands.constants.ArtistCommandsEnums.BuyMerchMessagesEnum.NOT_THIS_PAGE;
import static commands.constants.ArtistCommandsEnums.BuyMerchMessagesEnum.SUCCESS_BUY_MERCH;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public class BuyMerchCommand implements Executable {
    /**
     * Executes a command to buy a merchandise for a user and returns the result.
     * <p>
     * This method takes a Command object as input and retrieves the user, artist, and host
     * associated with the command from the library.
     * If none of them exist, it returns an appropriate message.
     * It then searches for the merchandise in the library. If the merchandise does not exist,
     * it returns an appropriate message.
     * If the user exists and the owner of the user's page is the owner of the merchandise, it
     * updates the merchandise owner's revenue, adds the merchandise to the user's bought
     * merchandise, and returns a success message.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution.
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

        MerchInput merch = null;
        ArtistInput merchOwner = null;

        for (ArtistInput libraryArtist : LibraryInput.getInstance().getArtists()) {
            for (MerchInput artistMerch : libraryArtist.getMerch()) {
                if (artistMerch.getName().equals(command.getName())) {
                    merch = artistMerch;
                    merchOwner = libraryArtist;
                }
            }
        }

        if (merch == null) {
            message = MERCH.getName() + command.getName() + NO_EXIST.getName();
            return new Output(command, message);
        }

        if (user != null) {
            if (!user.getPage().getOwner().equals(merchOwner.getUsername())) {
                message = user.getUsername() + NOT_THIS_PAGE.getName();
                return new Output(command, message);
            }

            merchOwner.setMerchRevenue(merchOwner.getMerchRevenue() + merch.getPrice());
            user.getBoughtMerch().add(merch);
            message = username + SUCCESS_BUY_MERCH.getName();

            return new Output(command, message);
        }

        return null;
    }
}
