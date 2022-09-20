package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    @NotNull
    protected final GameState state;

    protected AbstractSokobanGame(@NotNull GameState gameState) {
        this.state = gameState;
    }

    /**
     * @return True is the game should stop running.
     * For example when the user specified to exit the game or the user won the game.
     */
    protected boolean shouldStop() {
        // DONE

        // User specified to exit
        if (this.state.mostRecentAction instanceof Exit) {
            return true;
        }

        // User won the game
        if (this.state.isWin()) {
            return true;
        }

        return false;
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(@NotNull Action action) {
        // DONE
        // All updates to GameState after each Action are performed in this function

        this.state.mostRecentAction = action;
        return switch (action) {
            case InvalidInput invalidInput -> {
                yield new ActionResult.Failed(invalidInput, "Invalid Input.");
            }
            case Exit exit -> {
                yield new ActionResult.Success(exit);
            }
            case Undo undo -> {
                if (this.state.getUndoQuota().isPresent() && this.state.getUndoQuota().get() == 0) {
                    // Undo quota used up
                    yield new ActionResult.Failed(undo, "You have run out of your undo quota.");
                } else {
                    // Undo to last checkpoint
                    this.state.undo();
                    yield new ActionResult.Success(undo);
                }
            }
            case Move move -> {
                int playerId = move.getInitiator();
                Position playerPosition = this.state.getPlayerPositionById(playerId);

                if (playerPosition == null) {
                    // PlayerID does not exist
                    yield new ActionResult.Failed(move, "Player not found.");
                }

                // Process Move
                // Players can only move one box in each Move
                // Check movable by confirming nextPosition contains either own Box or Empty
                // If nextPosition contains own Box, check nextNextPosition contains Empty then can move
                // If nextPosition contains Empty, can move
                Position nextDestination = move.nextPosition(playerPosition);
                yield switch (this.state.getEntity(nextDestination)) {
                    case Empty empty -> {
                        // Player can move to nextDestination
                        this.state.move(playerPosition, nextDestination);
                        yield new ActionResult.Success(move);
                    }
                    case Box box -> {
                        if (box.getPlayerId() != playerId) {
                            // Pushed other players' Box
                            yield new ActionResult.Failed(move, "You cannot move other players' boxes.");
                        }
                        // Check nextNextDestination for space to push own Box
                        Position nextNextDestination = move.nextPosition(nextDestination);
                        switch (this.state.getEntity(nextNextDestination)) {
                            case Empty empty -> {
                                // Can push box
                                this.state.move(nextDestination, nextNextDestination);
                                this.state.move(playerPosition, nextDestination);
                                // Checkpoint after moving Box
                                this.state.checkpoint();
                                yield new ActionResult.Success(move);
                            }
                            case null, default -> {
                                // Blocked, cannot push box
                                yield new ActionResult.Failed(move, "Failed to push the box.");
                            }
                        }
                    }
                    case Wall wall -> {
                        yield new ActionResult.Failed(move, "You hit a wall.");
                    }
                    case Player player -> {
                        yield new ActionResult.Failed(move, "You hit another player.");
                    }
                };
            }
        };
    }
}
