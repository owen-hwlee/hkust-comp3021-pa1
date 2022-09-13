package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.utils.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameBoard is capable to create many GameState instances, each representing an ongoing game.
 */
public class GameMap {

    private int maxWidth;
    private int maxHeight;
    private Set<Position> destinations;
    private int undoLimit;
    private Entity[][] entities;

    /**
     * Create a new GameMap with width, height, set of box destinations and undo limit.
     *
     * @param maxWidth     Width of the game map.
     * @param maxHeight    Height of the game map.
     * @param destinations Set of box destination positions.
     * @param undoLimit    Undo limit.
     *                     Positive numbers specify the maximum number of undo actions.
     *                     0 means undo is not allowed.
     *                     -1 means unlimited. Other negative numbers are not allowed.
     */
    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        // DONE
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = destinations;
        this.undoLimit = undoLimit;
        this.entities = new Entity[maxWidth][maxHeight];
    }

    /**
     * Parses the map from a string representation.
     * The first line is undo limit.
     * Starting from the second line, the game map is represented as follows,
     * <li># represents a {@link Wall}</li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper-case letter represents a {@link Player}.</li>
     * <li>
     * Any lower-case letter represents a {@link Box} that is only movable by the player with the corresponding upper-case letter.
     * For instance, box "a" can only be moved by player "A" and not movable by player "B".
     * </li>
     * <li>. represents an {@link Empty} position in the map, meaning there is no player or box currently at this position.</li>
     * <p>
     * Notes:
     * <li>
     * There can be at most 26 players.
     * All implementations of classes in the hk.ust.comp3021.game package should support up to 26 players.
     * </li>
     * <li>
     * For simplicity, we assume the given map is bounded with a closed boundary.
     * There is no need to check this point.
     * </li>
     * <li>
     * Example maps can be found in "src/main/resources".
     * </li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException if undo limit is negative but not -1.
     * @throws IllegalArgumentException if there are multiple same upper-case letters, i.e., one player can only exist at one position.
     * @throws IllegalArgumentException if there are no players in the map.
     * @throws IllegalArgumentException if the number of boxes is not equal to the number of box destinations.
     * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()} do not match any player on the game board,
     *                                  or if there are players that have no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        // TODO
        String[] mapTextLines = mapText.split("\n");
        int undoLimit = Integer.parseInt(mapTextLines[0].strip());
        if (undoLimit < -1) {
            throw new IllegalArgumentException("undoLimit does not accept negative numbers smaller than -1.");
        }

        int maxHeight = mapTextLines.length - 1, maxWidth = 0;
        Set<Position> destinations = new HashSet<>();
        Set<Character> playerSet = new HashSet<>();
        List<Character> boxList = new ArrayList<>();

        for (int i = 1; i < mapTextLines.length; ++i) {
            String currentLine = mapTextLines[i].strip();
            if (maxWidth < currentLine.length()) {
                maxWidth = currentLine.length();
            }
            for (int j = 0; j < currentLine.length(); ++j) {
                char currentChar = currentLine.charAt(j);

                // Case: Player
                if (currentChar >= 'A' && currentChar <= 'Z') {
                    // Repeated Player
                    if (playerSet.contains(currentChar)) {
                        throw new IllegalArgumentException("Multiple same upper-case letters in map.");
                    } else {
                        playerSet.add(currentChar);
                        // TODO: add Entity to array
                    }
                }

                // Case: Box
                if (currentChar >= 'a' && currentChar <= 'z') {
                    boxList.add(currentChar);
                    // TODO: add Entity to array
                }

                // Case: Wall
                if (currentChar == '#') {
                    // TODO: add Wall
                    // new Wall();
                }

                // Case: Destination
                if (currentChar == '@') {
                    destinations.add(Position.of(j, i - 1));
                }

                // Case: Empty
                if (currentChar == '.') {
                    // new Empty();
                }

                // Case: null
                if (currentChar == ' ') {
                    // null;
                }
            }

            // End of line but still not reach maxWidth
            for (int j = currentLine.length(); j < maxWidth; ++j) {
                // new Empty();
            }
        }

        // No players
        if (playerSet.isEmpty()) {
            throw new IllegalArgumentException("There are no players in the map.");
        }

        // Number of boxes and destinations mismatched
        if (destinations.size() != boxList.size()) {
            throw new IllegalArgumentException("Number of boxes is not equal to number of box destinations");
        }

        // Unmatched players and boxes
        for (char player: playerSet) {
            if (!boxList.contains((char)(player + 'a' - 'A'))) {
                throw new IllegalArgumentException("Player has unmatched box");
            }
        }
        for (char box: boxList.stream().collect(Collectors.toSet())) {
            if (!playerSet.contains((char)(box + 'A' - 'a'))) {
                throw new IllegalArgumentException("Box has unmatched Player");
            }
        }

        return new GameMap(maxWidth, maxHeight, destinations, undoLimit);
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    @Nullable
    public Entity getEntity(Position position) {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        // DONE
        return this.destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit.
     */
    public Optional<Integer> getUndoLimit() {
        // DONE
        return Optional.of(this.undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMaxWidth() {
        // DONE
        return this.maxWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        // DONE
        return this.maxHeight;
    }
}
