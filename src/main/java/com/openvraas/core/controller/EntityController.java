package com.openvraas.core.controller;

import com.openvraas.core.json.JsonObject;

public interface EntityController {

    public enum Action {
        ADD,
        DEL,
        CHANGE
    }

    public static final EntityController NULL = new EntityController() {
        @Override
        public EntityController registerListenerController(
                ListenerController listenerController) {
            return this;
        }

        @Override
        public void notifyListeners(JsonObject json, Action action) {
            return;
        }

        @Override
        public String get(String id) {
            return "NULL";
        }

        @Override
        public EntityController del(JsonObject json) {
            return this;
        }

        @Override
        public EntityController change(JsonObject json) {
            return this;
        }

        @Override
        public EntityController add(JsonObject json) {
            return this;
        }
    };

    public EntityController add(JsonObject json);

    public EntityController del(JsonObject json);

    public EntityController change(JsonObject json);

    public String get(String id);

    public EntityController registerListenerController(ListenerController listenerController);

    public void notifyListeners(final JsonObject json, Action action);

}
