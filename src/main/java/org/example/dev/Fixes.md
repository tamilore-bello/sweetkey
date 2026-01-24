# FIXES

1. In USERDAO: we return an ArrayList. The safest way would be to return an immutable List.  
2. IN USERDAO: Instead of the return void and try/catch setup, it is more appropriate to have the method throw an exception
4. In USERDAO: throw appropriate exceptions or returns instead of all the nulls.
5. In USERDAO: make all operations thread-safe
6. In VM: have functions that validate fields.

