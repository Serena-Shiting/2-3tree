import java.util.ArrayList;

public class Tree {
    static public Node root;
    private int size;

    Tree() {
        root = null;
        size = 0;
    }

    public boolean insert(int key){
        if(root == null){
            root = new Node(key);
            size++;
            return true;
        }
        Node curNode = root;
        //start from the root, find the right leaf Node to insert
        //at the same time, check duplicate
        while (!curNode.isLeaf()) {
            for (int i = 0; i < curNode.nodeSize; i++) {
                if (curNode.data[i] == key) {
                    return false;
                }
            }
                curNode = curNode.getNextChild(key);
        }

        //found the leaf, check duplicate before insert
        for (int i = 0; i < curNode.nodeSize; i++) {
            if (curNode.data[i] == key) {
                return false;
            }
        }
            curNode.insertKey(key);
            size++;
            return true;
    }

    /**
     * the number of keys in the entire tree
     * @return the size of the tree
     */
    public int size(){
        return size;
    }

    /**
     *If x is not in the tree, it should return 0.
     * @param x is an integer can be stored in the tree
     * @return the int size of the subtree rooted at the node that contains integer x
     */
    public int size(int x){
        return root.findSubtreeRoot(x);
    }

    /**
     *if the integers were sorted and put into an array in ascending order.
     * For tree t, t.get(0) should return the minimum value in the tree,
     * and t.get(t.size()-1) should return the maximum
     * @param x kind like an index
     * @return item that store at index x
     */
    public int get(int x) {
       int value = root.getIndex(x);
        return value;
    }
}

class Node {
    private Node parent;
    private int NODE_CAPACITY = 3;
    public int data[] = new int[NODE_CAPACITY];
    ArrayList<Node> childrenList = new ArrayList<Node>();
    public int nodeSize = 0;
    public int sizeOfSubtree;

    Node(int n) {
        data[0] = n;
        nodeSize++;
        sizeOfSubtree = nodeSize;
    }

    public boolean isLeaf() {
        return childrenList.size() == 0;
    }

    public void insertKey(int k) {
        //insert the key to data（sorted in increasing order）even if exceeded capacity
        int i = 0;
        while(i < nodeSize && k > data[i]){
            i++;
        }
        for(int j = nodeSize; j > i; j--){
            data[j] = data[j-1];
        }
        data[i] = k;
        nodeSize++;
        sizeOfSubtree++;

        //spilt if exceeded capacity
        if(nodeSize == NODE_CAPACITY){
            splitNode();
        }

        updateParentSubtreeSIze();
    }

    public void updateParentSubtreeSIze(){
        if(parent == null){
            return;
        }
        parent.sizeOfSubtree = parent.nodeSize;
        if (parent.childrenList.size() > 0){
            for(int i = 0; i < parent.childrenList.size(); i++){
                parent.sizeOfSubtree += parent.childrenList.get(i).sizeOfSubtree;
            }
            parent.updateParentSubtreeSIze();
        }
    }

    public void splitNode() {
        Node newNode = new Node(data[2]);
        if(parent == null){
            parent = new Node(data[1]);
            Tree.root = parent;
            parent.addChildren(this);
            parent.addChildren(newNode);
            sizeOfSubtree -= 2;
        }else {
            parent.addChildren(newNode);
            sizeOfSubtree -= 2;
            parent.insertKey(data[1]);
        }
        newNode.parent = parent;
        nodeSize = 1;
        if(childrenList.size() == 4){
            spiltChildren(newNode);
        }

    }

    public void addChildren(Node n){
        if(childrenList.size() == 0){
            childrenList.add(n);
        }else {
            int i = 0;
            while(i < childrenList.size() && n.data[0] > childrenList.get(i).data[0]){
                i++;
            }
            childrenList.add(n);
            for (int j = childrenList.size()-1; j > i; j--) {
                childrenList.set(j, childrenList.get(j - 1));
            }
            childrenList.set(i, n);
        }
    }

    public void spiltChildren(Node newNode) {
        //update sizeOfSubtree, newNode = parent.childrenList.get(1)
        newNode.sizeOfSubtree += childrenList.get(2).sizeOfSubtree;
        newNode.sizeOfSubtree += childrenList.get(3).sizeOfSubtree;
        sizeOfSubtree -= childrenList.get(2).sizeOfSubtree;
        sizeOfSubtree -= childrenList.get(3).sizeOfSubtree;
        //connect children
        newNode.childrenList.add(childrenList.get(2));
        newNode.childrenList.add(childrenList.get(3));
        //connect parent
        newNode.childrenList.get(0).parent = newNode;
        newNode.childrenList.get(1).parent = newNode;
        //remove children
        childrenList.remove(3);
        childrenList.remove(2);

    }

    /**
     * find the root of the subtree that contains x
     * @param x is an integer can be stored in the tree
     * @return the number of keys the subtree rooted at the node that contains integer x has
     */
    public int findSubtreeRoot(int x){
        //check if the current Node contains the integer x
        //if yes, return the size of that subtree
        for (int i = 0; i < nodeSize; i++) {
            if (data[i] == x)
                 return sizeOfSubtree;
        }
        //Node doesn't contain the integer x, check if it's leaf
        //if not a leaf, continue look for the integer
        //if it is leaf, return 0, the whole tree doesn't contain x
        if(!isLeaf())
            return getNextChild(x).findSubtreeRoot(x);

        return 0;
    }

    public Node getNextChild(int key) {
        for(int i = 0; i < nodeSize; i++){
            if(key < data[i])
                return childrenList.get(i);
        }
        return childrenList.get(childrenList.size() - 1);
    }

    public int getIndex(int x){
        int smallerSubtree = 0;
        if(isLeaf()){
            return data[x];
        }
        for(int i = 0; i < childrenList.size(); i++){
            if(x < (smallerSubtree + childrenList.get(i).sizeOfSubtree)){
                return childrenList.get(i).getIndex(x-smallerSubtree);
            }
            if(smallerSubtree + childrenList.get(i).sizeOfSubtree + 1 == x+1){
                return data[i];
            }
            smallerSubtree += (childrenList.get(i).sizeOfSubtree + 1);
        }
        //look for the largest child
        return childrenList.get(childrenList.size()-1).getIndex(x);
    }
}



