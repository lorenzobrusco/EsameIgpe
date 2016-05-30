package multiPlayer.format;

import control.GameManager;
import de.lessvoid.nifty.controls.Chat;
import de.lessvoid.nifty.controls.ListBox;

/**
 * 
 * this class take a string and formats it for the chatbox
 *
 */
public class FormatStringChat {

    /** player's name */
    private final String namePlayer;
    /** max length to string */
    private final int MAX_CHARACTER_IN_LINE = 80;

    /** builder */
    public FormatStringChat(String namePlayer) {
	this.namePlayer = namePlayer;
    }

    /** this method take a string and formats it and send it for the chatbox */
    public void printMessageChatBox(String messageChatBox) {
	/** take nifty's control */
    	@SuppressWarnings("unchecked")
		ListBox<String> tmp1 =  GameManager.getIstance().getNifty().getCurrentScreen()
    			.findNiftyControl("#chatBox" , ListBox.class);
//	final Chat chatController = GameManager.getIstance().getNifty().getCurrentScreen()
//		.findNiftyControl("chatMultiPlayer", Chat.class);
	final String[] tmp = messageChatBox.split(" ");
	String space = "";
	String message = "";
	for (int i = 0; i < namePlayer.length(); i++) {
	    space += " ";
	}
	space += "          ";
	for (int i = 0; i < tmp.length; i++) {
	    if (i == 0)
		message += namePlayer + ": ";
	    if ((message.length() + tmp[i].length()) <= MAX_CHARACTER_IN_LINE) {
		message += tmp[i] + " ";
	    } else {
//		chatController.receivedChatLine(message, null);
	    	tmp1.addItem(message);
		message = space + tmp[i] + " ";
	    }
	}
//	chatController.receivedChatLine(message, null);
	tmp1.addItem(message);
    }

}
