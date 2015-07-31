package app.operatorclient.xtxt.Requestmanager;

/**
 * Created by kiran on 11/7/15.
 */
public class Customer {

    String created, customer_id, customer_name;

    public Customer(String created, String customer_id, String customer_name) {
        this.created = created;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
