package app.operatorclient.xtxt.Requestmanager;

import org.json.JSONObject;

/**
 * Created by kiran on 21/7/15.
 */
public class Message {

    String message_id, message, created, type, status;
    JSONObject visual_content;

    public Message(String message_id, String message, String created, String type, String status, JSONObject visual_content) {
        this.message_id = message_id;
        this.message = message;
        this.created = created;
        this.type = type;
        this.status = status;
        this.visual_content = visual_content;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONObject getVisual_content() {
        return visual_content;
    }

    public void setVisual_content(JSONObject visual_content) {
        this.visual_content = visual_content;
    }
}
