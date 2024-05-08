package commands.constants;

import lombok.Getter;

public final class ArtistCommandsEnums {

  public static final Integer MIN_YEAR = 1900;
  public static final Integer MAX_YEAR = 2300;

  public static final Integer FEBRUARY = 2;
  public static final Integer FEBRUARY_DAYS = 28;
  public static final Integer MAX_DAYS_MONTH = 31;
  public static final Integer MAX_MONTHS_YEAR = 12;
  public static final String DATE_FORMAT = "dd-MM-yyyy";

  private ArtistCommandsEnums() {
  }

  @Getter
  public enum AddAlbumMessagesEnum {
    NOT_ARTIST(" is not an artist."),
    ALREADY_EXISTS_ALBUM(" has another album with the same name."),
    TWICE_SONG(" has the same song at least twice in this album."),
    SUCCESS_ADD_ALBUM(" has added new album successfully.");

    private final String name;

    AddAlbumMessagesEnum(final String name) {
      this.name = name;
    }
  }

  @Getter
  public enum DeleteAlbumMessagesEnum {
    NO_ALBUM(" doesn't have an album with the given name."),
    NO_DELETE(" can't delete this album."),
    SUCCESS_DELETE_ALBUM(" deleted the album successfully.");

    private final String name;

    DeleteAlbumMessagesEnum(final String name) {
      this.name = name;
    }
  }

  @Getter
  public enum AddEventMessagesEnum {
    ALREADY_EXISTS_EVENT(" has another event with the same name."),
    EVENT_FOR("Event for "),
    INVALID_DATE(" does not have a valid date."),
    SUCCESS_ADD_EVENT(" has added new event successfully.");

    private final String name;

    AddEventMessagesEnum(final String name) {
      this.name = name;
    }
  }

  @Getter
  public enum RemoveEventMessagesEnum {
    NOT_EXISTS_EVENT(" doesn't have an event with the given name."),
    SUCCESS_REMOVE_EVENT(" deleted the event successfully.");

    private final String name;

    RemoveEventMessagesEnum(final String name) {
      this.name = name;
    }
  }

  @Getter
  public enum AddMerchMessagesEnum {
    ALREADY_EXISTS_MERCH(" has merchandise with the same name."),
    INVALID_PRICE("Price for merchandise can not be negative."),
    SUCCESS_ADD_MERCH(" has added new merchandise successfully.");

    private final String name;

    AddMerchMessagesEnum(final String name) {
      this.name = name;
    }
  }

  @Getter
  public enum BuyMerchMessagesEnum {
    NOT_THIS_PAGE("Cannot buy merch from this page."),
    MERCH("The merch "),
    SUCCESS_BUY_MERCH(" has added new merch successfully.");

    private final String name;

    BuyMerchMessagesEnum(final String name) {
      this.name = name;
    }
  }

  }
