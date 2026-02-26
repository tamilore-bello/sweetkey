**Commission Tracking System Application, titled Sweetkey.**

> An application that allows for the creation and deletion of users. An individual user can create 
commissions and store data associated with them, such as date ordered, due date, descriptions and specifications, etc.   

The goal for sweetkey is to create an application where users can store, manage, and keep track of various commissions, as well as store their commission history.   

**Technical Highlights of Sweetkey**
> Layered architecture: Swing UI, business logic (ViewModel), and data access (DAO).
> Designed a relational schema with foreign key relationships (between Users and Commissions)
> Used JDBC with prepared statements and parameter binding (as opposed to SQL injection).
> Used salted password hashing methods as opposed storing plain text.
> Used dynamic aggregation queries -- the aggregation is dependent on enum-based input
> Wrote analytical queries for metrics related to earnings, overdue detection, and custom sorting
> Cleared sensitive password data from memory after authentication.


<br><br>
**What inspired it?**
> I love art, I’m a digital art hobbyist who sometimes takes on commissions for fun myself. I haven’t seen any commission tracking applications that are widely accepted in the art community, so I thought a fun project would be to draw one up a realistic tool that could support artists, while showcasing my knowledge of basic backend architecture and structures.<br>
> It was a subject area I was personally familiar with, and passionate about.<br>
> I also became familiar with password hashing technigues through this.

<br><br>
**How I built Sweetkey.**
> First, I built the core model with two objects: Artist and Commission.<br>
> Then I introduced docker and researched + tested SQL database containerized DB<br>
> Then, I worked on the minimum database functions i needed, such as creation of users and commissions and fetching. I mapped each model to a respective SQL table and declared foreign keys and created a DAO (data access object) for it.<br>
> I continued to work on functions using a model/logic/driver setup using the CLI for testing. i had done all the major functions and it was time to tackle the GUI.<br>
> I did it methodically with Swing, at the same time, further adding complexity to DB operation functions and helpers. I also moved from the temporary informal password plaintext saving to hashing.<br>
> The rest of the project was just going down the list of functions I wanted my app to have and implementing them, while making the apps itself have a user-friendly experience and following best practices for error and validation handling. <br>
> Throughout all the steps, i forked to github, using the stable branch whenever i knew my project was error-free, and and unstable branch whenever i wasn’t confident that it was free of errors and merging/pulling using Git CLI as needed.

**Config Notes**
> this application was developed locally. I explored containerization of the entire app, however, I have yet to implement it. Setup will require docker, and the default URLs + password for connectivity can be found in the devutils.java class. 
