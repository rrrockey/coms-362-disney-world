package retailsales.command;

/**
 * Command Pattern interface for retail sales operations.
 * Every concrete command encapsulates a single, self-contained
 * retail action (purchase, return, restock, etc.).
 */
public interface RetailCommand {
    void execute();
}
