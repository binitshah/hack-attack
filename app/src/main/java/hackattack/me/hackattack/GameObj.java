package hackattack.me.hackattack;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JakeHoward on 1/21/17.
 */

public class GameObj {
    public double player1coordx = -1;
    public double player1coordy = -1;
    public double player2coordx = -1;
    public double player2coordy = -1;
    public int gameCode = 0;

    public GameObj() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GameObj(int gameCode) {
        this.gameCode = gameCode;
    }

//    @Exclude
//    public Map<String, Object> toMap(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("player1coordx", player1coordx);
//        result.put("player1coordy", player1coordy);
//        result.put("player2coordx", player2coordx);
//        result.put("player2coordy", player2coordy);
//        result.put("gameCode", gameCode);
//
//        return result;
//    }
}
