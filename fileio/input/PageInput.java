package fileio.input;

import commands.jsonReader.Command;
import lombok.Getter;
import lombok.Setter;
import pages.Visitable;
import pages.Visitor;

@Getter
@Setter
public class PageInput implements Visitable {
  private String name;
  private String owner;

  public PageInput(final Command command) {
    this.name = command.getNextPage();
    this.owner = command.getUsername();
  }

  public PageInput() {

  }

  public PageInput(final AccountInput account) {
    this.owner = account.getUsername();
  }

  /**
   * Accepts a visitor and allows it to perform an operation on this instance.
   *
   * <p>This method is part of the implementation of the Visitor design pattern. It allows a Visitor
   * object to visit the instance of the class and perform some operation on it.
   *
   * @param visitor The visitor to accept. This should be an instance of a class that implements the
   *     Visitor interface.
   * @return The result of the operation performed by the visitor on this instance.
   */
  @Override
  public String accept(final Visitor visitor) {
    return null;
  }

  /**
   * Populates the arrays of top 5 liked songs and followed playlists for a user.
   *
   * <p>The method first casts the given AccountInput to a UserInput. It then calls the
   * getTop5LikedSongs and getTop5LikedPlaylists methods to get the top 5 liked songs and followed
   * playlists for the user, respectively.
   *
   * @param account The account whose top 5 liked songs and followed playlists to get. This should
   *     be an instance of AccountInput, which will be cast to UserInput.
   */
  public void populatePageArrays(final AccountInput account) {

  }
}
