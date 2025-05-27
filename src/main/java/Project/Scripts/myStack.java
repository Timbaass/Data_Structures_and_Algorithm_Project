package Project.Scripts;
import Project.Models.Call;
import java.util.ArrayList;
import java.util.List;

public class myStack {
    private List<Node> heap;

    static class Node{
        public String id;
        Node next;
        public Node(String id) {
            this.id = id;
            this.next = null;
        }
    }

    public myStack(){
        heap = new ArrayList<>();
    }

    Node head = null;
    public void addstack(Call call){
        Node newnode = new Node(call.getCallerName());
        Node temp = head;
        if (head == null){
            head = newnode;
        }else{
            newnode.next = head;
            head = newnode;
        }
    }

    public void printstack(){
        Node temp = head;
        while(temp != null){
            heap.add(0,temp);
            System.out.println("id ler" + temp.id);
            temp = temp.next;
        }
    }
}
