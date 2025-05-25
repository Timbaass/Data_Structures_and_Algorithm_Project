package Project.Scripts;

public class myAvlTree {
    static class Node {
        int key, height;
        Node left, right;

        Node(int d) {
            key = d;
            height = 1;
        }
    }

    private Node root;

    // Yardımcı metodlar
    private int height(Node N) {
        if (N == null) return 0;
        return N.height;
    }

    private int getBalance(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Dönüş
        x.right = y;
        y.left = T2;

        // Yükseklik güncelle
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Dönüş
        y.left = x;
        x.right = T2;

        // Yükseklik güncelle
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private Node insert(Node node, int key) {
        if (node == null) return new Node(key);

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else
            return node; // Aynı elemanı ekleme

        // Yükseklik güncelle
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Denge faktörünü al
        int balance = getBalance(node);

        // 4 durum:

        // Sol Sol
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);

        // Sağ Sağ
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);

        // Sol Sağ
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Sağ Sol
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void insert(int key) {
        root = insert(root, key);
    }

    // Sıralı yazdırmak için
    public void inOrder() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.print(node.key + " ");
            inOrder(node.right);
        }
    }

    // Ağacın kök yüksekliği
    public int getHeight() {
        return height(root);
    }
}

