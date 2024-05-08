package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import commands.executableCommands.AdBreakCommand;
import commands.executableCommands.AddAlbumCommand;
import commands.executableCommands.AddAnnouncementCommand;
import commands.executableCommands.AddEventCommand;
import commands.executableCommands.AddMerchCommand;
import commands.executableCommands.AddPodcastCommand;
import commands.executableCommands.AddRemoveInPlaylistCommand;
import commands.executableCommands.AddUserCommand;
import commands.executableCommands.BackwardCommand;
import commands.executableCommands.BuyMerchCommand;
import commands.executableCommands.BuyPremiumCommand;
import commands.executableCommands.CancelPremiumCommand;
import commands.executableCommands.ChangePageCommand;
import commands.executableCommands.CreatePlaylistCommand;
import commands.executableCommands.DeleteUserCommand;
import commands.executableCommands.EndProgramCommand;
import commands.executableCommands.FollowPlaylistCommand;
import commands.executableCommands.ForwardCommand;
import commands.executableCommands.GetAllUsersCommand;
import commands.executableCommands.GetNotificationsCommand;
import commands.executableCommands.GetOnlineUsersCommand;
import commands.executableCommands.GetTop5AlbumsCommand;
import commands.executableCommands.GetTop5ArtistsCommand;
import commands.executableCommands.GetTop5PlaylistsCommand;
import commands.executableCommands.GetTop5SongsCommand;
import commands.executableCommands.LikeCommand;
import commands.executableCommands.LoadCommand;
import commands.executableCommands.LoadRecommendationsCommand;
import commands.executableCommands.NextCommand;
import commands.executableCommands.NextPageCommand;
import commands.executableCommands.PlayPauseCommand;
import commands.executableCommands.PrevCommand;
import commands.executableCommands.PreviousPageCommand;
import commands.executableCommands.PrintCurrentPageCommand;
import commands.executableCommands.RemoveAlbumCommand;
import commands.executableCommands.RemoveAnnouncementCommand;
import commands.executableCommands.RemoveEventCommand;
import commands.executableCommands.RemovePodcastCommand;
import commands.executableCommands.RepeatCommand;
import commands.executableCommands.SearchCommand;
import commands.executableCommands.SeeMerchCommand;
import commands.executableCommands.SelectCommand;
import commands.executableCommands.ShowAlbumsCommand;
import commands.executableCommands.ShowPlaylistsCommand;
import commands.executableCommands.ShowPodcastsCommand;
import commands.executableCommands.ShowPreferredSongsCommand;
import commands.executableCommands.ShuffleCommand;
import commands.executableCommands.StatusCommand;
import commands.executableCommands.SubscribeCommand;
import commands.executableCommands.SwitchConnectionStatusCommand;
import commands.executableCommands.SwitchVisibilityCommand;
import commands.executableCommands.UpdateRecommendationsCommand;
import commands.executableCommands.WrappedCommand;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.player.Player;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import monetization.MonetizationFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static commands.constants.CommandsConstants.ADD_ALBUM;
import static commands.constants.CommandsConstants.ADD_ANNOUNCEMENT;
import static commands.constants.CommandsConstants.ADD_EVENT;
import static commands.constants.CommandsConstants.ADD_MERCH;
import static commands.constants.CommandsConstants.ADD_PODCAST;
import static commands.constants.CommandsConstants.ADD_REMOVE_IN_PLAYLIST;
import static commands.constants.CommandsConstants.ADD_USER;
import static commands.constants.CommandsConstants.AD_BREAK;
import static commands.constants.CommandsConstants.BACKWARD;
import static commands.constants.CommandsConstants.BUY_MERCH;
import static commands.constants.CommandsConstants.BUY_PREMIUM;
import static commands.constants.CommandsConstants.CANCEL_PREMIUM;
import static commands.constants.CommandsConstants.CHANGE_PAGE;
import static commands.constants.CommandsConstants.CREATE_PLAYLIST;
import static commands.constants.CommandsConstants.DELETE_USER;
import static commands.constants.CommandsConstants.FOLLOW;
import static commands.constants.CommandsConstants.FORWARD;
import static commands.constants.CommandsConstants.GET_ALL_USERS;
import static commands.constants.CommandsConstants.GET_NOTIFICATIONS;
import static commands.constants.CommandsConstants.GET_ONLINE_USERS;
import static commands.constants.CommandsConstants.GET_TOP_5_ALBUMS;
import static commands.constants.CommandsConstants.GET_TOP_5_ARTISTS;
import static commands.constants.CommandsConstants.GET_TOP_5_PLAYLISTS;
import static commands.constants.CommandsConstants.GET_TOP_5_SONGS;
import static commands.constants.CommandsConstants.LIKE;
import static commands.constants.CommandsConstants.LOAD;
import static commands.constants.CommandsConstants.LOAD_RECOMMENDATIONS;
import static commands.constants.CommandsConstants.NEXT;
import static commands.constants.CommandsConstants.NEXT_PAGE;
import static commands.constants.CommandsConstants.PLAY_PAUSE;
import static commands.constants.CommandsConstants.PREV;
import static commands.constants.CommandsConstants.PREVIOUS_PAGE;
import static commands.constants.CommandsConstants.PRINT_CURRENT_PAGE;
import static commands.constants.CommandsConstants.REMOVE_ALBUM;
import static commands.constants.CommandsConstants.REMOVE_ANNOUNCEMENT;
import static commands.constants.CommandsConstants.REMOVE_EVENT;
import static commands.constants.CommandsConstants.REMOVE_PODCAST;
import static commands.constants.CommandsConstants.REPEAT;
import static commands.constants.CommandsConstants.SEARCH;
import static commands.constants.CommandsConstants.SEE_MERCH;
import static commands.constants.CommandsConstants.SELECT;
import static commands.constants.CommandsConstants.SHOW_ALBUMS;
import static commands.constants.CommandsConstants.SHOW_PLAYLISTS;
import static commands.constants.CommandsConstants.SHOW_PODCASTS;
import static commands.constants.CommandsConstants.SHOW_PREFERRED_SONGS;
import static commands.constants.CommandsConstants.SHUFFLE;
import static commands.constants.CommandsConstants.STATUS;
import static commands.constants.CommandsConstants.SUBSCRIBE;
import static commands.constants.CommandsConstants.SWITCH_CONNECTION_STATUS;
import static commands.constants.CommandsConstants.SWITCH_VISIBILITY;
import static commands.constants.CommandsConstants.UPDATE_RECOMMENDATIONS;
import static commands.constants.CommandsConstants.WRAPPED;
import static commands.player.TimestampTrack.updateAudioTrackbar;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {

    }

    /**
     * DO NOT MODIFY MAIN METHOD Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput, final String filePathOutput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);
        LibraryInput.setInstance(library);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ArrayNode outputs = objectMapper.createArrayNode();

        List<Command> commands =
                objectMapper.readValue(
                        new File(CheckerConstants.TESTS_PATH + filePathInput),
                        new TypeReference<List<Command>>() {

                        });

        for (Command command : commands) {
            BaseOutput output = null;
            JsonNode outputJson = null;
            UserInput user = LibraryInput.getInstance().getUserByName(command.getUsername());

            assert user != null;

            for (UserInput userLibrary : LibraryInput.getInstance().getUsers()) {
                Player player = userLibrary.getPlayer();
                if (userLibrary.isOnline()) {
                    updateAudioTrackbar(command, player, userLibrary.getUsername());
                } else {
                    player.setLastCommandTimestamp(command.getTimestamp());
                }
            }
            output =
                    switch (command.getCommand()) {
                        case SELECT -> (new SelectCommand()).executeCommand(command);
                        case SEARCH -> (new SearchCommand()).executeCommand(command);
                        case LOAD -> (new LoadCommand()).executeCommand(command);
                        case PLAY_PAUSE -> (new PlayPauseCommand()).executeCommand(command);
                        case REPEAT -> (new RepeatCommand()).executeCommand(command);
                        case SHUFFLE -> (new ShuffleCommand()).executeCommand(command);
                        case FORWARD -> (new ForwardCommand()).executeCommand(command);
                        case BACKWARD -> (new BackwardCommand()).executeCommand(command);
                        case LIKE -> (new LikeCommand()).executeCommand(command);
                        case NEXT -> (new NextCommand()).executeCommand(command);
                        case PREV -> (new PrevCommand()).executeCommand(command);
                        case ADD_REMOVE_IN_PLAYLIST -> (new AddRemoveInPlaylistCommand())
                                .executeCommand(command);
                        case STATUS -> (new StatusCommand()).executeCommand(command);
                        case CREATE_PLAYLIST -> (new CreatePlaylistCommand())
                                .executeCommand(command);
                        case SWITCH_VISIBILITY -> (new SwitchVisibilityCommand())
                                .executeCommand(command);
                        case FOLLOW -> (new FollowPlaylistCommand()).executeCommand(command);
                        case SHOW_PLAYLISTS -> (new ShowPlaylistsCommand())
                                .executeCommand(command);
                        case SHOW_PREFERRED_SONGS -> (new ShowPreferredSongsCommand())
                                .executeCommand(command);
                        case GET_TOP_5_SONGS -> (new GetTop5SongsCommand()).executeCommand(command);
                        case GET_TOP_5_PLAYLISTS -> (new GetTop5PlaylistsCommand())
                                .executeCommand(command);
                        case ADD_USER -> (new AddUserCommand()).executeCommand((command));
                        case DELETE_USER -> (new DeleteUserCommand()).executeCommand((command));
                        case SWITCH_CONNECTION_STATUS -> (new SwitchConnectionStatusCommand())
                                .executeCommand(command);
                        case GET_ONLINE_USERS -> (new GetOnlineUsersCommand())
                                .executeCommand(command);
                        case ADD_ALBUM -> (new AddAlbumCommand()).executeCommand(command);
                        case ADD_MERCH -> (new AddMerchCommand()).executeCommand(command);
                        case ADD_EVENT -> (new AddEventCommand()).executeCommand(command);
                        case ADD_PODCAST -> (new AddPodcastCommand()).executeCommand(command);
                        case ADD_ANNOUNCEMENT -> (new AddAnnouncementCommand())
                                .executeCommand(command);
                        case REMOVE_ALBUM -> (new RemoveAlbumCommand()).executeCommand(command);
                        case REMOVE_EVENT -> (new RemoveEventCommand()).executeCommand(command);
                        case REMOVE_ANNOUNCEMENT -> (new RemoveAnnouncementCommand())
                                .executeCommand(command);
                        case REMOVE_PODCAST -> (new RemovePodcastCommand())
                                .executeCommand(command);
                        case SHOW_ALBUMS -> (new ShowAlbumsCommand()).executeCommand(command);
                        case PRINT_CURRENT_PAGE -> (new PrintCurrentPageCommand())
                                .executeCommand(command);
                        case SHOW_PODCASTS -> (new ShowPodcastsCommand()).executeCommand(command);
                        case GET_ALL_USERS -> (new GetAllUsersCommand()).executeCommand(command);
                        case CHANGE_PAGE -> (new ChangePageCommand()).executeCommand(command);
                        case GET_TOP_5_ALBUMS -> (new GetTop5AlbumsCommand())
                                .executeCommand(command);
                        case GET_TOP_5_ARTISTS -> (new GetTop5ArtistsCommand())
                                .executeCommand(command);


                        case WRAPPED -> (new WrappedCommand()).executeCommand(command);
                        case BUY_MERCH -> (new BuyMerchCommand()).executeCommand(command);
                        case SEE_MERCH -> (new SeeMerchCommand()).executeCommand(command);
                        case PREVIOUS_PAGE -> (new PreviousPageCommand()).executeCommand(command);
                        case NEXT_PAGE -> (new NextPageCommand()).executeCommand(command);
                        case UPDATE_RECOMMENDATIONS -> (new UpdateRecommendationsCommand())
                                .executeCommand(command);
                        case LOAD_RECOMMENDATIONS -> (new LoadRecommendationsCommand())
                                .executeCommand(command);
                        case SUBSCRIBE -> (new SubscribeCommand()).executeCommand(command);
                        case GET_NOTIFICATIONS -> (new GetNotificationsCommand())
                                .executeCommand(command);
                        case BUY_PREMIUM -> (new BuyPremiumCommand()).executeCommand(command);
                        case CANCEL_PREMIUM -> (new CancelPremiumCommand())
                                .executeCommand(command);
                        case AD_BREAK -> (new AdBreakCommand()).executeCommand(command);
                        default -> null;
                    };

            if (output != null) {
                outputJson = objectMapper.valueToTree(output);
            }

            outputs.add(outputJson);
        }

        for (UserInput user : LibraryInput.getInstance().getUsers().stream()
                                                        .filter(UserInput::isPremium).toList()) {
            MonetizationFactory.createMonetizationStrategy(user.isPremium()).monetize(user);
        }

        BaseOutput endProgramOutput = (new EndProgramCommand()).executeCommand(null);
        JsonNode outputJson = null;

        if (endProgramOutput != null) {
            outputJson = objectMapper.valueToTree(endProgramOutput);
        }

        outputs.add(outputJson);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
