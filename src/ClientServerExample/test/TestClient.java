package ClientServerExample.test;

import ClientServerExample.clientapp.ClientAppController;

import java.io.IOException;


public class TestClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientAppController clientApp1 = new ClientAppController();
        ClientAppController clientApp2 = new ClientAppController();
    }
}
