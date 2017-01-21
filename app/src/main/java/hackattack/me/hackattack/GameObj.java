package hackattack.me.hackattack;

/**
 * Created by JakeHoward on 1/21/17.
 */

public class GameObj {
    public int player1coordx = -1;
    public int player1coordy = -1;
    public int player2coordx = -1;
    public int player2coordy = -1;
    public int gameCode = 0;

    public GameObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GameObj(int gameCode) {
        this.gameCode = gameCode;
    }
}
