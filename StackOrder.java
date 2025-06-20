package reservasi_foosen;

public class StackOrder {
    private NodeStack top;

    public void push(OrderMakanan data) { //Menambahkan pesanan ke stack
        NodeStack node = new NodeStack(data);
        node.next = top;
        top = node;
    }

    public OrderMakanan pop() { //Menghapus dan mengambil data teratas dari stack
        if (top == null) return null;
        OrderMakanan data = top.data;
        top = top.next;
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void tampilkan() {
        NodeStack current = top;
        System.out.println("\n== Riwayat Order (TOP -> BAWAH) ==");
        while (current != null) {
            OrderMakanan o = current.data;
            System.out.printf("- %s x%d\n", o.getMenu().getNama(), o.getJumlah());
            current = current.next;
        }
    }

    public void clear() {
        top = null;
    }
}
