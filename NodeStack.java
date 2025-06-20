
package reservasi_foosen;

public class NodeStack {
    OrderMakanan data;
    NodeStack next;

    public NodeStack(OrderMakanan data) {
        this.data = data;
        this.next = null;
    }
}
