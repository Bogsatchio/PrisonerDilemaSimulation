package main;

public class ResponsePair {
    public ResponsePair(boolean myResponse, boolean opponentResponse) {
        this.myResponse = myResponse;
        this.opponentResponse = opponentResponse;
    }
    boolean myResponse;
    boolean opponentResponse;

    @Override
    public String toString() {
        return "My response: " + myResponse + " | Opponent's response: " + opponentResponse;
    }

    public boolean getMyResponse() {
        return myResponse;
    }

    public boolean getOpponentResponse() {
        return opponentResponse;
    }
}
