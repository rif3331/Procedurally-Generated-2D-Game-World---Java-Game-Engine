package pepse.world;

/**
 * An interface for listening to energy updates in the game.
 * Implementing classes can use this to respond to changes in energy levels.
 */
public interface EnergyUpdateListener {

    /**
     * Called when the energy level is updated.
     *
     * @param newEnergy The updated energy level.
     */
    void onEnergyUpdated(int newEnergy);
}
