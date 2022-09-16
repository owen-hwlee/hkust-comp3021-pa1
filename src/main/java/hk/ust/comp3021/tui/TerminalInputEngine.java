package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

    /**
     * The {@link Scanner} for reading input from the terminal.
     */
    private final Scanner terminalScanner;

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        // This is an example showing how to read a line from the Scanner class.
        // Feel free to change it if you do not like it.
        final var inputLine = terminalScanner.nextLine();

        // TODO
        String instruction = inputLine.strip().toUpperCase();

        // EXIT: exit game
        if (instruction.equals("EXIT")) {
            return new Exit(-1);
        }

        // ASWD: move Player with ID 0 to Left, Down, Up, Right
        if (instruction.equals("A")) {
            return new Move.Left(0);
        } else if (instruction.equals("S")) {
            return new Move.Down(0);
        } else if (instruction.equals("W")) {
            return new Move.Up(0);
        } else if (instruction.equals("D")) {
            return new Move.Right(0);
        }

        // HJKL: move Player with ID 1 to Left, Down, Up, Right
        if (instruction.equals("H")) {
            return new Move.Left(1);
        } else if (instruction.equals("J")) {
            return new Move.Down(1);
        } else if (instruction.equals("K")) {
            return new Move.Up(1);
        } else if (instruction.equals("L")) {
            return new Move.Right(1);
        }

        // U: undo to previous GameState
        if (instruction.equals("U")) {
            return new Undo(-1);
        }

        return new InvalidInput(-1, "Invalid Input.");
    }
}
