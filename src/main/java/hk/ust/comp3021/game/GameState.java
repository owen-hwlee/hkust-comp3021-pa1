package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.Action;
import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * The state of the Sokoban Game.
 * Each game state represents an ongoing game.
 * As the game goes, the game state changes while players are moving while the original game map stays the unmodified.
 * <b>The game state should not modify the original game map.</b>
 * <p>
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
public class GameState {

    private final GameMap originalGameMap;
    // Current locations of all crates
    private Map<Position, Integer> currentCratesLocations;
    // Most recent Action
    Action mostRecentAction;
    // Current location of player
    private Map<Integer, Position> currentPlayersLocations;
    // Undo quota left
    private int undoQuotaLeft;
    // Map of Entity at current instance
    private Entity[][] currentMap;
    // Checkpoints
    private List<Checkpoint> checkpoints;

    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(@NotNull GameMap map) {
        // DONE
        this.originalGameMap = map;
        this.undoQuotaLeft = originalGameMap.getUndoLimit().isPresent() ? originalGameMap.getUndoLimit().get() : -1;
        this.checkpoints = new ArrayList<>();

        this.currentMap = this.initializeMapFromOriginalGameMap();
        this.updateStatesFromCurrentMap();
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public @Nullable Position getPlayerPositionById(int id) {
        // DONE
        return this.currentPlayersLocations.get(id);
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    public @NotNull Set<Position> getAllPlayerPositions() {
        // DONE
        return new HashSet<>(this.currentPlayersLocations.values());
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public @Nullable Entity getEntity(@NotNull Position position) {
        // DONE
        return this.currentMap[position.x()][position.y()];
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        // DONE
        return this.originalGameMap.getDestinations();
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    public Optional<Integer> getUndoQuota() {
        // DONE
        if (this.undoQuotaLeft == -1) {
            return Optional.empty();
        } else {
            return Optional.of(this.undoQuotaLeft);
        }
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true is the game wins.
     */
    public boolean isWin() {
        // DONE
        return this.originalGameMap.getDestinations().containsAll(this.currentCratesLocations.keySet());
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     * <b>The validity of the move of the entity in one position to another need not to check.</b>
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        // DONE
        // Perform swapping 2 Entities Positions
        Entity entityFrom = this.currentMap[from.x()][from.y()];

        switch (entityFrom) {
            case Player player -> this.currentPlayersLocations.put(player.getId(), to);
            case Box box -> {
                this.currentCratesLocations.remove(from);
                this.currentCratesLocations.put(to, box.getPlayerId());
            }
            case null, default -> {}
        }

        this.currentMap[from.x()][from.y()] = this.currentMap[to.x()][to.y()];
        this.currentMap[to.x()][to.y()] = entityFrom;
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     * <p>
     * Checkpoint is used in {@link GameState#undo()}.
     * Every undo actions reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        // DONE
        this.checkpoints.add(Checkpoint.of(this.currentPlayersLocations, this.currentCratesLocations));
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases the undo quota by one.
     * <p>
     * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
     * revert to the initial game state.
     */
    public void undo() {
        // DONE
        if (this.getUndoQuota().isPresent()) {
            this.undoQuotaLeft--;
        }

        this.mostRecentAction = null;

        this.checkpoints.remove(this.checkpoints.size() - 1);
        if (this.checkpoints.isEmpty()) {
            // Revert to initial game state
            this.currentMap = this.initializeMapFromOriginalGameMap();
        } else {
            // Revert to previous checkpoint
            Checkpoint checkpoint = this.checkpoints.get(this.checkpoints.size() - 1);

            // Remove Players and Boxes from currentMap
            for (int playerId: checkpoint.playerLocations.keySet()) {
                // Remove Player from currentMap
                Position playerPosition = this.getPlayerPositionById(playerId);
                this.currentMap[playerPosition.x()][playerPosition.y()] = new Empty();
            }
            for (Position cratePosition: this.currentCratesLocations.keySet()) {
                // Remove Box from currentMap
                this.currentMap[cratePosition.x()][cratePosition.y()] = new Empty();
            }

            // Add Players and Boxes to currentMap by Checkpoint Positions
            for (int playerId: checkpoint.playerLocations.keySet()) {
                // Add Player to currentMap
                Position playerPosition = checkpoint.playerLocations.get(playerId);
                this.currentMap[playerPosition.x()][playerPosition.y()] = new Player(playerId);
            }
            for (Position cratePosition: checkpoint.cratesLocations.keySet()) {
                // Add Box to currentMap
                int playerId = checkpoint.cratesLocations.get(cratePosition);
                this.currentMap[cratePosition.x()][cratePosition.y()] = new Box(playerId);
            }
        }

        this.updateStatesFromCurrentMap();
    }

    /**
     * Get the maximum width of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        // DONE
        return this.originalGameMap.getMaxWidth();
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        // DONE
        return this.originalGameMap.getMaxHeight();
    }

    // Helper functions
    private Entity[][] initializeMapFromOriginalGameMap() {
        Entity[][] tempMap = new Entity[this.getMapMaxWidth()][this.getMapMaxHeight()];
        for (int x = 0; x < this.getMapMaxWidth(); ++x) {
            for (int y = 0; y < this.getMapMaxHeight(); ++y) {
                tempMap[x][y] = this.originalGameMap.getEntity(Position.of(x, y));
            }
        }
        return tempMap;
    }

    private void updateStatesFromCurrentMap() {
        this.currentCratesLocations = new HashMap<>();
        this.currentPlayersLocations = new HashMap<>();
        for (int x = 0; x < this.getMapMaxWidth(); ++x) {
            for (int y = 0; y < this.getMapMaxHeight(); ++y) {
                switch (this.getEntity(Position.of(x, y))) {
                    case Box box -> this.currentCratesLocations.put(Position.of(x, y), box.getPlayerId());
                    case Player player -> this.currentPlayersLocations.put(player.getId(), Position.of(x, y));
                    case null, default -> {}
                }
            }
        }
    }

    private record Checkpoint(
            Map<Integer, Position> playerLocations,
            Map<Position, Integer> cratesLocations
    ) {
        static @NotNull Checkpoint of(
                Map<Integer, Position> playerLocations,
                Map<Position, Integer> cratesLocations
        ) {
            return new Checkpoint(new HashMap<>(playerLocations), new HashMap<>(cratesLocations));
        }
    }
}
