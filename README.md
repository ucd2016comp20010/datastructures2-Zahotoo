Starting repository for `Data Structures` COMP20280 2025-2026

### Wk 2: Linked Lists Exercises
- ***Q5: Are the tests complete?***  
A: All testcases passed.


- ***Q6: What is the difference between a singly linked list and a circularly linked list?***  
A: The Circularly Linked List has no pointer to null at the end
and instead the end points back to the head. Compare to the Singly Linked List,
Circularly Linked List has no traditional beginning which means any node can be a starting node.
We can traverse the whole list by starting from any node and stop when the first visited node is visited again.
However, there has a clear head and an end point in the Singly Linked List.


- ***Q7: In what situations would you prefer to use a linked list to an array?***  
A:
  1) We need constant-time insertions/delations from the list.
  2) We don't know how many items will be in the list.
  3) We don't need random access to any elements.
  4) We want to be able to insert items in the middle of the list.  


- ***Q8: Describe 2 possible use-cases for a circularly linked list.***  
A:
  1) Round-robin scheduling: Circularly linked lists are commonly used in round-robin scheduling, such as CPU process or turn-based games.
  Each task or player is visited in order and after the last node, the list cycles back to the first.
  2) Music playlists: A circularly linked list is ideal for a music playlist. Once the last song is reached, 
  the next pointer automatically returns to the first song.


### Wk 3: Stacks, Queues, Deques
- ***Q2: Write the pseudocode for an algorithm which implements a Queue using two stacks.***
````
    S_in <- new Stack<>()  
    S_out <- new Stack<>()
    
    Algorithm enqueue(e):
        input: an object e
        output: none
        S_in.push(e)
    
    Algorithm dequeue():
        input: none
        output: the front object
        if S_out is empty then:
            while S_in is not empty then:
                S_out.push(S_in.pop())
        end if
        
        if S_out is empty then:
            return null   // empty queue
        else:
            return S_out.pop()
````


- ***Q3: Write the pseudocode algorithm which reverse the elements on a Stack using two additional Stack's.***
````
    Algorithm reverse(S):
        input: a Stack S
        output: none
        S1 <- new Stack<>()
        S2 <- new Stack<>()
        
        // Pop all data from S to S1
        while S is not empty then:
            S1.push(S.pop())
        end while
        
        // Pop all data from S1 to S2
        while S1 is not empty then:
            S2.push(S1.pop())
        end while
        
        // Pop back to S
        while S2 is not empty then:
            S.push(S2.pop())
        end while  
````


### Wk 4: Trees  
- ***Q2: Write a recursive function (pseudo-code) to count the number of external nodes in a binary tree.***
````
    function COUNT-EXTERNAL(p)
        if p is external then
            return 1
        end if
        
        total <- 0
        
        for each child c of p:
            total <- total + COUNT-EXTERNAL(c)
        end for
        
        return total
    end function
````


- ***Q3: Describe an algorithm which counts only the left external nodes in a binary tree.***
````
    function COUNT-LEFT-EXTERNAL(p)
        total <- 0
        
        if p has a left child then
            if p.left() is external then
                total <- total + 1
            else
                total <- total + COUNT-LEFT-EXTERNAL(p.left())
            end if
        end if
        
        if p has a right child then
            total <- total + COUNT-LEFT-EXTERNAL(p.right())
        end if
        
        return total
    end function
````


- ***Q4: Consider a binary tree, where each node holds a single character. The nodes, in no particular order are ['A', 'E', 'F', 'M', 'N', 'U', 'X'].***
  1. Draw a representation of this binary tree such that a **preorder** traversal of the tree gives the result: "EXAMFUN".
  ````
  preorder: V - L - R
  
                E
             /    \
           X        F
         /   \    /  \
        A     M  U    N
  ````  
  2. Draw a representation of this binary tree such that an **inorder** traversal of the tree gives the result: "EXAMFUN".
  ````
  inorder: L - V - R
  
                M
             /    \
           X        U
         /   \    /  \
        E     A  F    N
  ````
  3. Draw a representation of this binary tree such that a **postorder** traversal of the tree gives the result: "EXAMFUN".
  ````
  postorder: L - R - V
  
                N
             /    \
           X        F
         /   \    /  \
        E     A  M    U
  
  ````


- ***Q5: Write the pseudocode for an algorithm which counts the total number of descendants of a node in a binary tree***
````
    function COUNT-DESCENDANTS(p)
        total <- 0
        
        for each child c of p then:
            total <- total + 1
            total <- total + COUNT-DESCENDANTS(c)
        end for
        
        return total
    end function
````


### Wk5: Trees II
- ***Q5: Write the pseudocode for an algorithm which finds the diameter of a binary tree***
````
    function DIAMETER(root)
        maxDiameter <- 0

        call DIAMETER-RECURSIVE(root)

        return maxDiameter
    end function


    function DIAMETER-RECURSIVE(node)
        if node == null then:
            return 0
        end if
        
        leftMost  <- DIAMETER-RECURSIVE(node.left)
        rightMost <- DIAMETER-RECURSIVE(node.right)
        
        diameterThroughNode <- leftMost + rightMost + 1
        
        maxDiameter <- MAX(maxDiameter, diameterThroughNode)
        
        return MAX(leftMost, rightMost) + 1
    end function
````


### Wk6: Recursion
- ***Q1: Draw the recursion trace for ReverseArray(A, 0, len(A)-1) where A={12, 5, 19, 6, 11, 3, 9, 34, 2, 1, 15};***
````
        |  call_1
    ReverseArray(A, 0, 10)    -> swap A[0] = 12, A[10] = 15
        |  call_2
    ReverseArray(A, 1, 9)     -> swap A[1] = 5, A[9] = 1
        |  call_3
    ReverseArray(A, 2, 8)     -> swap A[2] = 19, A[8] = 2
        |  call_4
    ReverseArray(A, 3, 7)     -> swap A[3] = 6, A[7] = 34
        |  call_5
    ReverseArray(A, 4, 6)     -> swap A[4] = 11, A[6] = 9
        |  call_6
    ReverseArray(A, 5, 5)   (i = j, recursion stop)
    
    final result: A = {15, 1, 2, 34, 9, 3, 11, 6, 19, 5, 12}
````


- ***Q2: Write out the recursive trace of the function for the 5th fibonacci number: Fibonacci(5).***
````

````


- ***Q3: Draw the recursion trace for Tribonacci(9)***
````

````


- ***Q4: What kind of recursive function is it?***  
A: Nested recursive function because there is a recursive call M(n+11) inside a recursive call M() when n <= 100.


- ***Q5: a) What does the function Foo do? b) What is the output of Foo(2468)***
A:
  - a) The function Foo() by convert the decimal number to binary by dividing 2 recursively. The base case is when x/2=0 and call Foo(x/2) recursively.
    When achieve the base case x/2 = 0, the process of recursion stops and will backtrack to print the value of x%2 to get the binary number finally.
  - b) 100110100100


- ***Q6: Write the pseudocode for a recursive function which prints the elements of a linked list in reverse***
````
    function PRINT-REVERSE(node)
        if node == null then:
            return
        end if
        
        call PRINT-REVERSE(node.next)
        print node.data
    end function
````


- ***Q7: Write the pseudocode for a fully recursive function which copies a linked list.***
````
    function COPY(node)
        if node == null then:
            return
        end if
        
        newNode <- node.data
        newNode.next = COPY(node.next)
        return newNode
    end function
````


- ***Q8: Draw the recursive trace for mystery(2, 4, 4).***
````
        |  call_1
    mystery(2, 4, 4)    -> return d + a = 4 + 4 = 8
        |  call_2
    mystery(1, 4, 4)    (n == 1  Recursion Stop) -> return a = 4
    
The final mystery result: 8
````


### Wk7: PQ & Heaps
- ***Q1: Illustration the execution of the heap.insert() method on the following input: [2, 5, 16, 4, 10, 23, 39, 18, 26, 15]***
````
         2
      /    \
     4      16
    / \    /  \
   5   10 23   39
  / \  /
18 26 15

insert 2   -> [2]
insert 5   -> [2, 5]
insert 16  -> [2, 5, 16]
insert 4   -> [2, 4, 16, 5]
insert 10  -> [2, 4, 16, 5, 10]
insert 23  -> [2, 4, 16, 5, 10, 23]
insert 39  -> [2, 4, 16, 5, 10, 23, 39]
insert 18  -> [2, 4, 16, 5, 10, 23, 39, 18]
insert 26  -> [2, 4, 16, 5, 10, 23, 39, 18, 26]
insert 15  -> [2, 4, 16, 5, 10, 23, 39, 18, 26, 15]
````


- ***Q2: List the nodes in the preorder traversal of the heap constructed from this array***
````
preorder: root -> left -> right
2, 4, 5, 18, 26, 10, 15, 16, 23, 39
````


- ***Q3: List the nodes in postorder traversal of the heap constructed from this array***
````
postorder: left -> right -> root
18, 26, 5, 15, 10, 4, 23, 39, 16, 2
````


- ***Q4: Can you construct a valid heap where a preorder traversal of the keys does not list them descending order?***
````
For example if you have a max-heap:
        20
      /    \
    15      18
   /  \    /  \
 14   13  17  16

  if you do the preorder traversal to this heap, the order goes to:
     20 -> 15 -> 14 -> 13 -> 18 -> 17 -> 16
     
     18 > 17: therefore, preorder traversal of the keys does not list them descending order
````


- ***Can you construct a valid heap where a postorder traversal of the keys does not list them ascending order?***
````
        2
      /    \
     4      16
    / \    /  \
   5   10 23   39
  / \  /
18 26 15

 
  if you do the postorder traversal to this heap, the order goes to:
    18, 26, 5, 15, 10, 4, 23, 39, 16, 2
    
    5 < 26: therefore, postorder traversal of the keys does not list them ascending order.
````
