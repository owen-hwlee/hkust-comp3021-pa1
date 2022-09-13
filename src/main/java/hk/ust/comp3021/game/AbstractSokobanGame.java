package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.utils.NotImplementedException;
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
        // TODO

        // User specified to exit
        if (false) {    // TODO: implement specify to exit
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
        // TODO
        return switch (action) {
            case InvalidInput invalidInput -> {
                yield new ActionResult.Failed(action, "Invalid Input.");
            }
            case Exit exit -> {
                // TODO: handle exit
                yield new ActionResult.Success(action);
            }
            case Undo undo -> {
                // TODO: handle undo
                yield new ActionResult.Success(action);
            }
            case Move move -> {
                // TODO: handle move
                // if can't move, use ActionResult.Failed()
                yield new ActionResult.Success(action);
            }
        };
    }
}
