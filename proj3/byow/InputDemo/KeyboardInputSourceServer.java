package byow.InputDemo;


import byow.Networking.BYOWServer;
import edu.princeton.cs.introcs.StdDraw;
public class KeyboardInputSourceServer {
    private BYOWServer byowServer;
    private static final boolean PRINT_TYPED_KEYS = false;
    public KeyboardInputSourceServer(BYOWServer byowServer) {
        StdDraw.text(0.3, 0.3, "press m to moo, q to quit");
        this.byowServer = byowServer;
    }

    public char getNextKey() {
        while (true) {
            if (byowServer.clientHasKeyTyped()) {
                char c = Character.toUpperCase(byowServer.clientNextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
            else {
                //mouse hud
                return 'm';
            }
        }
    }
    public boolean possibleNextInput() {
        return true;
    }




}
