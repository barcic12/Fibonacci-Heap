/**

* Name: Bar Cicurel

*/

 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 * 
 * 
 */

public class FibonacciHeap {

	public static int totalLinks, totalCuts;
	public HeapNode min;
	public int nodesNumber, treesNumber, markedNumber;
	public HeapNode[] buckets;

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is isEmpty.
	 * 
	 */
	// complexity: O(1)
	public boolean isEmpty() {
		if (this.min == null)
			return true;
		return false;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 * 
	 * Returns the new node created.
	 */
	// complexity: O(1)
	public HeapNode insert(int key) {

		HeapNode newNode = new HeapNode(key);
		if (this.isEmpty() == true) {
			this.min = newNode;
			this.min.setNext(this.min);
		} else {
			this.insertAfter(this.min, newNode);
			this.updateMin(newNode);
		}
		this.updateNumOfNodes(1);
		this.updateNumOfTrees(1);
		return newNode;
	}
	
	/**
	 *  adds newNode after node
	 */
	// complexity: O(1)
	public void insertAfter(HeapNode node, HeapNode newNode) {
		HeapNode nextNode = node.getNext();
		node.setNext(newNode);
		newNode.setNext(nextNode);
	}
	
	/**
	 *  checks if the key of node is smaller than the current min and if so it updates the min
	 */
	// complexity: O(1)
	public void updateMin(HeapNode node) {
		if (node.getKey() < this.min.getKey())
			this.min = node;
	}

	/**
	 *  adds i to treesNumber
	 */
	// complexity: O(1)
	public void updateNumOfTrees(int i) {
		this.treesNumber += i;
	}

	/**
	 *  adds i to nodesNumber
	 */
	// complexity: O(1)
	public void updateNumOfNodes(int i) {
		this.nodesNumber += i;
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	// complexity: WC O(n), amortized O(logn)
	public void deleteMin() {

		if (this.isEmpty() == true)
			return;
		HeapNode temp = this.min;
		HeapNode nextNode = min.getNext();
		HeapNode prevNode = min.getPrev();
		this.updateNumOfNodes(-1);

		if (this.min.hasChild() == true) {
			HeapNode minChild = min.getChild();
			updateNumOfTrees(this.min.getRank() - 1);
			this.parentCleaner(minChild);
			if (this.min.hasSibling() == true) {
				minChild.getPrev().setNext(nextNode);
				prevNode.setNext(minChild);
				this.min = this.min.getNext();
			} else {
				this.min = minChild;
			}
		} else {
			if (this.min.hasSibling() == true) {
				prevNode.setNext(min.getNext());
				this.min = min.getNext();
			} else {
				this.min = null;
			}
			this.updateNumOfTrees(-1);
		}
		this.buckets = heapNodeLogArray();
		this.min = this.consolidate();
		temp.child = null;
	}

	/**
	 * gets a node and sets all of its parents to be null
	 */
	// complexity: O(logn)
	public void parentCleaner(HeapNode node) {
		HeapNode start = node;
		do {
			node.parent = null;
			node = node.getNext();
		} while (node != start);
	}

	/**
	 * returns an empty array with size equals to the log of the heap's size
	 */
	// complexity: O(1)
	public HeapNode[] heapNodeLogArray() {
		int arraySize = 2 * (int) (Math.log(this.nodesNumber) / Math.log(2)) + 1;
		return new HeapNode[arraySize];
	}
	
	/**
	 * calls toBuckets and fromBuckets in order to the trees and find a new minimum
	 */
	// complexity: WC O(n), amortized O(logn) 
	public HeapNode consolidate() {
		if (this.min == null) {
			return null;
		} else {
			this.toBuckets();
			return this.fromBuckets();
		}
	}
	
	/**
	 * puts in the i'th place in the array pointer to the root of the tree with rank i
	 */
	// complexity: WC O(n), amortized O(logn) 
	public void toBuckets() {
		HeapNode a = this.min;
		(a.getPrev()).next = null;
		HeapNode b;
		while (a != null) {
			b = a;
			a = a.getNext();
			b.setNext(b);
			while (this.buckets[b.getRank()] != null) {
				b = this.linkNodes(b, this.buckets[b.getRank()]);
				this.buckets[b.getRank() - 1] = null;
			}
			this.buckets[b.getRank()] = b;
		}
	}
	
	/**
	 * makes a new heap from the field buckets.
	 */
	// complexity: O(logn) 
	public HeapNode fromBuckets() {
		int count = 0;
		HeapNode node = null;

		for (int i = 0; i < this.buckets.length; i++) {
			if (this.buckets[i] != null) {
				count++;
				if (node == null) {
					node = this.buckets[i];
					node.setNext(node);
				} else {
					insertAfter(node, this.buckets[i]);
					if (this.buckets[i].key < node.key)
						node = this.buckets[i];
				}
			}
		}
		this.treesNumber = count;
		return node;
	}
	
	/**
	 * gets 2 nodes and connects them.
	 */
	// complexity: O(1)
	public HeapNode linkNodes(HeapNode node1, HeapNode node2) {
		totalLinks++;
		this.treesNumber--;
		HeapNode temp = null;
		if (node1.getKey() > node2.getKey()) {
			temp = node1;
			node1 = node2;
			node2 = temp;
		}
		if (node1.hasChild() == true)
			insertAfter(node1.getChild(), node2);

		node1.child = node2;
		node2.parent = node1;
		node1.rank++;
		return node1;
	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 *
	 */
	// complexity: O(1)
	public HeapNode findMin() {
		return this.min;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	// complexity: O(1)
	public void meld(FibonacciHeap heap2) {
		HeapNode min2 = heap2.findMin();
		HeapNode prev2min = min2.getPrev();
		HeapNode nextMin1 = min.getNext();
		this.min.setNext(min2);
		prev2min.setNext(nextMin1);
		this.updateNumOfNodes(heap2.size());
		this.updateNumOfTrees(heap2.treesNumber);
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	// complexity: O(1)
	public int size() {
		return this.nodesNumber;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	// complexity: O(n)
	public int[] countersRep() {
		int[] array = this.intLogArray();
		int rank;
		HeapNode current = this.min;
		do {
			rank = current.getRank();
			array[rank]++;
			current = current.next;
		} while (current != this.min);
		return array;
	}

	/**
	 * returns an empty array with size equals to the log of the heap's size
	 */
	// complexity: O(1)
	public int[] intLogArray() {
		int arraySize = 2 * (int) (Math.log(this.nodesNumber) / Math.log(2)) + 1;
		return new int[arraySize];
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	// complexity: WC O(n), amortized O(logn)
	public void delete(HeapNode x) {
		this.decreaseKey(x, x.getKey() + 1);
		this.deleteMin();
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	// complexity: WC O(n), amortized O(1)
	public void decreaseKey(HeapNode x, int delta) {
		x.key = x.key - delta;
		HeapNode newNode = x.getParent();
		if (newNode != null && x.getKey() < newNode.getKey()) {
			cascadingCut(x, newNode);
		}
		if (this.min != x && x.key < this.min.key)
			this.min = x;
	}
	
	/**
	 * cuts until it gets to an unmarked parent and then the function marks it
	 */
	// complexity: WC O(n), amortized O(1)
	public void cascadingCut(HeapNode a, HeapNode b) {
		cut(a, b);
		if (b.parent != null) {
			if (b.marked == false) {
				b.marked = true;
				this.markedNumber++;
			} else
				cascadingCut(b, b.parent);
		}
	}

	/**
	 * gets father and son and disconnects between them.
	 */
	// complexity: O(1)
	public void cut(HeapNode x, HeapNode y) {
		totalCuts++;
		this.treesNumber++;
		if (x.marked == true) {
			x.marked = false;
			this.markedNumber--;
		}
		y.rank--;
		x.setParent(null);
		if (x.getNext() == x)
			y.child = null;
		else {
			y.child = x.getNext();
			x.getPrev().next = x.getNext();
			x.getNext().prev = x.getPrev();
		}
		x.setNext(x);
		insertAfter(min, x);
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	// complexity: O(1)
	public int potential() {
		return this.treesNumber + 2 * this.markedNumber;
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	// complexity: O(1)
	public static int totalLinks() {
		return totalLinks;
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	// complexity: O(1)
	public static int totalCuts() {
		return totalCuts;
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k*deg(H)). You are not allowed to change H.
	 */
	// complexity: O(k*deg(H))
	public static int[] kMin(FibonacciHeap H, int k) {
		int[] result = new int[k];
		FibonacciHeap temp = new FibonacciHeap();
		HeapNode current = H.findMin();
		temp.insert(current.getKey());
		int index = 0;

		while (index < k) {
			HeapNode son = temp.findMin();
			current = son;
			result[index] = (temp.findMin()).getKey();
			index++;
			temp.deleteMin();
			if (son != null) {
				do {
					temp.insert(current.getKey());
					current = current.getNext();
				} while (current != son);
			}
		}
		return result;
	}

	// * HEAPNODE CLASS */

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {

		public int key, rank;
		public HeapNode parent, child, next, prev;
		public boolean marked = false;

		// complexity: O(1)
		public HeapNode(int key) {
			this.key = key;
		}

		// complexity: O(1)
		public int getKey() {
			return this.key;
		}

		// complexity: O(1)
		public int getRank() {
			return this.rank;
		}

		// complexity: O(1)
		public HeapNode getParent() {
			return this.parent;
		}

		// complexity: O(1)
		public HeapNode getChild() {
			return this.child;
		}

		// complexity: O(1)
		public HeapNode getNext() {
			return this.next;
		}

		// complexity: O(1)
		public HeapNode getPrev() {
			return this.prev;
		}

		// complexity: O(1)
		public boolean hasChild() {
			return getChild() != null;
		}

		// complexity: O(1)
		public boolean hasSibling() {
			return (this != this.next || this != this.prev);
		}

		// complexity: O(1)
		public boolean hasPrev() {
			return getPrev() != null;
		}

		// complexity: O(1)
		public boolean hasNext() {
			return getNext() != null;
		}
		
		// complexity: O(1)
		public void setNext(HeapNode nextNode) {
			this.next = nextNode;
			nextNode.prev = this;
		}
		
		// complexity: O(1)
		public void setParent(HeapNode node) {
			this.parent = node;
		}
	}
}
