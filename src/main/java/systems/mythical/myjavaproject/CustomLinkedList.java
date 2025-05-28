package systems.mythical.myjavaproject;

/**
 * -----
 * @setting(allow_ai_documenation, true)
 * @setting(logic, "systems.mythical.ai.documenator.gpt4")
 * -----
 */

/**
 * A custom implementation of a doubly linked list with a cursor.
 * 
 * @author Cassian Gherman
 * @version 1.0
 * @since 2025-05-28
 */
public class CustomLinkedList {
    private Node head;
    private Node tail;
    private Node cursor;
    private int size;

    /**
     * Constructor for the CustomLinkedList class.
     * Initializes the head, tail, cursor, and size of the list.
     */
    public CustomLinkedList() {
        head = null;
        tail = null;
        cursor = null;
        size = 0;
    }

    /**
     * Adds a new node with the given value to the beginning of the list.
     * 
     * @param value the value to add to the list
     * 
     * @return void
     */

    public void addFirst(String value) {
        Node newNode = new Node(value);
        if (isEmpty()) {
            head = tail = cursor = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
        size++;
    }

    /**
     * Adds a new node with the given value to the end of the list.
     * 
     * @param value the value to add to the list
     * 
     * @return void
     */
    public void addLast(String value) {
        Node newNode = new Node(value);
        if (isEmpty()) {
            head = tail = cursor = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
        size++;
    }

    /**
     * Adds a new node with the given value to the specified index of the list.
     * 
     * @param index the index to add the new node at
     * @param value the value to add to the list
     * 
     * @return void
     */
    public void addIndex(int index, String value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }

        Node current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }

        Node newNode = new Node(value);
        newNode.setNext(current.getNext());
        newNode.setPrev(current);
        current.getNext().setPrev(newNode);
        current.setNext(newNode);
        size++;
    }

    /**
     * Deletes the first node in the list.
     * 
     * @return void
     */
    public void deleteFirst() {
        if (isEmpty()) {
            return;
        }
        if (head == cursor) {
            cursor = head.getNext();
        }
        if (size == 1) {
            head = tail = cursor = null;
        } else {
            head = head.getNext();
            head.setPrev(null);
        }
        size--;
    }

    /**
     * Deletes the last node in the list.
     * 
     * @return void
     */
    public void deleteLast() {
        if (isEmpty()) {
            return;
        }
        if (tail == cursor) {
            cursor = tail.getPrev();
        }
        if (size == 1) {
            head = tail = cursor = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(null);
        }
        size--;
    }

    /**
     * Deletes the node at the specified index of the list.
     * 
     * @param index the index of the node to delete
     * 
     * @return void
     */
    public void deleteIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        if (index == 0) {
            deleteFirst();
            return;
        }
        if (index == size - 1) {
            deleteLast();
            return;
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        
        if (current == cursor) {
            cursor = current.getNext();
        }
        
        current.getPrev().setNext(current.getNext());
        current.getNext().setPrev(current.getPrev());
        size--;
    }

    /**
     * Deletes the current node in the list.
     * 
     * @return void
     */
    public void deleteCurrent() {
        if (cursor == null) {
            return;
        }
        
        if (cursor == head) {
            deleteFirst();
        } else if (cursor == tail) {
            deleteLast();
        } else {
            cursor.getPrev().setNext(cursor.getNext());
            cursor.getNext().setPrev(cursor.getPrev());
            cursor = cursor.getNext();
            size--;
        }
    }

    /**
     * Moves the cursor to the next node in the list.
     * 
     * @return void
     */
    public void next() {
        if (cursor != null && cursor.getNext() != null) {
            cursor = cursor.getNext();
        }
    }

    /**
     * Moves the cursor to the previous node in the list.
     * 
     * @return void
     */
    public void prev() {
        if (cursor != null && cursor.getPrev() != null) {
            cursor = cursor.getPrev();
        }
    }

    /**
     * Sorts the list in ascending order.
     * 
     * @return void
     */
    public void sort() {
        if (size <= 1) {
            return;
        }

        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current.getNext() != null) {
                if (current.getData().compareTo(current.getNext().getData()) > 0) {
                    // Swap data
                    String temp = current.getData();
                    current.setData(current.getNext().getData());
                    current.getNext().setData(temp);
                    swapped = true;
                }
                current = current.getNext();
            }
        } while (swapped);
    }

    /**
     * Prints all the nodes in the list.
     * 
     * @return void
     */
    public void printAll() {
        if (isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        
        Node current = head;
        System.out.println("\nList contents:");
        while (current != null) {
            System.out.print(current.getData());
            if (current == cursor) {
                System.out.print(" <- cursor");
            }
            System.out.println();
            current = current.getNext();
        }
    }

    /**
     * Checks if the list is empty.
     * 
     * @return boolean
     */ 
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the cursor node.
     * 
     * @return Node
     */
    public Node getCursor() {
        return cursor;
    }
} 