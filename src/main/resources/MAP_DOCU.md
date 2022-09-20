# List of Maps and Their Characteristics

This document summarizes the characteristics of different game maps listed in the `resources/` directory.

## Cases required to be handled

### Initial map

- Undo limit >= -1
- At least one player
- Each player must have at least 1 box
- Number of boxes == number of destinations
- Unique upper-case letter for each player
- Each box must be matched to a player

### Main runtime cases

- ActionResult.Success
  - Player move to Empty
  - Player pushes one own Box to Empty
- ActionResult.Failed
  - null (possibly out of scope)
  - Box
    - Player pushes more than one own Box
    - Player pushes one own Box into another non-Empty Entity
    - Player pushes other players' Box
  - Player
    - Player pushes another Player
  - Wall
    - Player runs into Wall

### Possible edge cases

- Array index out of bound (to be investigated)

### Out of scope

There are several cases where the TAs explicitly determined to be out of scope of this PA:

- Map file structure
- Open wall boundary
- Whitespace within wall boundaries
- Deadlock (none of the boxes can be moved)

## Given Maps

2 sample maps were provided by TAs during assignment preparation:

- `map00.map`
  - 5 undo limits
  - Square map
    - Width: 6
    - Height: 6
  - 1 Player (A)
  - 1 Type of Box (a)
  - 2 Boxes (2a)
  - 2 Destinations
  - No deadlock
- `map01.map`
  - 5 undo limits
  - Non-square map
    - Width: 7
    - Height: 6
  - 1 Player (A)
  - 1 Type of Box (a)
  - 4 Boxes (4a)
  - 4 Destinations
  - No deadlock

## Self-created Maps

Additional maps were created by me to test for different cases and edge cases:

### Successful Maps

- `map02.map`
  - Unlimited undo (-1)
  - Square map
    - Width: 12
    - Height: 7
  - 2 Players (A, B)
  - 2 Types of Box (a, b)
  - 6 Boxes (4a, 2b)
  - 6 Destinations
  - No deadlock
- `map03.map`
  - 0 undo limit
  - Non-square map
    - Width: 14
    - Height: 7
  - 1 Player (A)
  - 1 Type of Box (a)
  - 4 Boxes (4a)
  - 4 Destinations
  - No deadlock

### Failed Maps

- `map04.map`
  - <span style="color: red">-3 undo limits</span>
  - Square map
    - Width: 6
    - Height: 6
  - 1 Player (A)
  - 1 Type of Box (a)
  - 2 Boxes (2a)
  - 2 Destinations
  - No deadlock
- `map05.map`
  - 3 undo limits
  - Square map
    - Width: 6
    - Height: 6
  - <span style="color: red">0 Players</span>
  - 0 Types of Box ()
  - 0 Boxes ()
  - 0 Destinations
  - No deadlock
- `map06.map`
  - -1 undo limits
  - Square map
    - Width: 15
    - Height: 10
  - <span style="color: red">4 Players (A, B, C, D)</span>
  - 4 Types of Box (a, b, c, d)
  - 8 Boxes (4a, 2b, 1c, 1d)
  - 8 Destinations
  - No deadlock
- `map07.map`
  - 5 undo limits
  - Square map
    - Width: 6
    - Height: 6
  - <span style="color: red">2 Players (A, A) (Duplicate uppercase letter)</span>
  - 1 Type of Box (a)
  - 2 Boxes (2a)
  - 2 Destinations
  - No deadlock
- `map08.map` (TODO)
  - 5 undo limits
  - Square map
    - Width: 6
    - Height: 6
  - 1 Player (A)
  - <span style="color: red">0 Type of Box ()</span>
  - 0 Boxes ()
  - 0 Destinations
  - No deadlock
- `map09.map` (TODO)
  - 5 undo limits
  - Non-square map
    - Width: 7
    - Height: 6
  - 2 Players (A, B)
  - <span style="color: red">1 Type of Box (a)</span>
  - 4 Boxes (4a)
  - 4 Destinations
  - No deadlock
- `map10.map` (TODO)
  - 5 undo limits
  - Non-square map
    - Width: 7
    - Height: 6
  - 1 Player (A)
  - <span style="color: red">2 Types of Box (a, b)</span>
  - 5 Boxes (4a, 1b)
  - 5 Destinations
  - No deadlock
- `map11.map` (TODO)
  - 5 undo limits
  - Non-square map
    - Width: 7
    - Height: 6
  - 1 Player (A)
  - 1 Type of Box (a)
  - 4 Boxes (4a)
  - <span style="color: red">5 Destinations</span>
  - No deadlock
- `map12.map` (TODO)
  - 5 undo limits
  - Non-square map
    - Width: 7
    - Height: 6
  - 1 Player (A)
  - 1 Type of Box (a)
  - 4 Boxes (4a)
  - <span style="color: red">0 Destinations</span>
  - No deadlock


## This is the end of this documentation.