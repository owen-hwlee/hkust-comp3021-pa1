package hk.ust.comp3021.tui;

import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private final PrintStream outputSteam;

    /**
     * @param outputSteam The {@link PrintStream} to write the output to.
     */
    public TerminalRenderingEngine(PrintStream outputSteam) {
        this.outputSteam = outputSteam;
    }

    @Override
    public void render(@NotNull GameState state) {
        final var builder = new StringBuilder();
        for (int y = 0; y <= state.getMapMaxHeight(); y++) {
            for (int x = 0; x <= state.getMapMaxWidth(); x++) {
                final var entity = state.getEntity(Position.of(x, y));
                final var charToPrint = switch (entity) {
                    // DONE
                    case Wall ignored -> '#';
                    case Box b -> ('a' + b.getPlayerId());
                    case Player p -> ('A' + p.getId());
                    case Empty ignored -> {
                        if (state.getDestinations().contains(Position.of(x, y))) {
                            yield '@';
                        } else {
                            yield '.';
                        }
                    }
                    case null -> ' ';
                };
                builder.append(charToPrint);
            }
            builder.append('\n');
        }
        outputSteam.print(builder);
    }

    @Override
    public void message(@NotNull String content) {
        // DONE
        // Hint: System.out is also a PrintStream.
        outputSteam.println(content);
    }
}
