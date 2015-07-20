package ClientServerExample.singleserver;

import ClientServerExample.common.Controller;
import ClientServerExample.common.Message;
import ClientServerExample.common.MessageProvider;
import ClientServerExample.common.MessageTag;
import ClientServerExample.serverapp.MainServerController;

import java.io.IOException;

/**
 * Class that holds control over the CMSocketServer.
 *
 * It communicates with the MainServer when needed (see the MainServer reference in the constructor).
 * Example: client disconnects
 *      1. Client presses the "Disconnect" button, sending a message: Tag=STOP, Content:{clientId}
 *      2. CMSocketServer reads it from the communication channel. The MessageHandler receives it for processing
 *      3. The MessageHandler calls the stop(clientId) method on the SingleServerController, that tells the MainServer
 *      that a client wants to disconnect. Without a reference to the MainServer, this couldn't be possible.
 *
 * Created by Andrea on 7/16/2015.
 */
public class SingleServerController implements Controller{
    private CMSocketServer singleServer;
    private MainServerController mainController;
    private SingleServerMessageHandler messageHandler;

    public SingleServerController(CMSocketServer server, MainServerController supremeController, SingleServerMessageHandler handler) {
        this.singleServer = server;
        this.mainController=supremeController;
        this.messageHandler=handler;
        messageHandler.setController(this);
    }

    /***********************************************************************************************
     * CONTROL
     **********************************************************************************************/

    /**
     * Asks the MainServer to remove from the list of connected clients the one with the id equal to
     * the value of the "id" parameter
     *
     * @param id client to remove
     */
    public void stop(int id){
        stopRunning();
        mainController.removeSingleServer(id);
    }

    /**
     * Stops the CMSocketServer thread's loop
     */
    public void stopRunning() {
        try {
            singleServer.stopRunning();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message from the single server CSSocketServer to the client connected to it
     *
     * @param msg message to be sent
     */
    @Override
    public void sendMessage(Message msg) {
        try {
            singleServer.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the incoming message received from the CSocketClient connected to the single CMSocketServer
     *
     * @return message read from the communication channel
     */
    @Override
    public Message readMessage() {
        try {
            return singleServer.readMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The MainServer assigns a unique id to each new connected client. To make sure that the client knows it's assigned
     * id, the MainServer asks the client's corresponding "single server" to communicate this information through
     * an IDENTITY tagged message.
     *
     * This method sends a message to the client with the unique identification number
     * the MainServer has assigned to it.
     *
     * @param id    identification number that the client receives when connecting to the MainServer
     */
    public void setID(int id){

        Message msg=new Message(MessageTag.IDENTITY);
        msg.addObject(id);

        sendMessage(msg);
    }

    /**
     * Sends a STOP tagged message to the client, asking him to stop listening for messages
     *
     */
    public void stopClient(){
        sendMessage(MessageProvider.getMessage(MessageTag.STOP));
        stopRunning();
    }

    /****************************************************************************
     * VIEW RELATED
     ****************************************************************************/

    /**
     * Displays in the corresponding field on the application GUI the message received from the client
     *
     * @param str String to be displayed
     */
    public void displayMessage(String str){
        mainController.displayMessage(str);
    }
}