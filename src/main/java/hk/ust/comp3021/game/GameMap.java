package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;


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
    private Set<Position> wallsPositions;
    private Set<Position> destinations;
    private Map<Position, Integer> initialBoxesPositions;
    private Map<Position, Integer> initialPlayersPositions;
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
        // DONE
        String[] mapTextLines = mapText.split("\\R");
        int undoLimit = Integer.parseInt(mapTextLines[0]);
        if (undoLimit < -1) {
            throw new IllegalArgumentException("undoLimit does not accept negative numbers smaller than -1.");
        }

        final int maxHeight = mapTextLines.length - 1;
        int maxWidth = 0;
        Set<Position> destinations = new HashSet<>();
        Map<Position, Integer> initialBoxesPositions = new HashMap<>();
        Map<Position, Integer> initialPlayersPositions = new HashMap<>();
        Set<Position> wallsPositions = new HashSet<>();

        for (String currentLine: mapTextLines) {
            int lineLength = currentLine.length();
            if (maxWidth < lineLength) {
                maxWidth = lineLength;
            }
        }
        Entity[][] entities = new Entity[maxWidth][maxHeight];


        for (int y = 0; y < maxHeight; ++y) {
            String currentLine = mapTextLines[y + 1];
            int x = 0;
            for (; x < currentLine.length(); ++x) {
                char currentChar = currentLine.charAt(x);

                // Case: Player
                if (currentChar >= 'A' && currentChar <= 'Z') {
                    int playerId = currentChar - 'A';
                    if (initialPlayersPositions.containsValue(playerId)) {      // Repeated Player
                        throw new IllegalArgumentException("Multiple same upper-case letters in map.");
                    } else {
                        initialPlayersPositions.put(Position.of(x, y), playerId);
                        entities[x][y] = new Player(playerId);
                    }
                }

                // Case: Box
                if (currentChar >= 'a' && currentChar <= 'z') {
                    int playerId = currentChar - 'a';
                    initialBoxesPositions.put(Position.of(x, y), playerId);
                    entities[x][y] = new Box(playerId);
                }

                // Case: Wall
                if (currentChar == '#') {
                    wallsPositions.add(Position.of(x, y));
                    entities[x][y] = new Wall();
                }

                // Case: Destination
                if (currentChar == '@') {
                    destinations.add(Position.of(x, y));
                    entities[x][y] = new Empty();
                }

                // Case: Empty
                if (currentChar == '.') {
                    entities[x][y] = new Empty();
                }

                // Case: null
                if (currentChar == ' ') {
                    entities[x][y] = null;
                }
            }

            // End of line but still not reach maxWidth
            for (; x < maxWidth; ++x) {
                entities[x][y] = null;
            }
        }

        // No players
        if (initialPlayersPositions.isEmpty()) {
            throw new IllegalArgumentException("There are no players in the map.");
        }

        // Number of boxes and destinations mismatched
        if (destinations.size() != initialBoxesPositions.size()) {
            throw new IllegalArgumentException("Number of boxes is not equal to number of box destinations");
        }

        // Unmatched players and boxes
        for (int playerId: initialPlayersPositions.values()) {
            if (!initialBoxesPositions.containsValue(playerId)) {
                throw new IllegalArgumentException("Player has unmatched box");
            }
        }
        for (int playerId: initialBoxesPositions.values()) {
            if (!initialPlayersPositions.containsValue(playerId)) {
                throw new IllegalArgumentException("Box has unmatched Player");
            }
        }

        GameMap tempGameMap = new GameMap(maxWidth, maxHeight, destinations, undoLimit);
        tempGameMap.entities = entities;
        tempGameMap.initialBoxesPositions = initialBoxesPositions;
        tempGameMap.initialPlayersPositions = initialPlayersPositions;
        tempGameMap.wallsPositions = wallsPositions;
        return tempGameMap;
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    @Nullable
    public Entity getEntity(Position position) {
        // DONE
        return this.entities[position.x()][position.y()];
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        // DONE
        this.entities[position.x()][position.y()] = entity;
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
        // DONE
        return new HashSet<>(initialPlayersPositions.values());
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
