package application;

public class LinkedStack {

	private Node front;// the top node(header)
	public int size;//size

	public LinkedStack() {//constructor
		front = null;
		size = 0;
	}

	public void push(Object element) {//push method
		Node newNode;
		newNode = new Node(element);
		newNode.next = front;
		front = newNode;
		size++;// update size
	}

	public Object pop() {
		if (!isEmpty()) {
			Node top = front; // save reference
			front = front.next;// Remove first node
			size--;
			return top.element;// Return the element from the saved ref
		} else
			return null;
	}

	public Object peek() {//peek method

		if (!isEmpty())//check if the stack is empty
			return front.getElement();
		else
			return null;
	}

	public boolean isEmpty() {
		return (front == null);
	}
   
	
	
	public void traverse() {//print the stack
	    Node current = front;
	    while (current != null) {
	        System.out.print(current.element + " ");
	        current = current.next;
	    }
	    System.out.println(); 
	}
	

}
