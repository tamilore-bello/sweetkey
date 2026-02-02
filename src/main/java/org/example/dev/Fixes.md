# FIXES
### A current list of items that I would like to return to and fix eventually...

1. In USERDAO: Instead of the return void and try/catch setup, it is more appropriate to have the method throw an exception 
2. In USERDAO: throw appropriate exceptions or returns instead of all the nulls.
3. In VM: more robust validation
4. In MAIN: have the normal be to only display UNCOMPLETED commissions and have a 5th alternative for the ARCHIVED/ALL
commissions.
5. In USERDAO: convert hard codes to enum
6. In USERDAO: make deleting the user and their comms a transaction (just in case!)
7. In VM: date_ordered must be equal to or before date_expected
7. In MAIN: detailed view for commissions, including changing status (completion) and payment received (paid)
   and possibly also date_expected, description, and reference link


