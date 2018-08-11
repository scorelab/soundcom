package soundcom.scorelab.org.soundcom;

/**
 * Created by user on 20-06-2018.
 */
import android.app.Activity;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>(); //messagelist to store messages
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    } //method to display messages

    public void add(Message message) { //add message to messages_list
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    public List<Message> getmessages() {
        return messages;
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i,View convertView, ViewGroup viewGroup)
    {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);
        System.out.println("history changed");
        if (message.isBelongsToCurrentUser()) {  //message is transmitted
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
            System.out.println("message by me");
        } else {  //message is received
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getData().getName());
            holder.messageBody.setText(message.getText());
            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
            drawable.setColor(Color.parseColor(message.getData().getColor()));
            System.out.println("message by others");
        }
        return convertView;
    }
}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}