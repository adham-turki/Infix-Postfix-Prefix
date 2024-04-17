package application;

class Node {
	Object element;// The data stored in the node
	Node next;// Reference to the next node in the Stack
	int intNext;//next int for cursor array

	public Node(Object element) {//constructor with one argument
		this.element = element;
		this.next = null;
		intNext = 0;
	}
	public Node(Object element, int next) {//constructor with two argument
		this.element = element;
		this.intNext = next;
	}

	public Object getElement() {//getter for the element
		return element;
	}

	public Node getNext() {//getter for the next
		return next;
	}

	public void setNext(Node next) {//setter for the next
		this.next = next;
	}
}
