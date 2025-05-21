package Project.Scripts;

import Project.Models.Call;
import java.util.ArrayList;
import java.util.List;

public class myPriorityQueue {
    private List<Call> heap;

    public myPriorityQueue() {
        heap = new ArrayList<>();
    }

    public void enqueue(Call call) {
        heap.add(call);
        heapifyUp(heap.size() - 1);
    }

    public Call dequeue() {
        if (heap.isEmpty()) return null;

        Call result = heap.get(0);
        Call last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }

        return result;
    }

    public Call peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public List<Call> getAllCalls() {
        return new ArrayList<>(heap); // sadece liste olarak almak istersen
    }

    // ---------------- Heap Logic -----------------

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(heap.get(index), heap.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int left, right, smallest;

        while (true) {
            left = 2 * index + 1;
            right = 2 * index + 2;
            smallest = index;

            if (left < heap.size() && compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < heap.size() && compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private int compare(Call c1, Call c2) {
        return Integer.compare(c1.getPriority().getLevel(), c2.getPriority().getLevel());
    }

    private void swap(int i, int j) {
        Call temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
