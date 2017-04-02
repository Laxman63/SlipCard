package com.silpe.vire.slip.components;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.silpe.vire.slip.dtos.User;

public class DatabaseManager {

    private DatabaseManager() {
    }

    private static DatabaseManager databaseManager = null;

    public static DatabaseManager getInstance() {
        if (databaseManager == null) databaseManager = new DatabaseManager();
        return databaseManager;
    }

    public DatabaseRequestBuilder retrieveConnectionsFor(Context context, User user) {
        final DatabaseReference ref = user.getConnectionsReference(context);
        return new DatabaseRequestBuilder();
    }

    public interface Action<T> {
        void consume(T t);
    }

    public static class DatabaseRequestBuilder {

        private DatabaseRequestBuilder() {
            dataSnapshotAction = null;
            databaseErrorAction = null;
        }

        private Action<DataSnapshot> dataSnapshotAction;
        private Action<DatabaseError> databaseErrorAction;

        public DatabaseRequestBuilder then(Action<DataSnapshot> consumer) {
            this.dataSnapshotAction = consumer;
            return this;
        }

        public DatabaseRequestBuilder error(Action<DatabaseError> consumer) {
            this.databaseErrorAction = consumer;
            return this;
        }

        public DatabaseRequest start() {
            return new DatabaseRequest(dataSnapshotAction, databaseErrorAction);
        }

    }

    public static class DatabaseRequest {

        private final Action<DataSnapshot> dataSnapshotAction;
        private final Action<DatabaseError> databaseErrorAction;

        private DatabaseRequest(Action<DataSnapshot> dataSnapshotAction, Action<DatabaseError> databaseErrorAction) {
            this.dataSnapshotAction = dataSnapshotAction;
            this.databaseErrorAction = databaseErrorAction;
        }

        private void start() {

        }

    }

}
