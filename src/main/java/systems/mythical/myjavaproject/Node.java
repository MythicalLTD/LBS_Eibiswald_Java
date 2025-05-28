package systems.mythical.myjavaproject;

/**
 * -----
 * @setting(allow_ai_documenation, true)
 * @setting(logic, "systems.mythical.ai.documenator.gpt4")
 * -----
 */

/**
 * A node in a doubly linked list.
 * 
 * @author Cassian Gherman
 * @version 1.0
 * @since 2025-05-28
 */
public class Node {
    private String data;
    private Node next;
    private Node prev;

    /**
     * Constructor for the Node class.
     * 
     * @param data the data to be stored in the node
     */
    public Node(String data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    /**
     * Gets the data of the node.
     * 
     * @return the data of the node
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data of the node.
     * 
     * @param data the data to be stored in the node
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the next node.
     * 
     * @return the next node
     */
    public Node getNext() {
        return next;
    }

    /**
     * Sets the next node.
     * 
     * @param next the next node
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * Gets the previous node.
     * 
     * @return the previous node
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * Sets the previous node.
     * 
     * @param prev the previous node
     */
    public void setPrev(Node prev) {
        this.prev = prev;
    }
} 