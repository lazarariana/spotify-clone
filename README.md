# Global Waves - Spotify clone 

## Description

This project simulates an audio player with a variety of options. There are
three type of accounts: normal user, artist and host. Each normal user has
an unique search bar and a player, which can play songs, playlists or podcasts
from the library. Additionally, it has two pages, Home and Liked Content Home
is the default pa ge, but users can change pages easily. Both artists and hosts
have pages, displaying their creations. For artist, events, albums owned and merch
are displayed and for host, announcements and podcasts owned. Users can create and
modify their own playlists or follow other public ones, like songs and see different
statistic based on number of likes/follows. The player has multiple functions such as
shuffle songs from a playlist, three repeat modes, skip to next track, rewind to last
track, forward/backward 90 seconds in a podcast episode. Moreover normal users can
switch their connection status. If the user is offline, the player stops, but the
audio file remains loaded until the user is online again and decides if he want to
finish the loaded file or search for a new one. User commands can't be executed when
the user is offline. Normal users can now buy merch of artists and see them. All normal users have
a recommendations system which is displayed on their home page and can be loaded in player.Normal
users are divided now in free and premium based on their subscription. A page navigation system is
available, users having a history of accesed pages in which they can navigate with next and prev
commands, including artist and host pages.

## Implementation

Connection between the users and the app is made through JSON format commands.
Interfaces Executable and BaseOutput are implemented through command classes,
outputs being also of JSON format. I used command pattern to handle the execution
of the commands and managing the output correctly. 
Moreover, the correct functionality of the app is also based on the fact that once a
file is loaded, it cannot be selected anymore. Also once a search is performed,
there will be nothing loaded in the player. I chose a hashmap for filters storage
due to the fact that keys can vary in each search and also multiple values can
be assigned to a key.
As for pages, each one has an printing format and displayed statistics or the activity
of the accounts.

## Inheritance
In order to handle the multiple types of accounts and features, I created AccountInput
class with all properties shared and then extended subclasses UserInput, ArtistInput
and HostInput. It is important that each type of user can create only specific types
of files and also only normal users have a player.

AlbumInput extends PlaylistInput due to their similar design and functionalities. The
main difference is that an album is owned by an artist and a playlist by a nromal user.

Each type of page extends class PageInput due to their specific structure implemented using
Visitor design pattern.

## Use of Singleton Design Pattern

Singleton design pattern is used for LibraryInput class, because it is not possible to create a
library.

## Use of Visitor Design Pattern

The Visitor design pattern is used in the print current command page and the populate
page command. It handles printing different types of pages, each of them having a
different structure and requiring a different way to print its content.
Moreover, it will be much easier to add new pages in the next stage of the project.

I chose to use this design pattern here because I am able to define a new operation
without changing the classes of the elements on which it operates. Here, both
`PrintCurrentPageVisitor` and PopulateCurrentPageVisitor class implement a `visit`
method for each type of page. Each `visit` method knows how to print its
corresponding page, thus encapsulating the printing logic within the visitor, and
keeping the page classes clean and focused on their main responsibilities.
This approach provides flexibility, as we can easily add a new operation on pages
by adding a new visitor, without needing to modify existing page classes.

## Use of Observer Design Pattern

The Observer design pattern is used in handling notifications with notifyObserver function and the
subscribe/unscribe commands. Artist and host classes implement Subject interface while user
implements BalanceObserver.

## Use of Strategy Design Pattern

The Strategy design pattern is used in implementing the monetization of the project for each type
of normal user. FreeMonetizationStrategy class handles the contribution to the monetization of the
artist through ads. PremiumMonetizationStrategy class implements purchasing a credit at a unique
price of 1.000.000. When the subscription is canceled, the credit is divided to artists on platform
based on listens. In the end the history of monetization is cleared for the normal user and saved
for the artist. MonetizationFactory creates the correct type of strategy.

## Structure

__constants__: 
<br></br>
``contains useful constants used for displaying output messages,
comparing with numbers, identifying type of files or commands.``

__executableCommands__:
<br></br>
``contains all types of commands supported by the app
which implement the Executable interface.``

__jsonReader__:
<br></br>
``these classes implement BaseOutput interface and represent all different
types of output written in JSON.``

__player__: 
 ```
  - In order to execute correctly the commands we need to take in consideration
  managing time correctly based on command timestamp, which is done through
  updating the timestamp of the last command executed and the overall active
  time of the player.
  - Besides calculating our current position based on time passed and audio
  files duration, it is important to know the functionalities used at that
  moment of time, which is handled with the status attributes.
  - Player must keep track of all started podcasts, therefore it stores pairs
  of podcasts and the second from where a user should continue listening.
  - A file must be loaded before such commands are applied to it.
  - TimesatmpTrack class is useful in identifying current playing audio file
  and also remaining time or that file.

 ```

__search bar__:
``````
- Users can perform searches based on various filters, all public files that
fit each value of the filters are returned.  
- In order to be able to load an audio file, it must be firstly selected from
the list of results returned by the search.

``````

__pages__:
``````
- Home Page user displays statistics based on the preferrences of
the normal users listed in Library.
- Liked Content Page is used to show the activity of the normal user which
consists of liked/followed audio files.

- Artist Page displayes content created by the artist: albums, events and merch.

- Host Page displayes content created by the host: podcasts and announcements.

 ``````

 __notifications__:
``````
Normal users receive notifications each time another user has followed
one of their playlists or a content creator has added something new on their page if they have
subscribed to them.
``````

 __recommendations__:
 ``````
Recommendations include random song, random playlist
and fans playlist based on elasped time, top genres and top fans.
Additionally the project has a statistics functionality, providing topArtists, topGenres, topSongs,
topAlbums and topEpisodes for normal users, topAlbums, topSongs, topFans and listeners for artists and
topEpisodes and listeners for hosts based on what was played by the user until the timestamp of wrapped
command.

 ``````

 __monetization__:
  ``````
Premium users receive a credit at the subscription which will be divided depending on listens for
each artist by the cancelation of the subscription. As for free users, breaks are
inserted by the end of the current audio file playing if they are not overwritten by a
load command. Each ad has a price, therefore artists are paid based on the number of
listens between breaks and the price of the current ad. Monetization is displayed at the
end of the program, containing: songRevenue - how much money they took off all the lists on
the platform, merchRevenue - how much money they pulled off the merchand sales on the platform,
ranking - artist's ranking on the platform after sales (indexed from 1), mostProfitableSong - a
song that the artist made the most money (based on the amount of returns).

 ``````

I used my implementation of Stage 1 and Stage 2 in order to extend functionalities in Stage 3. 
