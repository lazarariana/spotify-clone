package fileio.input;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class SongInput {
    private String name;
    private Integer duration;
    private String album;
    private List<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private int likes;

    public SongInput() {
        likes = 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SongInput songInput = (SongInput) o;
        return Objects.equals(name, songInput.name) && Objects.equals(artist, songInput.artist)
                && Objects.equals(album, songInput.album)
                && Objects.equals(genre, songInput.genre)
                && Objects.equals(releaseYear, songInput.releaseYear)
                && Objects.equals(duration, songInput.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration, album, genre, releaseYear, artist);
    }
}
