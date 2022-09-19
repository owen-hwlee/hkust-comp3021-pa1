package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.ActionResult;
import hk.ust.comp3021.game.AbstractSokobanGame;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.game.RenderingEngine;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal-based game only support at most two players, although the hk.ust.comp3021.game package supports up to 26 players.
     * This is only because it is hard to control too many players in a terminal-based game.
     *
     * @param gameState       The game state.
     * @param inputEngine     the terminal input engin.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        // DONE
        // Check the number of players
        if (gameState.getAllPlayerPositions().size() > 2) {
            throw new IllegalArgumentException("There cannot be more than two players in the map!");
        }
    }

    @Override
    public void run() {
        // DONE
        this.renderingEngine.message("Sokoban game is ready.");
        this.renderingEngine.render(this.state);

        // Game terminates when the game is won or the player performs Exit action
        // Deadlock checking is not required
        while (!this.shouldStop()) {
            // Game loop
            if (this.state.getUndoQuota().isPresent()) {
                this.renderingEngine.message("Undo Quota: %d".formatted(this.state.getUndoQuota().get()));
            } else {
                this.renderingEngine.message("Unlimited");
            }
            this.renderingEngine.message(">>>");

            switch (this.processAction(this.inputEngine.fetchAction())) {
                case ActionResult.Success success -> {}
                case ActionResult.Failed failed -> {
                    this.renderingEngine.message(failed.getReason());
                }
            }

            this.renderingEngine.render(this.state);
        }

        this.renderingEngine.message("Game exits.");
        if (this.state.isWin()) {
            this.renderingEngine.message("You win.");
        }
    }
}
