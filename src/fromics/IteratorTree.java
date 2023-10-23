package fromics;

import java.util.function.Consumer;

//honestly this is somewhat useless, but in theory
//it represents a tree which can be iterated of very quickly,
//I'm just not sure it's actually any quicker than using a list
//unless I decide to multi-thread it
public class IteratorTree<E> {
	private Node root;
	private class Node {
		private E data;
		private Node[] children;
		private boolean isRight;
		
		public Node(E data) {
			this.data = data;
			children = null;
			isRight = false;
		}
		
		@SuppressWarnings("unchecked")
		public void add(E data) {
			if(children == null) {
				children = (IteratorTree<E>.Node[]) new Object[2];
				children[0] = new Node(this.data);
				children[1] = new Node(data);
				this.data = null;
			} else {
				if(isRight) {
					children[1].add(data);
				} else {
					children[0].add(data);
				}
				isRight = !isRight;
			}
		}
	}
	
	public IteratorTree() {
		root = null;
	}
	
	public void add(E data) {
		if(root == null) {
			root = new Node(data);
		} else {
			root.add(data);
		}
	}
	
	public void iterate(Consumer<E> func) {
		iterate(func, root);
	}
	
	private void iterate(Consumer<E> func, Node cur) {
		if(cur.children == null) {
			func.accept(cur.data);
		} else {
			Thread left = new Thread(()->{
				iterate(func, cur.children[0]);
			});
			Thread right = new Thread(()->{
				iterate(func, cur.children[1]);
			});
			left.start();
			right.start();
		}
	}
}
