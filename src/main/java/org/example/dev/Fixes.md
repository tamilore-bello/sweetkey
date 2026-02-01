# FIXES
### A current list of items that I would like to return to and fix eventually...

1. In USERDAO: we return an ArrayList. The safest way would be to return an immutable List.  
2. In USERDAO: Instead of the return void and try/catch setup, it is more appropriate to have the method throw an exception 
3. In USERDAO: throw appropriate exceptions or returns instead of all the nulls.
4. In VM: have functions that validate fields.
5. In MAIN: add a selector for different ways to order the user's commissions
   (by cost lowest/highest, due soonest, oldest commissions, progress level most/least, unpaid/paid, etc.)
6. fix WEEK query (try rolling week)
7. finish settings tab
8. add witholding estimate USA

