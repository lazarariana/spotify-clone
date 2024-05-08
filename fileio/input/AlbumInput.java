package fileio.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class AlbumInput extends PlaylistInput {
  private String description;
  private int releaseYear;

  public AlbumInput(final Command command) {
    super(command);
    getSongs().addAll(command.getSongs());
    setName(command.getName());
  }

  @Override
  @JsonIgnore
  public String getVisibility() {
    return super.getVisibility();
  }

  @Override
  @JsonIgnore
  public Integer getFollowers() {
    return super.getFollowers();
  }


  /**
   * The method iterates over all the songs on the album and adds their names to a list. It returns
   * the names of all songs on this album.
   *
   * @return An ArrayList of Strings containing the names of all songs on this album.
   */
  public List<String> getSongsNames() {
    ArrayList<String> songsNames = new ArrayList<>();

    for (SongInput song : this.getSongs()) {
      songsNames.add(song.getName());
    }

    return songsNames;
  }

  /**
   * The method iterates over all the albums in the library and returns the one with the given name.
   * If no album with the given name is found, it returns null. It returns the AlbumInput object
   * with the given name.
   *
   * @param name The name of the album to get. This should be a String.
   * @return The AlbumInput object with the given name, or null if no such album is found.
   */
  public static AlbumInput getAlbumByName(final String name) {
    for (AlbumInput album : LibraryInput.getInstance().getAlbums()) {
      if (album.getName().equals(name)) {
        return album;
      }
    }

    return null;
  }

  /**
   * The method iterates over all the songs on the album and adds up their likes. It returns the
   * total number of likes for all songs on this album.
   *
   * @return An Integer representing the total number of likes for all songs on this album.
   */
  public Integer getTotalLikesAlbum() {
    int totalLikes = 0;

    for (SongInput song : this.getSongs()) {
      totalLikes += song.getLikes();
    }

    return totalLikes;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return super.equals(o);
  }

  /**
   * Returns a hash code value for the object.
   *
   * <p>This method is supported for the benefit of hash tables such as those provided by HashMap.
   * The hash code is calculated based on the hash code of the superclass and the description and
   * releaseYear fields.
   *
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), description, releaseYear);
  }
}
