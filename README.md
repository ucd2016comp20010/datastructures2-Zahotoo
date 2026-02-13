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
  
  ````
  3. Draw a representation of this binary tree such that a **postorder** traversal of the tree gives the result: "EXAMFUN".
  ````
  
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

