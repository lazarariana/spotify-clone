package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class EventInput {
  private String name;
  private String date;
  private String description;

  public EventInput(final Command command) {
    this.name = command.getName();
    this.date = command.getDate();
    this.description = command.getDescription();
  }

  /**
   * The method iterates over all the events of the given artist and returns the one with the given
   * name. It returns the EventInput object with the given name from the events of a specific artist
   * or null if no event with the given name is found, it returns null.
   *
   * @param artist The artist whose event to get. This should be an instance of ArtistInput.
   * @param name The name of the event to get. This should be a String.
   * @return The EventInput object with the given name from the artist's events, or null if no such
   *     event is found.
   */
  public static EventInput getEventByName(final ArtistInput artist, final String name) {
    for (EventInput event : artist.getEvents()) {
      if (event.getName().equals(name)) {
        return event;
      }
    }

    return null;
  }
}
