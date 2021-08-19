<p align="center">
  <h1 align="center">Minesweeper</h1>
  <p align="center">
    A logic puzzle game where limited information about the location of "mines" in a grid is presented and the goal is to discover all of the mines withough accidentally clicking on one.
    <a href="https://en.wikipedia.org/wiki/Minesweeper_(video_game)">Read more here</a>
    <br />
  </p>
</p>

![Preview](/assets/img.png)

## Basic Features

1. An N by M grid containing K mines randomly placed.
2. Each click either reveals a mine (ending the game) or an integer that indicates the number of mines in the surrounding 8 squares.
3. Automatically reveals contiguous regions of “zero” squares.
4. A timer that indicates the elapsed time.
5. A high score board that is updated with the fastest times for standard grid sizes.
6. The first square clicked is never a mine.
7. Marking of suspected mine squares prevents accidentally clicking on them.
8. Game is won when the mined squares are the only ones left unrevealed.
9. Save/resume of in-progress games.

## Setup / Dependencies

* Junit 4.12+ (For testing library)

* javafx sdk 15.0.1+ (For GUI)

## Contributing

* Caroline Hyland
* James O'Connell
* Nicholas Leluan
* Christian Trejo
