# Authors: [Nisarg Shah](https://github.com/nshah9856), [Joanna Ko](https://github.com/joannatko)
## Demo Video
- View our Project 1 Demo Video here: https://www.youtube.com/watch?v=OlU795mq0hg
- View our Project 2 Demo Video here: https://www.youtube.com/watch?v=SDhbaxUaCQI
- View our Project 3 Demo Video here: https://www.youtube.com/watch?v=PhrO_PNp6J4
- View our Project 4 Demo Video here: https://www.youtube.com/watch?v=-firS5-hSrM

```diff
! View our Project 5 Demo Video here: https://www.youtube.com/watch?v=DmsVD8qfRug
```


- # General
    - #### Team#: 46
    
    - #### Names: Nisarg Shah, Joanna Ko
    
    - #### Project 5 Video Demo Link: https://www.youtube.com/watch?v=DmsVD8qfRug 
        - The JMeter file used for Demo has been added [here](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/master/Demo.jmx)

    - #### Instruction of deployment: [Click here](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/README.md#deploy-fabflix-remotely)

    - #### Group Member Contributions: Division of Tasks (Project 5)
        ##### Nisarg Shah
        - Worked on Connection Pooling Changes
        - Worked on testing (all the nitty-gritty around it)
        - Worked on Setup alongside Joanna
        ##### Joanna Ko
        - Wrote Connection Pooling changes
        - Worked with Nisarg on Task 2 - 4

        ### Note: The commits might seem biased, but due to the nature of this Project, Joanna & Nisarg worked together through Zoom sessions.

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
        - [FabFlix/src/LoginServlet.java](/FabFlix/src/LoginServlet.java)
        - [FabFlix/src/AddMovieServlet.java](FabFlix/src/AddMovieServlet.java)
        - [FabFlix/src/AddStarServlet.java](FabFlix/src/AddStarServlet.java)
        - [FabFlix/src/AutoCompleteServlet.java](FabFlix/src/AutoCompleteServlet.java)
        - [FabFlix/src/CartServlet.java](FabFlix/src/CartServlet.java)
        - [FabFlix/src/DashboardLoginServlet.java](FabFlix/src/DashboardLoginServlet.java)
        - [FabFlix/src/FetchGenres.java](FabFlix/src/FetchGenres.java)
        - [FabFlix/src/FetchMetaData.java](FabFlix/src/FetchMetaData.java)
        - [FabFlix/src/MoviesServlet.java](FabFlix/src/MoviesServlet.java)
        - [FabFlix/src/PageCountServlet.java](FabFlix/src/PageCountServlet.java)
        - [FabFlix/src/PaymentServlet.java](FabFlix/src/PaymentServlet.java)
        - [FabFlix/src/PlaceOrderServlet.java](FabFlix/src/PlaceOrderServlet.java)
        - [FabFlix/src/SearchServlet.java](FabFlix/src/SearchServlet.java)
        - [FabFlix/src/SessionServlet.java](FabFlix/src/SessionServlet.java)
        - [FabFlix/src/SingleMovieServlet.java](FabFlix/src/SingleMovieServlet.java)
        - [FabFlix/src/SingleStarServlet.java](FabFlix/src/SingleStarServlet.java)
        - [FabFlix/web/META-INF/context.xml](FabFlix/web/META-INF/context.xml)
        
        All changes were commited [here](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/pull/34)
               
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    Previously, the Tomcat Servlet connected to the database by using url and creating connections and run queries.         This took alot of time, establishing new connection and new queries each time. With Connection Pooling we reduce the time it takes by making a more secure connection that stored in `context.xml`. We define our datasource with connection pooling by allocating set of connections in our `context.xml`. When we get connections, it takes a connection from the pool that is pre-created and does not need to be established again. When our connection is finished, we call `close()`, which does not physically close the connection but returned to the pool for future use. Thus the time it takes to establish and free connections is saved. 
    
    - #### Explain how Connection Pooling works with two backend SQL.
    With two backend SQL, we have our database connection to either the Master or Slave's database, and achieves the same purpose explained as above, saving time on establishing and freeing connections.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
        - [FabFlix/src/AddMovieServlet.java](FabFlix/src/AddMovieServlet.java)
        - [FabFlix/src/AddStarServlet.java](FabFlix/src/AddStarServlet.java)
        - [FabFlix/src/PlaceOrderServlet.java](FabFlix/src/PlaceOrderServlet.java)
        - [FabFlix/src/UpdateEmployeeSecurePassword.java](FabFlix/src/UpdateEmployeeSecurePassword.java)
        - [FabFlix/src/UpdateSecurePassword.java](FabFlix/src/UpdateSecurePassword.java)
        - [FabFlix/web/META-INF/context.xml](FabFlix/web/META-INF/context.xml)
        
        All changes were commited [here](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/pull/35)
        
    - #### How read/write requests were routed to Master/Slave SQL?
    With MySQL Replication, a client or web app reads and writes to MySQL Master and only reads to MySQL slave. In our FabFlix, we route write requests to Master while allowing read requests to either Master or Slave.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
    - The `log_processing.py` file is located at the base of the repo, to use it, simply in the command line type:
    ```
    python3 log_processing.py [LOG_FILE]
    ```


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/single-instance-1thread.png)   | 528                         | 487.8185539564724                                  | 487.62180937812263                        | This is using the connection pooling. We notice that there is not much difference in TS and TJ times, as majority of the `doGet` is just `jdbc` related tasks. A weird observation: we see a higher average query time compared to no connection pooling           |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/single-instance-10thread.png)   | 5488                         | 5441.411128574943                                  | 5439.654045677896                        | We see that with 10 threads, we approximately get 10x average query time from that of a single thread. Also, we see that throughout went lower on this run, possibly making the average a bit higher.           |
| Case 3: HTTPS/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/single-instance-10thread-https.png)   | 5321                         | 5264.745196152914                                  | 5260.59513484595                        | This is approximately the same time as 10 threads with HTTPS, which goes against our speculation as HTTPS adds overhead. But, a possible explanation to this could be that the throughput on this run was a bit higher than with the HTTP run           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/Single-instance-no-pooling-10-threads.png)   | 4927                         | 4884.495033007949                                  | 4884.264358771386                        | We see that the average query time is drastically (not THAT much but still) lower than that with connection pooling, which again goes contradictory to our speculatoins. There is a possible explanation thought, the throughput in this run was way higher than that with the other runs.           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/Load-balancer-1thread.png)   | 527                         | 486.194070922                                  | 486.023972279                        | For the load balancer, even though we split the load between two instances, the average query time is similar to that of a single instance. But, the benifit of load balancing is definetely evident with more threads below.           |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/Load-balancer-10threads.png)   | 2506                         | 2463.57727315                                  | 2463.28637806                        | Here, we see that the average query time is practically is half of single-instance run. This in a sense proves, that load balancing is achieving its purpose. We also see that here it is evident that connection pooling does indeed help with the query time. The reason it is evident here is probably because the workload being divided does not make our instances slower due to memory constraint of free tier.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/develop/graphImages/Load-balancer-no-pooling-10-threads.png)   | 2600                         | 2557.29069713 | 2557.03389475                        | Here, again, we pracically see that the average query time is half of single instance. We also notice that no connection pooling results in about 100ms more, or abt 10ms per request (estimates)           |

#### Observation: Amazon instances with their limited resources might have proven to the bottlenecks for slight inconsistencies in query times, as we encountered instances getting stuck occassionally because of the bombardment of requests.

## Deploy FabFlix (Remotely)
1. Git clone repository: `git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/`

2. Change directory into repo: `cd cs122b-spring20-team-46`

3. Build war file: `mvn package`

4. Copy the war file into tomcat: `cp ./target/*.war /home/ubuntu/tomcat/webapps`

5. Open Tomcat Domain at *\<your-amazon-instance-domain\>:8080*

6. Go to Manager Apps > Click FabFlix

You should now be on the movie list page.

## Deploy FabFlix (Locally on Development Machine)
### Git clone repository
`git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/`

### IntelliJ Configuration
Import Project from External Model > Choose Maven

### To Connect Tomcat 
1. Click `Add Configurations` / `Edit Configurations`
2. Fix button should appear at bottom right screen 
3. Click `FabFlix:war exploded`
4. Apply changes and click `OK`
5. Click `Run` application to build, connect server and launch Tomcat.

![Tomcat Build Configuration](./images/tomcat_build.png)

You are now all set up! Visit FabFlix on at `http://localhost:8080/FabFlix`.

## Substring Matching Design
Used the following from Instructions on P2 Task 2 (`LIKE`)

Mainly our `LIKE` statements reside under `SearchServlet.java` files, but there is also a replicatino (not exactly the same) under `PageCountServlet.java` to make sure we are fetching the correct # of pages.

For example: `where title like ? "AND year like ? AND director like ?` -- This is the format adapted in the file and `?` if replaced with parameter setting (`PreparedStatement`)

# Project 4 - Full Text Search, Autocomplete, Android Application, Fuzzy Search

## To deploy our FabFlix on an Android Emulator, type the follow commands in your terminal.
#### Note: This is assuming you cloned, and are in FabFlixMobile directory. With AndriodSDK installed.

1. Create an APK package `./gradlew build`
2. Open Android Emulator `emulator -avd {REPLACE_WITH_EMULATOR_NAME}`
3. Install Android APK to Emulator `adb install -t app/build/outputs/apk/debug/app-debug.apk`

## Design and Implementation of Fuzzy Search
 We use the Levenshtein (Edit Distance) Algorithm (LEDA) to implement the fuzzy search. 
  - Flamingo, from the Professor's research is used to make the UDF.
 To make use of `ed` (Edit Distance Algorithm function in MySQL), in our backend (`AutoCompleteServlet.java` & `SearchServlet.java`) we dynamically set the `threshold` by checking the length of query. 
 ```
 - If length of title is less than 4, than 1 error is allowed. 
 - If length if less than 6 than 2 errors are allowed. 
 - Else, 3 errors are allowed. (3 is the most errors one can make for fuzzy search to be considered)
 ```
 - We convert the title to lowercase when conducting `ed` to not include case errors as typing errors.
 - We conduct an `OR` query to consider Fulltext search along with Fuzzy search. 
 - (Ex: `select * from movies where match(title) against ("+s* +lov*" in boolean mode) OR ed("s lov",lower(title))`)

# Project 3 - 

#### NOTE: To go back from dashboard to FabFlix go to /FabFlix/mainPage.html 

## Inconsistences 
  We have written two files [inconsistentGenreInMovies.md](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/master/Parser/inconsistentGenreInMovies.md) and [inconsistentGenres.md](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-46/blob/master/Parser/inconsistentGenres.md)
  
  We have not written out as talked about in the demo inconsistences in parsing (such as `parseInt` kind of problems. 

  ## Efficiency
  To make parsing efficient, we adapted two crucial steps.
  1. We load all needed data into JAVA memory in one query ( saving back-n-forth with db)
  2. We output the `new` inserts such as for `stars`, `genres`, `movies`, `stars_in_movies`, and `genres_in_movies` into `.txt` files so that we can easily and efficiently `load` them into the sql database. 
  
  This we noticed saved us HUGE amount of time compared to when we tried single inserts in the middle of parsing. 


## Group Member Contributions: Division of Tasks (Project 1)
### Nisarg Shah
- Worked with Joanna on SQL queries and table creattions.
- Mainly worked on the front-end (JS and HTML)
- Conducted fixes in pom.xml and servlet's

### Joanna Ko
- Worked on the SQL table creations for `moviedb`. 
- Worked on the Java files.
- Changed/fixed front end UI.

## Group Member Contributions: Division of Tasks (Project 2)

### Nisarg Shah
- Created and worked with Joanna on:
  - MainPage, NavBar, Payment, Cart, Order, etc.. (JS)
  - HTML/CSS
  - Servlets for fetches of genre's, sessions, payments, orders, etc.. (Java)

### Joanna Ko
- Login Page
- Building SQL queries with Nisarg
- Single Movie/Stars, Movie List page with Nisarg
- Creditcard Page
- Worked on front end (html/css) for correlated pages

## Group Member Contributions: Division of Tasks (Project 3)
### Nisarg Shah
- Worked on reCaPTCHA and HTTPS 
- Helped with understanding and writing encryption and stored procedure
- Contributed in the _dashboard screen.
- Worked with joanna on parsing and storing xml data efficiently

### Joanna Ko
- Worked with Nisarg with reCaPTCHA set up
- Worked on HTTPS
- Worked on Encryping passwords
- Wrote Stored Procedure for adding movies
- Worked with Nisarg on XML parsing

## Group Member Contributions: Division of Tasks (Project 4)
### Nisarg Shah
- Worked on the fronend of Autocomplete for FabFlix.
- Worked with Joanna on developing the Mobile app (Constraint layouts, backend calls and parsing, etc.)
- Integrated Fuzzy Search into the backend.

### Joanna Ko
- Wrote Full Text Search SQL commands
- Worked on AutoComplete Servlet
- Worked with Nisarg on Android Studio App
