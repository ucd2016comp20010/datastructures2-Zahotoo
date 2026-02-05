Starting repository for `Data Structures` COMP20280 2025-2026

### Wk 2: Linked Lists Exercises Q5 - Q8:
- *Q5: Are the tests complete?*  
A: All testcases passed.


- *Q6: What is the difference between a singly linked list and a circularly linked list?*  
A: The Circularly Linked List has no pointer to null at the end
and instead the end points back to the head. Compare to the Singly Linked List,
Circularly Linked List has no traditional beginning which means any node can be a starting node.
We can traverse the whole list by starting from any node and stop when the first visited node is visited again.
However, there has a clear head and an end point in the Singly Linked List.


- *Q7: In what situations would you prefer to use a linked list to an array?*  
A:
  1) We need constant-time insertions/delations from the list.
  2) We don't know how many items will be in the list.
  3) We don't need random access to any elements.
  4) We want to be able to insert items in the middle of the list.  


- *Q8: Describe 2 possible use-cases for a circularly linked list.*  
A:
  1) Round-robin scheduling: Circularly linked lists are commonly used in round-robin scheduling, such as CPU process or turn-based games.
  Each task or player is visited in order and after the last node, the list cycles back to the first.
  2) Music playlists: A circularly linked list is ideal for a music playlist. Once the last song is reached, 
  the next pointer automatically returns to the first song.


