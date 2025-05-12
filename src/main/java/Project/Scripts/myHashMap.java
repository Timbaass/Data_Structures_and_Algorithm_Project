package Project.Scripts;

public class myHashMap <K, V>{

    static class Entry<K, V>{
        K key;
        V value;
        Entry<K, V> next;
        public Entry(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private static final int INITIAL_CAPACITY = 10;
    Entry<K, V>[] table;

    public myHashMap(){
        table = new Entry[INITIAL_CAPACITY];
    }

    public int hash(K key){
        return Math.abs(key.hashCode() % (table.length));
    }

    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;
        int count = 0;

        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            prev = current;
            current = current.next;
            count++;
        }

        if (count < 5) {
            Entry<K, V> newEntry = new Entry<>(key, value);
            if (prev == null) {
                table[index] = newEntry;
            } else {
                prev.next = newEntry;
            }
        }
    }

    public V get(K key){
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null){
            if (current.key.equals(key)){
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    public void remove(K key){
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        if (current == null){
            return;
        }

        while (current != null){
            if (current.key.equals(key)){
                if (prev == null){
                    table[index] = current.next;
                } else{
                    prev.next = current.next;
                }
                return;
            }
            prev = current;
            current = current.next;
        }
    }

    public void print(){
        for (int i = 0; i < table.length; i++){
            Entry<K, V> current = table[i];
            System.out.print("[" + i + "] = ");
            while (current != null){
                System.out.print("[" + current.key + "]" + "=" + "[" + current.value + "]" + "-->");
                current = current.next;
            }
            System.out.println("null");
        }
    }
}

