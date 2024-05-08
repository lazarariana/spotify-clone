package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public final class AnnouncementInput {
  private String name;
  private String description;

  public AnnouncementInput(final Command command) {
    this.name = command.getName();
    this.description = command.getDescription();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AnnouncementInput that = (AnnouncementInput) o;
    return Objects.equals(name, that.name);
  }

  /**
   * Retrieves an announcement by its name from a host's list of announcements.
   *
   * <p>The method iterates over all announcements of the host and checks if the name of the
   * announcement matches the given name. If a match is found, it returns the announcement. If no
   * match is found, it returns null.
   *
   * @param host The host whose list of announcements to search. This should be an instance of
   *     HostInput.
   * @param name The name of the announcement to search for.
   * @return An AnnouncementInput object if an announcement with the given name is found, null
   *     otherwise.
   */
  public static AnnouncementInput getAnnouncementByName(final HostInput host, final String name) {
    for (AnnouncementInput announcement : host.getAnnouncements()) {
      if (announcement.getName().equals(name)) {
        return announcement;
      }
    }

    return null;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }
}
