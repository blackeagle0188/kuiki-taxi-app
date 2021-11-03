package co.intentservice.chatui.viewholders;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;

import co.intentservice.chatui.R;
import co.intentservice.chatui.views.MessageView;

/**
 * View Holder for the Chat UI. Interfaces with the Received and Sent views and sets them up
 * with any messages required.
 * <p>
 * Original Code by Timi
 * Extended by James Lendrem, Michael Obi, Samuel Ojo
 */

public class MessageViewHolder {

    public final int STATUS_SENT = 0;
    public final int STATUS_RECEIVED = 1;

    View row;
    Context context;

    private MessageView messageView;
    private int backgroundRcv, backgroundSend;
    private int bubbleBackgroundRcv, bubbleBackgroundSend;
    private int textColorRcv, textColorSend;
    private int tStampColorRcv, tStampColorSend;

    public MessageViewHolder(View convertView, int backgroundRcv, int backgroundSend, int bubbleBackgroundRcv, int bubbleBackgroundSend, int textColorRcv, int textColorSend, int tStampColorRcv, int tStampColorSend) {
        row = convertView;
        context = row.getContext();
        messageView = (MessageView) convertView;
        this.backgroundRcv = backgroundRcv;
        this.backgroundSend = backgroundSend;
        this.bubbleBackgroundSend = bubbleBackgroundSend;
        this.bubbleBackgroundRcv = bubbleBackgroundRcv;
        this.textColorRcv = textColorRcv;
        this.textColorSend = textColorSend;
        this.tStampColorRcv = tStampColorRcv;
        this.tStampColorSend = tStampColorSend;
    }

    public void setMessage(String message) {

        messageView.setMessage(message);

    }

    public void setTimestamp(String timestamp) {

        messageView.setTimestamp(timestamp);

    }

    public void setElevation(float elevation) {

        messageView.setElevation(elevation);

    }

    public void setSeenStatus(String seen) {
        messageView.setSeenStatus(seen);
    }

    public void setSender(String sender) {
        messageView.setSender(sender);
    }

    public void setBackground(int messageType) {

        int chatMessageBackground = ContextCompat.getColor(context, R.color.cardview_light_background);
        int bubbleBackground = ContextCompat.getColor(context, R.color.cardview_light_background);
        int textColor = ContextCompat.getColor(context, R.color.black);
        int tStampColor = ContextCompat.getColor(context, R.color.black);

        switch (messageType) {
            case STATUS_RECEIVED:
                chatMessageBackground = backgroundRcv;
                bubbleBackground = bubbleBackgroundRcv;
                textColor = textColorRcv;
                tStampColor = tStampColorRcv;
                break;
            case STATUS_SENT:
                chatMessageBackground = backgroundSend;
                bubbleBackground = bubbleBackgroundSend;
                textColor = textColorSend;
                tStampColor = tStampColorSend;
                break;
        }

        messageView.setBackgroundColor(chatMessageBackground);
        messageView.setBackground(bubbleBackground);
        messageView.setTextColor(textColor);
        messageView.setTStampColor(tStampColor);

    }

}
