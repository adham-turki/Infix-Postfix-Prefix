package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Cursor {
	
	private Node[] cursorArray;//array of nodes
	private final static int MAX_SIZE = 10000;// max array size of cursor

	public void initialization() {//method to initialize the cursor array
		cursorArray = new Node[MAX_SIZE];
		for (int i = 0; i < MAX_SIZE; i++)
			cursorArray[i] = new Node(null, i + 1);
		cursorArray[MAX_SIZE - 1].intNext = 0;
	}
		
	public int cursorAlloc() {
		int p = cursorArray[0].intNext;
		cursorArray[0].intNext = cursorArray[p].intNext;
		return p;// return the index of the available node (most likely empty node)
	}

	public int createList() {
		/* create new empty list */
		int l = cursorAlloc();
		if (l == 0)
			System.out.println("Error:out of space");
		else
			cursorArray[l] = new Node("-", 0);// Empty Linked List
		return l; /* Head of the list */
	}

	public void cursorFree(int p) {
		cursorArray[p].element = null; // free the content
		cursorArray[p].intNext = cursorArray[0].intNext;
		cursorArray[0].intNext = p;
	}

	public boolean isNull(int l) {
		/* return true if the list not created */
		return cursorArray[l] == null;
	}

	public boolean isEmpty(int l) {
		// return true if the list is empty
		return cursorArray[l].intNext == 0;
	}

	public boolean isLast(int p) {
		// check if the node p is last or not
		return cursorArray[p].intNext == 0;
	}

	public void insertAtHead(Object data, int l) {
		if (isNull(l)) // list not created
			return;
		int p = cursorAlloc();
		if (p != 0) {//insert the object
			cursorArray[p] = new Node(data, cursorArray[l].intNext);
			cursorArray[l].intNext = p;
		} else
			System.out.println("Out Of Space");
	}

	public int find(Object data, int l) {//method to dint a specific object
		int p = cursorArray[l].intNext;
		while ((p != 0) && !cursorArray[p].element.equals(data))//loop to find the object
			p = cursorArray[p].intNext;
		return p;
	}
	public void remove(Object data, int l) {//method to remove a specific object
        int pos = findPrevious(data, l);
        if (cursorArray[pos].intNext != 0) {
            int tmp = cursorArray[pos].intNext;
            cursorArray[pos].intNext = cursorArray[tmp].intNext;
            cursorFree(tmp);
        }
    }

    private int findPrevious(Object data, int l) {//method to get the previous
        int pos = l;
        int current = cursorArray[l].intNext;
        while ((current != 0) && !cursorArray[current].element.equals(data)) {
            pos = current;
            current = cursorArray[current].intNext;
        }
        return pos;
    }


   

   
    
   
    

}