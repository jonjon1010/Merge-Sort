// CS 351 Homework 13 
// Fall 2019 
// Due Date: 12/09/2019
// Name: Jonathan Nguyen
// 
// I got help from Issac the tutuor, the TA Natasha, and Henry in understanding how how to implementing 
// merge correct for the methods we are given. 

package edu.uwm.cs351;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import junit.framework.TestCase;
import snapshot.Snapshot;


/*
 * Allows duplicates and sortedness-equivalent elements
 * These are always placed to the right
 */

public class BasicTree<E> {

	private Node<E> root;
	private Comparator<E> comp;

	//Static node class
	private static class Node<T> {
		T data;
		int treeSize;
		Node<T> left, right;
		Node (T e) { data = e; }
		@Override
		public String toString() {
			return data.toString() + " with " + treeSize + " nodes";
		}
	}

	/**
	 * Construct empty tree-based set
	 */
	public BasicTree(Comparator<E> c) {
		root = null;
		comp = c;
	}
	
	//the sorting methods should not edit any fields, so an invariant is omitted

	/**
	 * Return the size
	 */
	public int size() {
		if(root == null) return 0;
		return root.treeSize;
	}

	/**
	 * Add an element
	 * @param t the element
	 * @return true if something is added
	 */
	public boolean add(E t) {
		if (t == null)
			throw new IllegalArgumentException("null cannot be added");
		return doAdd(root, null, false, t);
	}
	
	private boolean doAdd(Node<E> n, Node<E> par, boolean isRight, E value) {
		if (n == null) {
			n = new Node<E>(value);
			n.treeSize = 1;
			if (par == null)
				root = n;
			else if (isRight)
				par.right = n;
			else
				par.left = n;
			return true;
		} else {
			int c = comp.compare(value, n.data);
			if (c < 0) {
				boolean res = doAdd(n.left, n, false, value);
				if(res)
					n.treeSize++;
				return res;
			}
			else {
				boolean res = doAdd(n.right, n, true, value);
				if(res)
					n.treeSize++;
				return res;
			}
		}
	}

	/**
	 * clear the tree
	 */
	public void clear() {
		root = null;
	}

	/**
	 * Return whether t is in the tree
	 * @param t the element
	 * @return whether t is in the tree
	 */
	public boolean contains(E t) {
		if (t == null)
			return false;
		return doFind(root, t) != null;
	}

	private Node<E> doFind(Node<E> n, E val) {
		if (n == null)
			return null;
		int c = comp.compare(val, n.data);
		if (c == 0) {
			if (n.data.equals(val))
				return n;
			else
				return doFind(n.right, val);
		}
		else if (c < 0)
			return doFind(n.left, val);
		else
			return doFind(n.right, val);
	}

	/**
	 * Return whether the tree is empty
	 * @return true if tree is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public String toString() {
		return "Set of size " + size();
	}
	
	/**
	 * Return an array holding the elements of the tree in sorted order
	 * Use insertion sort
	 * If we wanted stability, it would be easier to insert with an in-order traversal
	 * Instead, we insert according to a preorder traversal as better practice for merge sort
	 * 
	 * @param comp the comparator that defines sortedness
	 * @return the array holding the elements
	 */
	@SuppressWarnings("unchecked")
	public E[] insertionSort(Comparator<E> comp) {
		//TODO: Create the array to hold the elements
		//  and make the initial recursive call to doInsertionSortion
		int size = 0;
		if(root != null) size = root.treeSize;
		E[] array = (E[]) new Object[size];
		doInsertionSort(root,array,0,comp);
		return array;	
	}
	
	private int doInsertionSort(Node<E> n, E[] array, int count, Comparator<E> comp) {
		//TODO: Insert the current element in to the array using insert
		//  Then recursively insert the left and right subtrees
		//  Count keeps track of how many elements are in the array so far
		if(n == null) return 0;
		insert(n.data,array,count++,comp);
		if(n.left != null) count = doInsertionSort(n.left, array,count++,comp);
		if(n.right != null) count = doInsertionSort(n.right,array,count++,comp);	
		return count;
	}
	
	private void insert(E e, E[] arr, int count, Comparator<E> comp) {
		//TODO: insert e into the array in sorted order
		//  Assuming the contents of the array are sorted
		//  and stored from 0 inclusive to count exclusive
		if(e == null) return;
		for(int i = 0; i < arr.length; i++) {
			if(count == 0 || arr[i] == null) {
				arr[i] = e;
				break;
			}
			if(comp.compare(e, arr[i]) < 0) {
				for(int j = count; j > i; j--) {
					arr[j] = arr[j - 1];
				}
				arr[i] = e;
				break;
			}
		}
	}
	
	/**
	 * Return an array holding the elements of the tree in sorted order
	 * Use merge sort
	 * The sort should be stable, meaning that two elements that are sortedness equivalent
	 * should be relatively sorted according to the inorder traversal on the tree
	 * 
	 * @param comp the comparator that defines sortedness
	 * @return the array holding the elements
	 */
	@SuppressWarnings("unchecked")
	public E[] mergeSort(Comparator<E> comp) {
		//TODO: Create the array to hold the elements
		//  and make the initial recursive call to doMergeSort
		int size = 0;
		if(root != null) size = root.treeSize;
		E[] array = (E[]) new Object[size];
		doMergeSort(root,array,0,size,comp);
		return array;
	}
	
	private void doMergeSort(Node<E> n, E[] array, int lower, int upper, Comparator<E> comp) {
		//TODO: Place the element stored in n into the array, in the range given by lower and upper,
		//  leaving room for the left subtree to the left, and the right subtree to the right
		//  Then recursively sort the left subtree and right subtree
		//  Finally merge the (now sorted) left and right subtrees along with the node n, calling merge
		//  Lower and upper are the inclusive lower and exclusive upper bounds on the space
		//  allocated for the subtree rooted at n
		//  This means upper minus lower should be equal to the treeSize of n
		//  Elements should be added to the array in the order of a preorder traversal
		if(n == null) return;
		int index = 0;
		if(n.left == null) index = lower;
		else index = lower + n.left.treeSize;
		//int subIndex = 0;
		//if(n.right == null) subIndex = upper - 1;
		//subIndex = upper - n.right.treeSize - 1;-
		array[index] = n.data;
		doMergeSort(n.left,array,lower,index,comp);
		doMergeSort(n.right,array,index + 1,upper,comp);
		merge(array,lower,index,upper,comp);
	}
	

	@SuppressWarnings("unchecked")
	private void merge(E[] array, int lower, int middle, int upper, Comparator<E> comp) {
		//TODO: Assuming the range [lower, middle) is sorted, as is [middle+1, upper),
		//  repeatedly choose the smallest element from those elements in [lower, upper)
		//  that have not yet been chosen (using smallestIndex) and add it to a queue
		//  (remember there are only a small number of places to look for the smallest element)
		//  Once all elements are added to the queue, overwrite the range [lower, upper)
		//  with the (sorted) contents of the queue
		//  You can use an index to keep track of which elements from [lower, middle)
		//  and [middle+1, upper) have been chosen
		//  You can use a boolean to keep track of whether the element at middle has been chosen
			
			int i1 = lower;
			int j1 = middle + 1;
			int index = 0;
			boolean atMid = false;
			Queue<E> q = new LinkedList<E>();
			while(i1 < middle && j1 < upper) {
				if(!atMid) { 
					index = smallestIndex(comp,array[i1],array[middle],array[j1]);
					if(index == 0) {
						q.add(array[i1]);
						i1++;
						if(i1 == middle) {
							q.add(array[middle]);
							atMid = true;
						}
					}
					else if(index == 1) {
						q.add(array[middle]);
						atMid = true;
					}
					else {
						q.add(array[j1]);
						j1++;
					}
				}
				else {
					index = smallestIndex(comp,array[i1],array[j1]);
					if(index == 0) {
						q.add(array[i1]);
						i1++;
						if(i1 == middle && j1 == upper - 1) {
							q.add(array[j1]);
						}
					}
					else {
						q.add(array[j1]);
						j1++;
						if(i1 + 1 == middle && j1 == upper) {
							q.add(array[i1]);
							break;
						}
					}
				}
			}
			
			int i2 = i1 + 1;
			while(i1 < middle) {
				if(atMid) {
					index = smallestIndex(comp,array[i1],array[i2]);
					if(index == 0) {
						q.add(array[i1]);
						if(i1 + 1 != i2) i1 = i2;
						else i1++;
						if(i2 != middle) i2++;
					}
					else {
						q.add(array[i2]);
						if(i2 != middle) i2++;
					}
					if(i2 == middle) {
						q.add(array[i1]);
						break;
					}	
				}
				else {
					index = smallestIndex(comp,array[i1],array[middle]);
					if(index == 0) {
						q.add(array[i1]);
						i1++;
						if(i1 == middle) q.add(array[i1]);
						i2++;
					}
					else {
						q.add(array[middle]);
						i2 = i1 + 1;
						if(i2 == middle) {
							q.add(array[i1]);
							break;
						}
						atMid = true;
					}
				}
			}
			
			if(!atMid && lower == middle) j1 = i1;
			
			int j2 = j1 + 1;
			while(j1 < upper) {
				if(j2 == upper) break;
				index = smallestIndex(comp,array[j1],array[j2]);
				if(index == 0) {
					q.add(array[j1]);
					if(j1 + 1 != j2) j1 = j2;
					else j1++;
					if(j2 == upper - 1) {
						q.add(array[j2]);
						break;
					}
					if(j2 != upper -1) j2++;
				}
				else {
					q.add(array[j2]);
					if(j2 != upper - 1)j2++;
					else {
						q.add(array[j1]);
						break;
					}
				}	
			}
			
			if(!q.isEmpty()){
				for(int i = lower; i < upper; i++) {
					if(q.isEmpty()) break;
					array[i] = q.remove();
				}
			}
			
		}
	

	@SuppressWarnings("unchecked")
	private int smallestIndex(Comparator<E> comp, E... elements) {
		//TODO: Return the index of the smallest element among elements
		//  if elements consists only of nulls, behavior is undefined
		//  otherwise, return the index of the smallest non-null element in elements
		//  null elements should not be compared
		//  To help enforce stability, we should prefer choosing the earlier element
		//  in case of sortedness equivalence
		E temp = null;
		int index = 0;
		for(int i = 0; i < elements.length; i++) {
			if(elements[i] !=  null && temp == null) {
				temp = elements[i];
				index = i;
			}
			if(elements[i] != null && comp.compare(temp, elements[i]) > 0) {
				temp = elements[i];
				index = i;
			}
		}
		return index;
	}

	
	public static class TestInternals extends TestCase {
		private static final String[] TO_LOG = new String[] {"./src/edu/uwm/cs351/BasicTree.java"};
		private static boolean firstRun = true;	
		
		public void log() {
			System.out.println("running");
			Snapshot.capture(TO_LOG);
		}
		
		private BasicTree<Integer> self;
		Integer i0, i1, i1a, i1b, i2, i3, i4, i5;
		Node<Integer> n0, n1, n1a, n1b, n2, n3, n4, n5;
		Comparator<Integer> ascending = new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {return a-b;}
		};
		Comparator<Integer> descending = new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {return b-a;}
		};
		
		protected void setUp() {
			if(firstRun) {
				log();
				firstRun = false;
			}
			i0 = 0;
			i1 = new Integer(1);
			i1a = new Integer(1);
			i1b = new Integer(1);
			i2 = 2;
			i3 = 3;
			i4 = 4;
			i5 = 5;
			n0 = new Node<Integer>(i0);
			n1 = new Node<Integer>(i1);
			n1a = new Node<Integer>(i1a);
			n1b = new Node<Integer>(i1b);
			n2 = new Node<Integer>(i2);
			n3 = new Node<Integer>(i3);
			n4 = new Node<Integer>(i4);
			n5 = new Node<Integer>(i5);
			
			self = new BasicTree<Integer>(new Comparator<Integer>() {
				public int compare(Integer a, Integer b) {return 0;}
			});
		}
		
		private boolean arrayTest(Integer[] a1, Integer[] a2) {
			if(a1.length != a2.length)
				return false;
			for(int i=0; i<a1.length; i++)
				if(!(a1[i] == a2[i]))
					return false;
			return true;
		}
		
		//test0x: insert
		
		public void test00() {
			Integer[] arr = new Integer[] {i1, i2, i3, null};
			self.insert(i4, arr, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4}));

			arr = new Integer[] {i1, i2, i4, null};
			self.insert(i3, arr, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4}));

			arr = new Integer[] {i1, i3, i4, null};
			self.insert(i2, arr, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4}));

			arr = new Integer[] {i2, i3, i4, null};
			self.insert(i1, arr, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4}));
			
			arr = new Integer[] {i4, i3, i2, null};
			self.insert(i1, arr, 3, descending);
			assertTrue(arrayTest(arr, new Integer[] {i4, i3, i2, i1}));

			arr = new Integer[] {i4, i3, i1, null};
			self.insert(i2, arr, 3, descending);
			assertTrue(arrayTest(arr, new Integer[] {i4, i3, i2, i1}));

			arr = new Integer[] {i4, i2, i1, null};
			self.insert(i3, arr, 3, descending);
			assertTrue(arrayTest(arr, new Integer[] {i4, i3, i2, i1}));

			arr = new Integer[] {i3, i2, i1, null};
			self.insert(i4, arr, 3, descending);
			assertTrue(arrayTest(arr, new Integer[] {i4, i3, i2, i1}));
		}
		
		public void test01() {
			Integer[] arr = new Integer[6];
			self.insert(i3, arr, 0, ascending);
			self.insert(i5, arr, 1, ascending);
			self.insert(i2, arr, 2, ascending);
			self.insert(i4, arr, 3, ascending);
			self.insert(i0, arr, 4, ascending);
			self.insert(i1, arr, 5, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i2, i3, i4, i5}));
		}
		
		//test1x: doInsertionSort
		
		public void test10() {
			Integer[] arr = new Integer[1];
			self.doInsertionSort(null, arr, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null}));
		}
		
		public void test11() {
			Integer[] arr = new Integer[] {i1, i2, i3, null};
			self.doInsertionSort(null, arr, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, null}));
		}
		
		public void test12() {
			Integer[] arr = new Integer[] {null, null, null};
			n2.treeSize = 1;
			self.doInsertionSort(n2, arr, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i2, null, null}));
		}
		
		public void test13() {
			Integer[] arr = new Integer[] {i1, i3, null};
			n2.treeSize = 1;
			self.doInsertionSort(n2, arr, 2, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3}));
		}
		
		public void test14() {
			Integer[] arr = new Integer[] {null, null, null};
			n1.treeSize = 3;
			n1.left = n4;
			n4.treeSize = 2;
			n4.left = n3;
			n3.treeSize = 1;
			self.doInsertionSort(n1, arr, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i3, i4}));
		}
		
		public void test15() {
			Integer[] arr = new Integer[] {i2, i5, null, null, null};
			n1.treeSize = 3;
			n1.left = n4;
			n4.treeSize = 2;
			n4.left = n3;
			n3.treeSize = 1;
			self.doInsertionSort(n1, arr, 2, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4, i5}));
		}
		
		public void test16() {
			Integer[] arr = new Integer[] {null, null, null};
			n5.treeSize = 3;
			n5.right = n1;
			n1.treeSize = 2;
			n1.right = n3;
			n3.treeSize = 1;
			self.doInsertionSort(n5, arr, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i3, i5}));
		}
		
		public void test17() {
			Integer[] arr = new Integer[] {i1, i5, null, null, null};
			n2.treeSize = 3;
			n2.right = n4;
			n4.treeSize = 2;
			n4.right = n3;
			n3.treeSize = 1;
			self.doInsertionSort(n2, arr, 2, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3, i4, i5}));
		}
		
		public void test18() {
			Integer[] arr = new Integer[] {null, null, null, null, null, null};
			n0.treeSize = 6;
			n0.left = n1;
			n1.treeSize = 2;
			n0.right = n2;
			n2.treeSize = 3;
			n1.right = n3;
			n3.treeSize = 1;
			n2.left = n4;
			n4.treeSize = 1;
			n2.right = n5;
			n5.treeSize = 1;
			self.doInsertionSort(n0, arr, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i2, i3, i4, i5}));
		}
		
		public void test19() {
			Integer[] arr = new Integer[] {i2, null, null, null, null, null, null, null};
			n0.treeSize = 5;
			n0.left = n1;
			n1.treeSize = 1;
			n0.right = n3;
			n3.treeSize = 3;
			n3.left = n4;
			n4.treeSize = 2;
			n4.left = n5;
			n5.treeSize = 1;
			self.doInsertionSort(n0, arr, 1, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i2, i3, i4, i5, null, null}));
		}
		
		//test2x: smallestIndex
		
		public void test20() {
			assertEquals(0, self.smallestIndex(ascending, i1, i2, i3));
			assertEquals(2, self.smallestIndex(descending, i1, i2, i3));
			assertEquals(0, self.smallestIndex(ascending, i1, i3, i2));
			assertEquals(1, self.smallestIndex(descending, i1, i3, i2));
			assertEquals(1, self.smallestIndex(ascending, i2, i1, i3));
			assertEquals(2, self.smallestIndex(descending, i2, i1, i3));
			assertEquals(2, self.smallestIndex(ascending, i2, i3, i1));
			assertEquals(1, self.smallestIndex(descending, i2, i3, i1));
			assertEquals(1, self.smallestIndex(ascending, i3, i1, i2));
			assertEquals(0, self.smallestIndex(descending, i3, i1, i2));
			assertEquals(2, self.smallestIndex(ascending, i3, i2, i1));
			assertEquals(0, self.smallestIndex(descending, i3, i2, i1));
		}
		
		public void test21() {
			assertEquals(0, self.smallestIndex(ascending, i1, i1a, i1b));
			assertEquals(0, self.smallestIndex(descending, i1, i1a, i1b));
			assertEquals(1, self.smallestIndex(ascending, i2, i1a, i1b));
			assertEquals(1, self.smallestIndex(descending, i0, i1a, i1b));
		}
		
		public void test22() {
			assertEquals(0, self.smallestIndex(ascending, i1, i2, null));
			assertEquals(1, self.smallestIndex(descending, i1, i2, null));
			assertEquals(1, self.smallestIndex(ascending, null, i2, i3));
			assertEquals(2, self.smallestIndex(descending, null, i2, i3));
			assertEquals(0, self.smallestIndex(ascending, i1, null, i3));
			assertEquals(2, self.smallestIndex(descending, i1, null, i3));
			assertEquals(2, self.smallestIndex(ascending, null, null, i3));
			assertEquals(2, self.smallestIndex(descending, null, null, i3));
			assertEquals(0, self.smallestIndex(ascending, i1, null, null));
			assertEquals(0, self.smallestIndex(descending, i1, null, null));
			assertEquals(1, self.smallestIndex(ascending, null, i2, null));
			assertEquals(1, self.smallestIndex(descending, null, i2, null));
		}
		
		//test3x: merge
		
		public void test30() {
			Integer[] arr = new Integer[] {i1, null, null, null, null};
			//left and right empty, only merging middle element
			self.merge(arr, 0, 0, 1, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, null, null, null, null}));
		}
		
		public void test31() {
			Integer[] arr = new Integer[] {null, null, null, null, i1, null, null, null, null};
			//left and right empty, only merging middle element
			self.merge(arr, 4, 4, 5, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, null, null, null, i1, null, null, null, null}));
		}
		
		public void test32() {
			Integer[] arr = new Integer[] {null, null, null, i4, i1, i2, i0, null, null};
			//left and right empty, only merging middle element
			self.merge(arr, 4, 4, 5, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, null, null, i4, i1, i2, i0, null, null}));
		}
		
		public void test33() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left has one, right is empty, but already sorted
			self.merge(arr, 2, 3, 4, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null}));
		}
		
		public void test34() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left has one, right is empty
			self.merge(arr, 3, 4, 5, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i3, i1, i4, i2, i0, null, null}));
		}
		
		public void test35() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left is empty, right has one
			self.merge(arr, 1, 1, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i3, i5, i4, i1, i2, i0, null, null}));
		}
		
		public void test36() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left has one, right has one
			self.merge(arr, 4, 5, 7, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i3, i4, i0, i1, i2, null, null}));
		}
		
		public void test37() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left has two (sorted), right is empty
			self.merge(arr, 2, 4, 5, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i1, i3, i4, i2, i0, null, null}));
		}
		
		public void test38() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i2, i0, null, null};
			//left is empty, right has two (sorted)
			self.merge(arr, 3, 3, 6, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i3, i1, i2, i4, i0, null, null}));
		}
		
		public void test39() {
			Integer[] arr = new Integer[] {null, i5, i3, i4, i1, i0, i2, null, null};
			//left has two, right has two
			self.merge(arr, 2, 4, 7, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null, i5, i0, i1, i2, i3, i4, null, null}));
		}
		
		//test4x: stability of merge
		
		public void test40() {
			Integer[] arr = new Integer[] {i0, i1, i4, i3, i1a, i2, i5};
			self.merge(arr, 0, 3, 7, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i1a, i2, i3, i4, i5}));
		}
		
		public void test41() {
			Integer[] arr = new Integer[] {i1a, i0, i1, i2, i3, i4, i5};
			self.merge(arr, 0, 0, 7, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1a, i1, i2, i3, i4, i5}));
		}
		
		public void test42() {
			Integer[] arr = new Integer[] {i0, i1b, i5, i1, i2, i3, i4};
			self.merge(arr, 0, 3, 7, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1b, i1, i2, i3, i4, i5}));
		}
		
		public void test43() {
			Integer[] arr = new Integer[] {i0, i1, i5, i1a, i1b, i2, i3, i4};
			self.merge(arr, 0, 3, 8, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i1a, i1b, i2, i3, i4, i5}));
		}
		
		//test5x: doMergeSort
		
		public void test50() {
			Integer[] arr = new Integer[1];
			self.doMergeSort(null, arr, 0, 0, ascending);
			assertTrue(arrayTest(arr, new Integer[] {null}));
		}
		
		public void test51() {
			Integer[] arr = new Integer[] {i1, i2, i3};
			self.doMergeSort(null, arr, 3, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i1, i2, i3}));
		}
		
		public void test52() {
			Integer[] arr = new Integer[] {i3, i0, null, i2, null, null};
			self.doMergeSort(null, arr, 1, 1, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i3, i0, null, i2, null, null}));
		}
		
		public void test53() {
			Integer[] arr = new Integer[] {i3, i0, null, i2, null, null};
			n4.treeSize = 1;
			self.doMergeSort(n4, arr, 2, 3, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i3, i0, i4, i2, null, null}));
		}
		
		public void test54() {
			Integer[] arr = new Integer[] {i3, i0, null, null, i2, null, null};
			n4.treeSize = 2;
			n4.left = n5;
			n5.treeSize = 1;
			self.doMergeSort(n4, arr, 2, 4, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i3, i0, i4, i5, i2, null, null}));
		}
		
		public void test55() {
			Integer[] arr = new Integer[] {null, null, null, null, null, null, i1a};
			n4.treeSize = 6;
			n4.left = n2;
			n2.treeSize = 5;
			n2.left = n0;
			n0.treeSize = 4;
			n0.left = n1;
			n1.treeSize = 3;
			n1.left = n5;
			n5.treeSize = 2;
			n5.left = n3;
			n3.treeSize = 1;
			self.doMergeSort(n4, arr, 0, 6, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i2, i3, i4, i5, i1a}));
		}
		
		public void test56() {
			Integer[] arr = new Integer[] {i1b, null, null, null, null, null, null, i1a};
			n0.treeSize = 6;
			n0.left = n1;
			n1.treeSize = 2;
			n0.right = n2;
			n2.treeSize = 3;
			n1.right = n3;
			n3.treeSize = 1;
			n2.left = n4;
			n4.treeSize = 1;
			n2.right = n5;
			n5.treeSize = 1;
			self.doMergeSort(n0, arr, 1, 7, descending);
			assertTrue(arrayTest(arr, new Integer[] {i1b, i5, i4, i3, i2, i1, i0, i1a}));
		}
		
		public void test57() {
			Integer[] arr = new Integer[] {null, null, null, null, null, null, null, null};
			n0.treeSize = 8;
			n0.left = n1;
			n1.treeSize = 3;
			n1.right = n1a;
			n1a.treeSize = 2;
			n1a.right = n1b;
			n1b.treeSize = 1; //
			n0.right = n3;
			n3.treeSize = 4;
			n3.left = n4;
			n4.treeSize = 3;
			n4.left = n5;
			n5.treeSize = 1;
			n4.right = n2;
			n2.treeSize = 1;
			self.doMergeSort(n0, arr, 0, 8, descending);
			assertTrue(arrayTest(arr, new Integer[] {i5, i4, i3, i2, i1, i1a, i1b, i0}));
			arr = new Integer[] {null, null, null, null, null, null, null, null};
			self.doMergeSort(n0, arr, 0, 8, ascending);
			assertTrue(arrayTest(arr, new Integer[] {i0, i1, i1a, i1b, i2, i3, i4, i5}));
		}
	}
}
