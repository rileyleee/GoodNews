package com.saveurlife.goodnews.ble;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class MapAdapter extends BaseAdapter {
    private final Context context;
    private final int layoutResId;
    private final Map<String, Map<String, BleMeshConnectedUser>> data;
    private final ArrayList<BleMeshConnectedUser> users;

    public MapAdapter(Context context, int layoutResId, Map<String, Map<String, BleMeshConnectedUser>> data) {
        this.context = context;
        this.layoutResId = layoutResId;
        this.data = data;
        this.users = new ArrayList<>();
        for (Map<String, BleMeshConnectedUser> userMap : data.values()) {
            users.addAll(userMap.values());
        }
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public BleMeshConnectedUser getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("getView", "getView");
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(layoutResId, parent, false);
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BleMeshConnectedUser user = getItem(position);
        holder.textView.setText(user.toString());

        return convertView;
    }

    public void updateData() {
        users.clear();
        ArrayList<String> existingUserIds = new ArrayList<>();
        for (Map<String, BleMeshConnectedUser> userMap : data.values()) {
            for(BleMeshConnectedUser bleMeshConnectedUser : userMap.values()){
                if(!existingUserIds.contains(bleMeshConnectedUser.getUserId())){
                    users.add(bleMeshConnectedUser);
                    existingUserIds.add(bleMeshConnectedUser.getUserId());
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addAll(Map<String, Map<String, BleMeshConnectedUser>> newData) {
        data.clear();
        data.putAll(newData);
        updateData();
    }



    static class ViewHolder {
        TextView textView;
    }
}
