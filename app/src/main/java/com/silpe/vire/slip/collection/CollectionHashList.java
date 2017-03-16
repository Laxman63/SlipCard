package com.silpe.vire.slip.collection;

import com.silpe.vire.slip.dtos.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class CollectionHashList {

    //private final Map<String, Integer> positions;
    private final List<User> list;

    CollectionHashList() {
        //positions = Collections.synchronizedMap(new LinkedHashMap<String, Integer>());
        list = Collections.synchronizedList(new ArrayList<User>());
    }

    User get(int position) {
        return list.get(position);
    }

    int size() {
        return list.size();
    }

    private synchronized boolean add(User user, CollectionAdapter adapter) {
        //String uid = user.getUid();
        if (/*positions.get(uid) != null*/list.contains(user)) {
            return update(user, adapter);
        } else {
            //Integer position = size();
            //positions.put(uid, position);
            list.add(user);
            adapter.notifyItemInserted(0/*reverse(position)*/);
            return true;
        }
    }

    synchronized boolean update(User user, CollectionAdapter adapter) {
        //String uid = user.getUid();
        //Integer position = positions.get(uid);
        if (/*position == null*/!list.contains(user)) {
            return add(user, adapter);
        } else {
            int position = list.indexOf(user);
            list.set(position, user);
            adapter.notifyItemChanged(reverse(position));
            return false;
        }
    }

    synchronized void remove(String uid, CollectionAdapter adapter) {
        //Integer position = positions.get(uid);
        //if (position != null) {
        //list.remove(position.intValue());
        //int actualPosition = reverse(position - 1);
        //positions.remove(uid);
        User user = new User(uid);
        adapter.notifyItemRemoved(list.size() - list.indexOf(user) - 1);
        list.remove(user);
        //}
    }

    synchronized void update(List<User> users, CollectionAdapter adapter) {
        synchronized (list) {
            for (User user : users) {
                add(user, adapter);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private int reverse(int position) {
        return size() - position - 1;
    }

}
