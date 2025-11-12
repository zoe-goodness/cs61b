# Lec3 Testing

1.

str1.compareTo(str2) 方法会返回：

负数：str1 < str2

0:两者相等

正数: str1 > str2

# Lec5 SLLists, Nested Classes, Sentinel Nodes

1.

如果嵌套类不需要访问外部类 `SLList` 的实例方法或变量，你可以将嵌套类声明为 `static`

```java
public class SLList {
       public static class IntNode {
            public int item;
            public IntNode next;
            public IntNode(int i, IntNode n) {
                item = i;
                next = n;
            }
       }

       private IntNode first;
...

```

这样可以节省一点内存，因为每个 `IntNode` 不再需要保存访问其外部 `SLList` 的引用。换句话说，如果嵌套类从不使用外部类的实例成员，就可以用 `static`，节省内存。

# Lec6 DLLists, Arrays

1.

The basic idea is that right after the name of the class in your class declaration, you use an arbitrary placeholder inside angle brackets: `<>`.

```java
public class DLList {
    private IntNode sentinel;
    private int size;

    public class IntNode {
        public IntNode prev;
        public int item;
        public IntNode next;
        ...
    }
    ...
}
```

to

```java
public class DLList<BleepBlorp> {
    private IntNode sentinel;
    private int size;

    public class IntNode {
        public IntNode prev;
        public BleepBlorp item;
        public IntNode next;
        ...
    }
    ...
}
```

Since generics only work with reference types, we cannot put primitives like `int` or `double` inside of angle brackets, e.g. `<int>`. Instead, we use the reference version of the primitive type, which in the case of `int` case is `Integer`, e.g.

```java
DLList<Integer> d1 = new DLList<>(5);
d1.insertFront(10);
```

# Lec7 ALists, Resizing vs SLists

1.

Java does not allow us to create an array of generic objects due to an obscure issue with the way generics are implemented. That is, we cannot do something like:

```java
Glorp[] items = new Glorp[8];
```

Instead, we have to use the awkward syntax shown below:

```java
Glorp[] items = (Glorp []) new Object[8];
```

# Lec8 Inheritance, Implements

1.

```java
public interface List61B<Item> {
    public void addFirst(Item x);
    public void add Last(Item y);
    public Item getFirst();
    public Item getLast();
    public Item removeLast();
    public Item get(int i);
    public void insert(Item x, int position);
    public int size();
}
```

We will add to

```java
public class AList<Item> {...}
```

a relationship-defining word: implements.

```java
public class AList<Item> implements List61B<Item>{...}
```

2.

override的方法是根据dynamic method selection来进行操作的，而重载方法不是这样（overload）

Say there are two methods in the same class

```java
public static void peek(List61B<String> list) {
    System.out.println(list.getLast());
}
public static void peek(SLList<String> list) {
    System.out.println(list.getFirst());
}
```

and you run this code

```java
SLList<String> SP = new SLList<String>();
List61B<String> LP = SP;
SP.addLast("elk");
SP.addLast("are");
SP.addLast("cool");
peek(SP);
peek(LP);
```

The first call to peek() will use the second peek method that takes in an SLList. The second call to peek() will use the first peek method which takes in a List61B. This is because the only distinction between two overloaded methods is the types of the parameters. When Java checks to see which method to call, it checks the **static type** and calls the method with the parameter of the same type.

3.

How do we differentiate between "interface inheritance" and "implementation inheritance"? Well, you can use this simple distinction:

- Interface inheritance (what): Simply tells what the subclasses should be able to do.
  - EX) all lists should be able to print themselves, how they do it is up to them.
- Implementation inheritance (how): Tells the subclasses how they should behave.
  - EX) Lists should print themselves exactly this way: by getting each element in order and then printing them.

When you are creating these hierarchies, remember that the relationship between a subclass and a superclass should be an "is-a" relationship. AKA Cat should only implement Animal Cat **is an** Animal. You should not be defining them using a "has-a" relationship. Cat **has-a** Claw, but Cat definitely should not be implementing Claw.（suitable to not only interface inheritance but also implementation inheritance

# Lec9 Extends, Casting, Higher Order Functions

1.

We’d like to build RotatingSLList that can perform any SLList operation as well as: rotateRight(): Moves back item to the front. 

```java
public class RotatingSLList<Blorp> extends SLList<Blorp>{
       public void rotateRight() {
              Blorp oldBack = removeLast();
              addFirst(oldBack);
	}
}
```

Because of extends, RotatingSLList inherits all members of SLList:

- All instance and static variables.
- All methods.
- All nested classes.
- Constructors are **not** inherited!

# Lec10 Subtype Polymorphism vs HoFs

## Comparables

```java
public interface Comparable<T> {
    public int compareTo(T obj);
}
```

Notice that `Comparable<T>` means that it takes a generic type. This will help us avoid having to cast an object to a specific type! Now, we will rewrite the Dog class to implement the Comparable interface, being sure to update the generic type `T` to Dog:

```java
public class Dog implements Comparable<Dog> {
    ...
    public int compareTo(Dog uddaDog) {
        return this.size - uddaDog.size;
    }
}
```

## Comparators

1.

Natural order - used to refer to the ordering implied in the `compareTo` method of a particular class.

2.

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

```java
import java.util.Comparator;

public class Dog implements Comparable<Dog> {
    ...
    public int compareTo(Dog uddaDog) {
        return this.size - uddaDog.size;
    }

    private static class NameComparator implements Comparator<Dog> {
        public int compare(Dog a, Dog b) {
            return a.name.compareTo(b.name);
        }
    }

    public static Comparator<Dog> getNameComparator() {
        return new NameComparator();
    }
}
```

什么时候用Comparable:只有一个要比较的时候

什么时候用Comparator：有多个需要比较的时候

3.

在中文中，Comparable通常翻译成可比较接口/可比较性接口，Comparator通常翻译成比较器接口

# Lec11 Exceptions, Iterators, Object Methods

## Iteration

1.

We check that there are still items left with `seer.hasNext()`, which will return true if there are unseen items remaining, and false if all items have been processed.

Last, `seer.next()` does two things at once. It returns the next element of the list, and here we print it out. It also advances the iterator by one item. In this way, the iterator will only inspect each item once. 

2.

在中文中，Iterator通常翻译成迭代器，Iterable通常翻译成可迭代对象/可迭代容器

3.

```java
public interface Iterable<T> {
    Iterator<T> iterator();
}
```

```java
public interface Iterator<T> {
    boolean hasNext();
    T next();
}
```

```java
import java.util.Iterator;

public class ArraySet<T> implements Iterable<T> {
    private T[] items;
    private int size; // the next item to be added will be at position size

    public ArraySet() {
        items = (T[]) new Object[100];
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key.
     */
    public boolean contains(T x) {
        for (int i = 0; i < size; i += 1) {
            if (items[i].equals(x)) {
                return true;
            }
        }
        return false;
    }

    /* Associates the specified value with the specified key in this map.
       Throws an IllegalArgumentException if the key is null. */
    public void add(T x) {
        if (x == null) {
            throw new IllegalArgumentException("can't add null");
        }
        if (contains(x)) {
            return;
        }
        items[size] = x;
        size += 1;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /** returns an iterator (a.k.a. seer) into ME */
    public Iterator<T> iterator() {
        return new ArraySetIterator();
    }

    private class ArraySetIterator implements Iterator<T> {
        private int wizPos;

        public ArraySetIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = items[wizPos];
            wizPos += 1;
            return returnItem;
        }
    }

    public static void main(String[] args) {
        ArraySet<Integer> aset = new ArraySet<>();
        aset.add(5);
        aset.add(23);
        aset.add(42);

        //iteration
        for (int i : aset) {
            System.out.println(i);
        }
    }

}
```

# Lec14 Disjoint Sets

## Introduction

1.

Disjoint-Sets也叫Union-Find

2.

```java
public interface DisjointSets {
    /** connects two items P and Q */
    void connect(int p, int q);

    /** checks to see if two items are connected */
    boolean isConnected(int p, int q); 

```

## Quick Find

### List of Sets

![image-20250906092114436](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092114436.png)

### Quick Find

1.

```java
public class QuickFindDS implements DisjointSets {

    private int[] id;

    /* Θ(N) */
    public QuickFindDS(int N){
        id = new int[N];
        for (int i = 0; i < N; i++){
            id[i] = i;
        }
    }

    /* need to iterate through the array => Θ(N) */
    public void connect(int p, int q){
        int pid = id[p];
        int qid = id[q];
        for (int i = 0; i < id.length; i++){
            if (id[i] == pid){
                id[i] = qid;
            }
        }
    }

    /* Θ(1) */
    public boolean isConnected(int p, int q){
        return (id[p] == id[q]);
    }
}
```

2.

![image-20250906092208413](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092208413.png)

## Quick Union

1.

We will still represent our sets with an array. Instead of an id, we assign each item the index of its parent. If an item has no parent, then it is a 'root' and we assign it a negative value.

![image-20250906092355142](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092355142.png)

2.

![image-20250906092444262](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092444262.png)

```java
public class QuickUnionDS implements DisjointSets {
    private int[] parent;

    public QuickUnionDS(int num) {
        parent = new int[num];
        for (int i = 0; i < num; i++) {
            parent[i] = -1;
        }
    }

    private int find(int p) {
        while (parent[p] >= 0) {
            p = parent[p];
        }
        return p;
    }

    @Override
    public void connect(int p, int q) {
        int i = find(p);
        int j= find(q);
        parent[i] = j;
    }

    @Override
    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }
}
```

## Weighted Quick Union

1.

**New rule:** whenever we call `connect`, we always link the root of the smaller tree to the larger tree.

2.

根据weight来，小的weight排到大的weight上面，root的值是 负号（wight）

3.

![image-20250906092732368](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092732368.png)

## Weighted Quick Union with Path Compression

1.

Connecting all the items along the way to the root will help make our tree shorter with each call to `find`.

Recall that **both** `**connect(x, y)**` **and** `**isConnected(x, y)**` **always call** `**find(x)**` **and** `**find(y)**`**.** Thus, after calling `connect` or `isConnected` enough, essentially all elements will point directly to their root.

2.

![image-20250906092842806](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250906092842806.png)

# Lec16 ADTs， Sets, Maps, BSTs

## Sets

**Sets**: an unordered set of unique elements (no repeats)

## BST Delete

three categories:

- the node we are trying to delete has no children
- has 1 child
- has 2 children

### Deletion: No Children

If the node has no children, it is a leaf, and we can just delete its parent pointer and the node will eventually be swept away by the [garbage collector](https://stackoverflow.com/questions/3798424/what-is-the-garbage-collector-in-java).

### Deletion: One Child

If the node only has one child, we know that the child maintains the BST property with the parent of the node because the property is recursive to the right and left subtrees. Therefore, we can just reassign the parent's child pointer to the node's child and the node will eventually be garbage collected.

### Deletion: Two Children

![image-20250910151048617](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910151048617.png)

To find these nodes, you can just take the right-most node in the left subtree or the left-most node in the right subtree. Then, we replace the `dog` node with either `cat` or `elf` and then remove the old `cat` or `elf` node.

This is called **Hibbard deletion**, and it gloriously maintains the BST property amidst a deletion.

## BSTs as Sets and Maps

We can use a BST to implement the `Set` ADT. If we use a BST, we can decrease the runtime of `contains` to log⁡(n) because of the BST property which enables us to use binary search!

We can also make a binary tree into a map by having each BST node hold `(key,value)` pairs instead of singular values. We will compare each element's key in order to determine where to place it within our tree.

# Lec17 B-Trees(2-3，2-3-4 Trees)

## B-Trees add

Upon `add` in a B-Tree, we simply append the value to an existing leaf node in the correct location instead of creating a new leaf node. If the node is too full, it splits and pushes a value up.

## B-Trees Invariants

Because of the way B-Trees are constructed, they have two invariants:

1. All leaves are the same distance from the root.
2. A non-leaf node with k items must have exactly k + 1 children.

These two invariants guarantee a "bushy" tree with log⁡N height.

## Runtime for contains and add

他们都是O(logN)

# Lec18 Red Black Trees

Wonderfully balanced as they are, B-Trees are really difficult to implement. We need to keep track of the different nodes and the splitting process is pretty complicated. As computer scientists who appreciate clean code and a good challenge, let's find another way to create a balanced tree.

## Tree rotation

rotateLeft(G): Let x be the right child of G. Make G the new left child of x.

rotateRight(G): Let x be the left child of G. Make G the new right child of x.

![image-20250910161033630](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161033630.png)

Here are the implementations of `rotateRight` and `rotateLeft:`

```java
private Node rotateRight(Node h) {
    Node x = h.left;
    h.left = x.right;
    x.right = h;
    return x;
}

// make a right-leaning link lean to the left
private Node rotateLeft(Node h) {
    Node x = h.right;
    h.right = x.left;
    x.left = h;
    return x;
}
```

## Creating LLRB Trees

![image-20250910161500045](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161500045.png)

![image-20250910161529924](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161529924.png)

## Inserting LLRB Trees

![image-20250910161757214](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161757214.png)

![image-20250910161901884](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161901884.png)

![image-20250910161916932](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250910161916932.png)

## Summary

LLRBs maintain correspondence with 2-3 trees, Standard Red-Black trees maintain correspondence with 2-3-4 trees.

- Java’s [TreeMap](https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/999dbd4192d0f819cb5224f26e9e7fa75ca6f289/src/java.base/share/classes/java/util/TreeMap.java) is a red-black tree that corresponds to 2-3-4 trees.
- 2-3-4 trees allow glue links on either side (see [Red-Black Tree](http://en.wikipedia.org/wiki/Red–black_tree)).
- More complex implementation, but faster.

# Lec19 Hashing

1.

合法 hashCode 的确定性定义：

 如果两个对象相等（`equals` 返回 true），它们必须有相同的 `hashCode`，这样哈希表才能正确找到它们。

2.

Bottom line: If your class override equals, you should also override hashCode in a consistent manner.

- If two objects are equal, they must always have the same hash code.

If you don’t, everything breaks:

- `Contains` can’t find objects (unless it gets lucky).
- `Add` results in duplicates.

# Lec20 Heaps and PQs

## Priority Queues

1.

```java
/** (Min) Priority Queue: Allowing tracking and removal of 
  * the smallest item in a priority queue. */
public interface MinPQ<Item> {
    /** Adds the item to the priority queue. */
    public void add(Item x);
    /** Returns the smallest item in the priority queue. */
    public Item getSmallest();
    /** Removes the smallest item from the priority queue. */
    public Item removeSmallest();
    /** Returns the size of the priority queue. */
    public int size();
}
```

Priority Queue is an Abstract Data Type that optimizes for handling minimum or maximum elements.

## Heaps

1.

We will define our binary min-heap as being **complete** and obeying **min-heap** property:

- Min-heap: Every node is less than or equal to both of its children
- Complete: Missing items only at the bottom level (if any), all nodes are as far left as possible.

![image-20250926154834756](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250926154834756.png)

2.

heap是priority queue这个adt的一个implementation

- `add`: Add to the end of heap temporarily. Swim up the hierarchy to the proper place.
  - Swimming involves swapping nodes if child < parent
- `getSmallest`: Return the root of the heap (This is guaranteed to be the minimum by our *min-heap* property
- `removeSmallest`: Swap the last item in the heap into the root. Sink down the hierarchy to the proper place.
  - Sinking involves swapping nodes if parent > child. Swap with the smallest child to preserve *min-heap* property.

3.

最大堆（max heap）或最小堆（min heap）是一个以数组表示的二叉树，堆是完全二叉树

# Lec21 Tree and Graph Traversals

1.simple graph:不允许self-loop，并且两个结点之间最多只有一条path

multigraph graph:两个结点之间有两条path，可以有self-loop,也可以没有self-loop

![image-20250926162302577](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250926162302577.png)

这个就是self-loop（即指向自身的）

# Lec22 Graph Traversals and Implementations

1.

BFS：

```java
Initialize the fringe, an empty queue 
    add the starting vertex to the fringe
    mark the starting vertex
    while fringe is not empty:
        remove vertex v from the fringe
        for each neighbor n of vertex v:
            if n is not marked:
                add n to fringe
                mark n
                set edgeTo[n] = v
                set distTo[n] = distTo[v] + 1
```

![image-20250926163126943](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250926163126943.png)

BFS的fringe是一个队列

2.

DFS

```java
Initialize the fringe, an empty stack
    push the starting vertex on the fringe
    while fringe is not empty:
        pop a vertex off the fringe
        if vertex is not marked:
            mark the vertex
            visit vertex
            for each neighbor of vertex:
                if neighbor not marked:
                    push neighbor to fringe
```

DFS的fringe是一个stack

3.

需要注意的是，**DFS 和 BFS 不仅仅是在 fringe（前沿节点结构）上不同**，它们在标记节点的顺序上也不同。

- 对于 **DFS**，我们是在访问节点时才标记它——也就是说，只有当节点从 fringe 中弹出时才会标记它。因此，如果某个节点已经被加入栈但尚未被访问，它可能会出现在栈中多个位置。
- 对于 **BFS**，我们一旦将节点加入 fringe 就立即标记它，这样就不会出现同一个节点在队列中重复出现的情况。

标记顺序很重要，下面给一个例子

![image-20250930191300959](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250930191300959.png)

![image-20250930191307017](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250930191307017.png)

4.

对于我们的图（Graph）API，我们使用一个常见的约定：将每个唯一节点分配一个整数编号。这可以通过维护一个映射（map）来实现，该映射能够告诉我们每个原始节点标签对应的整数编号。这样，我们就可以将 API 定义为专门处理整数，而不需要引入泛型

5.

DFS/BFS on a graph backed by adjacency lists runs in O(V+E), while on a graph backed by an adjacency matrix runs in O(V^2)

6.

**DFS 前序遍历（DFS preorder）**：按照 DFS 调用每个顶点的顺序。

**DFS 后序遍历（DFS postorder）**：按照 DFS 从每个顶点返回的顺序。

# Lec23 Shortest Paths

## Dijkstra‘s Algorithm

1.

**Dijkstra 算法** 接收一个输入顶点 **s**，并输出从 **s** 出发的最短路径树。它是如何工作的？

1. 创建一个 **优先队列**（priority queue）。
2. 将 **s** 加入优先队列，并设置优先级为 **0**；将所有其他顶点加入优先队列，并设置优先级为 **∞**（无穷大）。
3. 当优先队列不为空时：
   - 从优先队列中弹出一个顶点。
   - 放松（relax）从该顶点出发的所有边。

2.

Dijkstra's Algorithm适用于所有边都是non-negative

3.

Dijkstra's Algorithm 的伪代码

```java
def dijkstras(source):
    PQ.add(source, 0)
    For all other vertices, v, PQ.add(v, infinity)
    while PQ is not empty:
        p = PQ.removeSmallest()
        relax(all edges from p)
```

```java
def relax(edge p,q):
   if q is visited (i.e., q is not in PQ):
       return

   if distTo[p] + weight(edge) < distTo[q]:
       distTo[q] = distTo[p] + w
       edgeTo[q] = p
       PQ.changePriority(q, distTo[q])
```

## A* Algorithm

1.

源点到该节点的真实距离 + 从该节点到目标节点的估计距离

2.

heuristics need to be good. There are two definitions required for goodness.

1. Admissibility. heuristic(v, target) ≤trueDistance(v, target). (Think about the problem above. The true distance from the neighbor of C to C wasn't infinity, it was much, much smaller. But our heuristic said it was ∞, so we broke this rule.)
2. Consistency. For each neighbor vof w:
   - heuristic(v, target) ≤ dist(v, w) + heuristic(w, target)
   - where dist(v, w) is the weight of the edge from v to w.

## Summary

![image-20250926172556572](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250926172556572.png)

# Lec24 Minimum Spanning Trees

## Prim‘s Algorithm

1.

Prim 算法是一种从图中找到最小生成树（MST）的方法，其步骤如下：

1. 从任意一个节点开始。
2. 重复以下步骤：加入一条**最短的边**，这条边必须有一个节点在当前构建中的 MST 内，另一个节点在 MST 外。
3. 重复直到 MST 中有 V - 1条边为止（其中 V是顶点的数量）。

2.

它的运行方法和dijkstra是差不多的，可以直接看demo，唯一的区别在于，加入候选节点到fringe（priority queue）时，不是基于它们到目标顶点的距离，而是基于它们到当前构建的 MST 的距离。所以它的时间复杂度也是add是O(VlogV),removeSmallest(VlogV),changePriority(ElogV),所以最后的时间复杂度是O(ElogV)

## Kruskal's Algorithm

1.

这个算法的步骤如下：

1. 将图中所有的边按权重从小到大排序。
2. 按排序好的顺序一次取出边，将其加入当前构建的最小生成树（MST），**前提是加入它不会形成环**。
3. 重复此过程，直到 MST 中有 V - 1条边（其中 V是顶点的数量）。

2.

需要注意的是，Kruskal 算法得到的最小生成树（MST）可能**不**会与 Prim 算法得到的完全相同，但**两种算法都会返回一个 MST**。

由于它们得到的都是最小（最优）的生成树，因此它们都会给出有效且最优的结果（它们的总权重是相同的）。

3.

运行方法直接看demo

4.

![image-20250926174540119](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250926174540119.png)

如果没有pre-sorted list of edges，则是用priority queue来维持边的权重的大小关系的，因此时间复杂度是O(ElogE)+O(ElogE)+O(Vlog*V) + O(Elog星V)，所以时间复杂度是O(ElogE)



# Lec25 Range Searching and Multi-Dimensional Data

## QuadTree(四叉树)

一个节点底下分为四个，西北，东北，东南，西南

适用于二维空间，因为在二维空间中有四个象限

## K-D Tree

将层次化划分的思想推广到二维以上的一种方法是使用 **K-D 树（K-D Tree）**。它的做法是**逐层轮流使用不同的维度来划分**。

在二维情况下，它在第一层像基于 X 的树那样划分；第二层像基于 Y 的树那样划分；第三层再回到基于 X 的树；第四层再是基于 Y 的树，以此类推。

在三维情况下，划分会在三个维度之间每三层循环一次；更高维度的情况也依此类推。K-D 树的一个优势就在于它能够更容易推广到高维空间。不过，无论维度多高，K-D 树始终是一棵**二叉树**，因为在每一层，空间都只会被划分为 “大于” 和 “小于” 两个部分。

### Nearest Neighbor using a K-D Tree

为了找到与查询点最近的点（最近邻），我们在 K-D 树中按照下面的步骤进行：

1. 从根节点开始，把该点记为“**目前为止的最佳点**”。计算它到查询点的距离，并将该距离记为“**要打破的分数**（score to beat）”。在上图中，我们从 A 开始，A 到被标记点的距离是 4.5。
2. 该节点将周围空间划分为两个子空间。对于每个子空间，问自己：“这个子空间里是否可能存在一个更接近查询点的点？”可以通过计算查询点到该子空间边界的**最短距离**来回答这个问题（见下图的紫色虚线）。
3. 对于每个被判定为可能包含更好点的子空间，递归地继续上述过程。
4. 最终，记录下来的“目前为止的最佳点”就是最近邻——即距离查询点最近的点。

# Lec26 Prefix Operations and Tries

## Character Keyed Map

```java
public class DataIndexedCharMap<V> {
    private V[] items;
    public DataIndexedCharMap(int R) {
        items = (V[]) new Object[R];
    }
    public void put(char c, V val) {
        items[c] = val;
    }
    public V get(char c) {
        return items[c];
    }
}
```

## Trie

1.

通过它在父节点的 **DataIndexedCharMap** 中的位置来确定字符值(即ch实例变量)

```java
public class TrieSet {
   private static final int R = 128; // ASCII
   private Node root;    // Trie 的根节点

   private static class Node {
      // 移除了 'ch' 实例变量
      private boolean isKey;   
      private DataIndexedCharMap next;

      private Node(boolean blue, int R) {
         isKey = blue;
         next = new DataIndexedCharMap<Node>(R);
      }
   }
}

```

To address the issue of wasted space, let us explore two possible solutions:

- *Alternate Idea #1*: Hash-Table based Trie. This won't create an array of 128 spots but instead initialize the default value and resize the array only when necessary with the load factor.
- *Alternate Idea #2*: BST based Trie. Again this will only create children pointers when necessary, and we will store the children in the BST. We will have to worry about the runtime for searching in this BST, but this is not a bad approach.

![image-20250927134654876](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250927134654876.png)

2.

Trie是一种具体的数据结构的实现，它可以用来实现map或set

3.

Know how to insert and search for an item in a Trie. Know that Trie nodes typically do not contain letters, and that instead letters are stored implicitly on edge links. Know that there are many ways of storing these links, and that the fastest but most memory hungry way is with an array of size R. We call such tries R-way tries.

# Lec28 Reduction and Decomposition

## Topological Sorts and DAGs

1.

**Topological Sort:** an ordering of a graph's vertices such that for every directed edge ***u***→***v***, ***u*** comes before ***v*** in the ordering.

2.

topological sorts only apply to **directed, acyclic (no cycles) graphs** - or **DAG**s.

3.

**Topological Sort:** an ordering of a **DAG**'s vertices such that for every directed edge ***u***→***v***, ***u*** comes before ***v*** in the ordering.

4.

![image-20250927141549938](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250927141549938.png)

5.

Topological Sort Algorithm:

- Perform a DFS traversal from every vertex in the graph
- Record DFS postorder along the way.
- Topological ordering is the reverse of the postorder.

所以时间复杂度是O(V+E),其中V是图中顶点的数量，E是边的数量

```java
topological(DAG):
    initialize marked array
    initialize postOrder list
    for all vertices in DAG:
        if vertex is not marked:
            dfs(vertex, marked, postOrder)
    return postOrder reversed

dfs(vertex, marked, postOrder):
    marked[vertex] = true
    for neighbor of vertex:
        dfs(neighbor, marked, postOrder)
    postOrder.add(vertex)
```

上述这个代码用的dfs其实是用了stack的，只不过我们之前是用的遍历，这次用的递归

BFS也可以做（但超出了课程的要求范围）

We can find a topological sort of any DAG in **O(V+E)** time using **DFS** (or **BFS**).

## Shortest Path on DAGs

1.这个方法不像dijkstra算法一样，它可以处理negative edges，而dijkstra只能处理不是negative edges的图

2.

![image-20250927143902845](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250927143902845.png)

所以shortest path algorithm for dags的时间复杂度是O(V+E)

## Longest Paths on DAGS

第一种方法：

1. 
   Form a new copy of the graph, called G', with all edge weights negated (signs flipped).
2. Run DAG shortest paths on G' yielding result X
3. Flip the signs of all values in X.distTo. X.edgeTo is already correct.

第二种方法

we could modify the DAG shortest path algorithm from the previous section to choose the larger distTo when relaxing an edge.

## Reductions and Decomposition

1.

**if any subroutine for task Q can be used to solve P, we say P reduces to Q.**

例如上面的Longest Paths on DAGs，Since DAG-SPT can be used to solve DAG-LPT, we say that "DAG-LPT reduces to DAG-SPT."

# Lec29 Basic Sorts

## Selection Sort & Heapsort

### Naive Heapsort

1.

to heapsort N items, we can insert all the items into a max heap and create and output array. Then, we repeatedly delete the largest item from the max heap and put the largest item at the end part of the output array.

2.

The overall runtime of this algorithm is Θ(Nlog⁡N). There are three main components to this runtime:

- Inserting N items into the heap: O(Nlog⁡N).
- Selecting the largest item: Θ(1)
- Removing the largest item: O(log⁡N)

### In-place Heapsort

1.

As an alternate approach, we can use the input array itself to form the heap and output array.

Rather than inserting into a new array that represents our heap, we can use a process known as *bottom-up heapification* to convert the input array into a heap. Bottom-up heapification involves moving in reverse level order up the heap, sinking nodes to their appropriate location as you move up.

By using this approach, we avoid the need for an extra copy of the data. Once heapified, we use the naive heapsort approach of popping off the maximum and placing it at the end of our array. In doing so, we maintain an "unsorted" front portion of the array (representing the heap) and a "sorted" back portion of the the array (representing the sorted items so far).

可以看demo

2.

This process overall is still O(Nlog⁡N), since bottom-up heapification requires at most N sink-down operations that take at most log⁡N time each.

*Note: it is possible to prove that bottom-up heapficiation is bounded by* Θ(N）. *However, this proof is out of scope for this class.*

## Mergesort

1. 
   Split the items into half.
2. Mergesort each half.
3. Merge the two sorted halves to form the final result.

Mergesort has a runtime of Θ(Nlog⁡N)

The auxiliary array used during the merge step requires Θ(N)extra space. Note that in-place mergesort is possible; however it is very complex and the runtime suffers by a significant constant factor, so we will not cover it here.

可以看demo

## Insertion Sort

### Naive Insertion Sort

![image-20250929193742138](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929193742138.png)

### In-place Insertion Sort

![image-20250929194032094](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929194032094.png)

### Insertion Sort Runtime

![image-20250929194205962](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929194205962.png)

Insertion Sort 每一次交换都会修复恰好一个逆序对

# Lec30 Quick Sort

1.

**Quicksort Algorithm** 

To quicksort N items: 

1. Partition on the leftmost item as the pivot. 
2. Recursively quicksort the left half. 
3. Recursively quicksort the right half. 

可以看demo

2.

巧合的是，在大多数常见情况下，快速排序在实测中确实是最快的排序算法。

![image-20250929195349116](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929195349116.png)

In comparison, Mergesort seems to be a lot better, since Quicksort suffers from a theoretical worst case runtime of Θ(N^2). So how can Quicksort be the fastest sort empirically? Quicksort's advantage empirically comes from the fact that on average, Quicksort is Θ(NlogN)

![image-20250929195832332](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929195832332.png)



![image-20250929200423661](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20250929200423661.png)

# Lec32 More Quick Sort, Sorting Summary

## Quicksort Flavors vs. MergeSort

### Quicksort Flavors： Philosophies 

#### Philosophy 1 : Randomness

the first method is pick pivots randomly.

the second method is shuffle items before sort.

#### Philosophy 2 : Smarter Pivot Selection

To the problem is to choose a "good" pivot effectively

the first method is constant time pivot pick:

One of such approach is to pick a few items, and then among them, choose the "best" one as the pivot. This type of approach exemplifies a type of pivot selection procedure that is both *deterministic* and *constant* time.

However, a big drawback of such procedure is that the resulting Quicksort has a family of "dangerous inputs"---inputs that will lead to the worst case runtime or break the algorithm---that an adversary could easily generate.

the second method is Linear time pivot pick

Since we are partioning multiple times, always selecting the median as the pivot seems like a good idea, because each partition will allow us to cut the size of the resulting subarrays by half. 

Therefore, to improve upon the idea from the first method, we can calculate the *median* of a given array, which can be done in linear time, then select that as the pivot.

The "exact median QuickSort" algorithm will not have the technical vulnerabilities as the first method approach, and has a runtime of θ(NlogN).(这个exact median Quicksort可以用我们下面提到的Quick Select) However, it is still slower than MergeSort.

#### Philosophy 3: Introspection

This philosophy simply relies on introspecting the recursive depth of our algorithm, and switch to MergeSort once we hit the depth threshold. 

Although this is a reasonable approach, it is not common to use in practice.

### Quicksort Falvors vs. MergeSort: Who's the best

#### Candidate One: Quicksort L3S

Quicksort L3S is the Quicksort that is introduced last time, with the following properties:

- Always leftmost pivot selection
- Partioning
- Shuffle before starting

![image-20251013151148072](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013151148072.png)

![image-20251013174217922](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013174217922.png)

#### Candidate Two:Quicksort LTHS

Quicksort LTHS is very similar to Quicksort L3S, except with a different partioning scheme: [**Tony Hoare's In-Place Partioning Scheme**](https://docs.google.com/presentation/d/1DOnWS59PJOa-LaBfttPRseIpwLGefZkn450TMSSUiQY/pub?start=false&loop=false&delayms=3000&slide=id.g12b16fb6b6_0_2).

##### Tony Hoare's Partioning

Imagine two pointers, one at each end of the items array, walking towards each other:

- Left pointer loves small items.
- Right pointer loves large items.
- Big idea: Walk towards each other, swapping anything they don’t like; stop when the two pointers cross each other. 
  - Left pointer hates larger or equal items
  - Right pointer hates smaller or equal items
- New pivot = Item at right pointer.
- End result is that things on left of pivot (originally the leftmost item) are “small” and things on the right of pivot are “large”.

可以看demo

It's important to also note that: 

- Faster schemes have been found since.
- Overall runtime still depends crucially on pivot selection strategy!

![image-20251013151822746](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013151822746.png)

#### Candidate Three: QuickSort PickTH

Recall from philosophy 2b that the idea of identifying the median and use it as the pivot is inefficient because finding the median itself is a costly process. (Runtime: θ(NlogN)

Turns out, it is possible to find the median in θ(N) time instead, with an algorithm called "[PICK](https://www.cs.princeton.edu/~wayne/cs423/lectures/selection-4up.pdf)".

Will this improved version of Exact Median Quicksort perform better than Mergesort?

![image-20251013152637928](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013152637928.png)

## 3-scan partitioning one example：

![image-20251025092409346](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251025092409346.png)

![image-20251025092416177](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251025092416177.png)

![image-20251025092425796](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251025092425796.png)

the 3 scan partitioning process is divided into three:the first is less than the pivot, the second is equal to the pivot,the third is greater than the pivot

![image-20251025103418628](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251025103418628.png)

## Quick Select

this algorithm is to identify the median

Goal: find the median using partioning. 

![image-20251013153158441](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013153158441.png)

### Runtime

![image-20251013153337565](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013153337565.png)

![image-20251013153458437](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013153458437.png)

## Stability, Adaptiveness, and Optimization

### Stability

![image-20251013153829485](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013153829485.png)

![image-20251013153840044](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013153840044.png)

Insertion sort is stable!MergeSort is stable. HeapSort is not stable. QuickSort can be stable depending on its partitioning scheme, but its stability cannot be assumed since many of its popular partitioning schemes, like Hoare, are unstable.(即有的Quicksort是stable，有的是不稳定的)

### Optimization

Adaptiveness - A sort that is adaptive exploits the existing order of the array. Examples are InsertionSort, SmoothSort, and TimSort.

Switch to Insertion Sort - When a subproblem reaches size 15 or lower, use insertion sort. It is very very fast for inputs of small sizes.

Exploit restrictions on set of keys - For example, if the number of keys is some constant, we can use this constraint to sort faster by applying 3-way QuickSort.(3-way QuickSort也叫3-scan QuickSort)

Switch from QuickSort - If the recursion goes too deep, switch to a different type of sort.

## Exercise

1.

Suppose we have the array `[17, 15, 19, 32, 2, 26, 41, 17, 17]`, and we partition it using 3-scan partitioning using `17` as the pivot. What array do we end up with?

Ans:

`[15, 2, 17, 17, 17, 19, 32, 26, 41]`. First we scan for the smaller elements (15, 2), then the equal elements (17, 17, 17), and finally the larger elements (19, 32, 26, 41) in the order they appear from left to right in the original array.

2.

![image-20251013175116757](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251013175116757.png)

3.

Why does Java’s built-in `Array.sort` method use Quicksort for `int`, `long`, `char`, or other primitive arrays, but Mergesort for all `Object` arrays?

Ans:

This is because primitives don't require stability--an `int` is indistinguishable from any other `int` if they are equal by `.equals()`. However, this is not true for `Object`s, since two different `Object`s at different memory addresses can still be equal, and stability may be desireable when sorting objects.

# Lec34 Sorting and Algorithmic Bounds

## Sorting Summary

1.

![image-20251018181634561](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018181634561.png)

Why?

- Quicksort isn’t stable, but there’s only one way to order them. Wouldn’t have multiple types of orders.
- Could sort by other things, say the sum of the digits. 
- Order by the number of digits.
- My usual answer: 5 is just 5. There are no different possible 5's.
- When you are using a primitive value, they are the ‘same’. A 4 is a 4. Unstable sort has no observable effect.
- There’s really only one natural order for numbers, so why not just assume that’s the case and sort them that way. 
- By contrast, objects can have many properties, e.g. section and name, so equivalent items CAN be differentiated.

2.

![image-20251018182032016](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182032016.png)

## Math Problems Out of Nowhere

![image-20251018182110088](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182110088.png)

## Theoretical Bounds on Sorting

1.

Let's say we place Tom the Cat, Jerry the Mouse, and Spike the Dog in opaque soundproof boxes labeled A, B, and C. We want to figure out which is which using a scale.

Let's say we knew that Box A weighs less than Box B, and that Box B weighs less than Box C. Could we tell which is which? 

The answer turns out to be yes! 

![image-20251018182414458](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182414458.png)

We can find a sorted order by just considering these two inequalities. What if Box A weighs more than Box B, and that Box B weighs more than Box C? Could we still find out which is which?

![image-20251018182420059](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182420059.png)

The answer turns out to be yes! So far, we have been able to solve this game with just two inequalities! Let's go ahead and try a third case scenario. Could we know which is which if Box A weighs less than Box B, but Box B weighs more than Box C?

![image-20251018182444872](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182444872.png)

The answer turns out to be no. It turns out to have two possible orderings:

- a: mouse, c: cat, b: dog (sorted order: acb)
- c: mouse, a: cat, b: dog (sorted order: cab)

So while we were on a really great streak of solving the game with only two inequalities, we will need a third to solve all possibilities of the game. If we add the inequality a < c then this problem goes away and becomes solvable.

![image-20251018182608233](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182608233.png)

Now that we've created this table, we can create a tree to help us solve this sorting game of cat and mouse (and dog).

![image-20251018182618292](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018182618292.png)

But how can we make this generalizable for all sorting? We know that each leaf in this tree is a unique possible answer to how boxes should be sorted. So the number of ways the boxes can be sorted should turn out to be the number of leaves in the tree. That then begs the question of how many ways can N boxes be sorted? The answer turns out to be N! ways as there are N! permutations of a given set of elements.

So how many questions do we need to ask in order to know how to sort the boxes? We would need to find the number of levels we must go through to get our answer in the leaf.  Since it's a binary tree the minimum amount levels turn out lg(N!) levels to reach a leaf. (Note that lg means (log base 2)).

So, using this game we have found that we must ask at least lg(N!)  questions about inequalities to find a proper way to sort it. Thus our lower bound for R(N) is Ω(lg(N!)). 而lg(N!)与 NlgN grow the same asymptote, so it's  Ω(NlgN)

![image-20251018183229337](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251018183229337.png)

# Lec35 Radix Sorts

## Counting Sort

![image-20251021205712567](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021205712567.png)

可以看Demo

## Counting Sort Runtime

![image-20251021213027440](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213027440.png)

![image-20251021213057451](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213057451.png)

![image-20251021213128393](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213128393.png)

## LSD Radix Sort

![image-20251021213305327](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213305327.png)

![image-20251021213310699](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213310699.png)

![image-20251021213331177](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213331177.png)

即LSD runtime就是W次counting sort

![image-20251021213517050](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213517050.png)

![image-20251021213550437](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213550437.png)

## MSD Radix Sort

![image-20251021213715266](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213715266.png)

![image-20251021213832253](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213832253.png)

![image-20251021213839639](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213839639.png)

![image-20251021213848363](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213848363.png)

![image-20251021213853059](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213853059.png)

![image-20251021213900817](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251021213900817.png)



# Lec36 Sorting and Data Structures Conclusion

## Intuitive ：Radix Sort vs. Comparison Sorting

![image-20251022140739683](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022140739683.png)

![image-20251022140855892](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022140855892.png)

## Just in Time Compiler

![image-20251022141407716](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022141407716.png)

![image-20251022141506715](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022141506715.png)



![image-20251022141518620](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022141518620.png)

![image-20251022141612540](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251022141612540.png)

# Lec38 Compression

## Prefix-free Codes

### Morse Code

As an introductory example, consider the Morse code alphabet. Looking at the alphabet below, what does the sequence – – • – – • represent? It’s ambiguous! The same sequence of symbols can represent either MEME, or GG, depending on what you choose – – • to represent

![image-20251031104215450](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104215450.png)

### Prefix-free Codes

**前缀无歧义码（prefix-free codes）**：在这种编码中，没有任何一个代码词是另一个代码词的前缀。我们可以把摩斯电码表示为一棵从根到叶子的**代码词树**，每个叶子节点对应一个符号。 从这棵树中可以看到，**有些符号的编码是其他符号编码的前缀**。

![image-20251031104411469](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104411469.png)

As an example of an (arbitrary) prefix-free code, consider the following encoding:

![image-20251031104422618](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104422618.png)

The following code is also prefix-free:

![image-20251031104429163](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104429163.png)

## Shannon-Fano Codes

香农-范诺编码（Shannon-Fano codes）是一种**根据符号或字符及其出现概率**来构造**前缀无歧义码（prefix-free codes）\**的方法。
 其核心思想是：\*\*让出现频率较高的字符使用更短的编码\*\*，而\**出现频率较低的字符使用更长的编码**。

算法步骤如下：

1. 统计文本中所有字符的**相对出现频率**；
2. 将字符按频率排序后，**划分为左右两部分**，使两部分的总频率尽量接近；
3. 给左半部分的每个字符编码前加上“0”，右半部分的每个字符编码前加上“1”；
4. 对左右两部分**递归重复**以上过程。

最终，你会得到一棵编码树（如下图所示）：
 在这棵树中，**出现频率高的字符路径更短**，而**频率低的字符路径更长**。

![image-20251031104553281](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104553281.png)

However, Shannon-Fano coding is NOT optimal, so it is not used very often.

## Huffman Coding Conceptuals

1.

哈夫曼编码（Huffman coding）采用了一种**自底向上（bottom-up）**的方法来构造前缀无歧义码（prefix-free codes），这与香农-范诺编码（Shannon-Fano codes）所采用的**自顶向下（top-down）**方法相对。huffman coding 是一种最优的构造prefix-free codes的方法

算法步骤如下：

1. 计算每个符号的**相对频率**；
2. 将每个符号表示为一个**节点**，其**权重（weight）**等于该符号的相对频率；
3. 从所有节点中**取出权重最小的两个节点**，将它们合并成一个新的“超级节点”（super node），其权重等于两者权重之和；
4. **重复**上述过程，直到所有节点都被合并成一棵完整的树。

![image-20251031104700060](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104700060.png)

2. data structures for huffman coding:

![image-20251031104839611](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031104839611.png)

3. 在实际中有两种方法

第一种方法：

对于每种输入类型（英文文本、中文文本、图像等），我们需要收集大量样本输入，用这些样本来构建一个**标准化的编码体系**（例如为英语、中文等分别创建标准编码）。

语料库是指**一组语言片段的集合**，用于作为该语言的代表样本。
 下面是一个例子，说明我们希望使用“ENGLISH”语料库来压缩 `mobydick.txt` 文件：

```
$ java HuffmanEncodePh1 ENGLISH mobydick.txt
```

**问题：**
 这样可能会导致**次优编码（Suboptimal encoding）**，
 也就是说，我们使用的语料库与实际输入并不完全匹配。
 在这个例子中，`mobydick.txt` 的字符频率分布可能与一般的英语文本不同，
 因为它可能具有作者特有的语言风格或其他特点。

第二种方法:

对于每一个可能的输入文件，都为它**单独创建一套唯一的编码**。
 这样，当别人收到这个压缩文件时，就可以使用我们**随文件一起发送的编码表**来进行解码。

如下例所示，我们在压缩时**没有指定语料库（corpus）**，
 而是**附带了一个专门用于该文件的编码信息文件**，以帮助解码过程：

```
$ java HuffmanEncodePh2 mobydick.txt
```

**问题：**
 这种方法需要在压缩的位流（bitstream）中**额外存储编码表（codeword table）**，
 因此会占用一些额外空间。
 不过，相较于使用通用语料库的方法，这种方式**在实际应用中效果更好**，
 因此在现实世界中被**广泛采用**。

实际第二种方法比第一种方法用的更多

4.

![image-20251031111410158](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031111410158.png)

![image-20251031111505814](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031111505814.png)

## Compression Theory

### Compression Ratios(压缩比)

数据压缩的目标是在**尽可能保留信息的前提下**，**减少数据序列的大小**。
 例如，在英文中，字母 **e** 的出现频率比 **z** 高，因此我们希望用**更少的比特**来表示 **e**。

**压缩比**是衡量**压缩后数据大小与原始数据大小差异**的指标。

**哈夫曼编码（Huffman Coding）\**是一种压缩技术，它通过\**为常见符号分配更短的比特序列**来实现更高效的编码。

**游程编码（Run-length encoding, RLE）\**是另一种压缩方法，它会将\**连续重复的字符**替换为“该字符 + 出现次数”。

**LZW 编码（Lempel–Ziv–Welch）\**是一种压缩技术，它会在输入中\**查找常见的重复模式**，并用**较短的代码**替换这些模式。

大多数压缩技术的总体思想都是：
 **利用数据序列中存在的冗余性或规律性来减少数据大小。**
 然而，如果一个序列中**完全没有冗余或规律**，那么压缩就可能**无法实现**。

### Self-Extracting Bits

Self-Extracting Bits是一种压缩技术，它将**压缩后的数据位（compressed bits）\**和\**解压算法（decompression algorithm）即java文件(如果是其他语言就是其他语言的文件）\**以及trie对应的文件一起封装成\**同一个比特序列**。

Self-Extracting Bits可以用来创建**可执行文件（executable files）**，
 这些文件在任何拥有解释器（例如 **Java 解释器**）的系统上都可以**直接运行并自行解压**。

![image-20251031105633517](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031105633517.png)

## Universial Compression: An Impossible Idea

一：原因

1.

不可能设计一个算法，可以对**任意比特流压缩 50%**。

- 否则你可以反复压缩，最终只剩下 1 位，这显然是不可能的。

2.

![image-20251111160106360](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251111160106360.png)

二：

![image-20251031111859976](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031111859976.png)

![image-20251031111905751](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031111905751.png)

![image-20251031110749772](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251031110749772.png)

# Lec39 Compression, Complexity, and P=NP?

## Models of Compression

1.

Recall the `HugPlant` example from the previous chapter. Using Huffman encoding, we can achieve a compression ratio of 25%. 

However, using another algorithm we'll call `MysteryX` for now, we can compress `HugPlant.bmp` down to 29,432 bits! This achieves a 0.35% compression ratio.

![image-20251103193900678](C:\Users\syx92\AppData\Roaming\Typora\typora-user-images\image-20251103193900678.png)

What is `MysteryX`? Well, it's simply the Java code `HugPlant.java` written to generate the `.bmp` file! Going back to the model of self-extracting bits, we see the power of code and interpreters in compression. This leads us to two interesting questions:

- **comprehensible compression:** given a target bitstream `B`, can we create an algorithm that outputs useful, readable Java code?
- **optimal compression**: given a target bitstream `B`, can we find the *shortest* possible program that outputs this bitstream?

