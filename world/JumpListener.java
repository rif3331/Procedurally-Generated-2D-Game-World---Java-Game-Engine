package pepse.world;

import danogl.util.Vector2;

/**
 * An interface for listening to jump events in the game.
 * Implementing classes can use this to respond to the avatar's jump actions.
 */
public interface JumpListener {

    /**
     * Called when the avatar performs a jump.
     *
     * @param avatarPosition The position of the avatar at the time of the jump.
     */
    void onJump(Vector2 avatarPosition);
}
