package pages;

import commands.jsonReader.Command;
import fileio.input.AccountInput;
import fileio.input.AlbumInput;
import fileio.input.EventInput;
import fileio.input.MerchInput;
import fileio.input.PageInput;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class ArtistPage extends PageInput {
  private List<EventInput> events;
  private List<MerchInput> merch;
  private List<AlbumInput> albums;

  public ArtistPage(final Command command) {
    super(command);
  }

  public ArtistPage(final AccountInput account) {
    super(account);
  }

  public ArtistPage(final String name, final String owner) {
    this.setName(name);
    this.setOwner(owner);
  }

  @Override
  public String accept(final Visitor visitor) {
    return visitor.visit(this);
  }
}
