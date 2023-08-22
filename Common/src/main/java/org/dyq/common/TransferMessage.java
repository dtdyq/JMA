package org.dyq.common;

public class TransferMessage {
    public String level;
    public String body;

    public TransferMessage() {
    }

    public TransferMessage(String level, String body) {
        this.level = level;
        this.body = body;
    }

    public String getLevel() {
        return level;
    }

    public TransferMessage setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getBody() {
        return body;
    }

    public TransferMessage setBody(String body) {
        this.body = body;
        return this;
    }
}
