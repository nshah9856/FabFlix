# Authors: [Nisarg Shah](https://github.com/nshah9856), [Joanna Ko](https://github.com/joannatko)
## Demo Video
View our Project 1 Demo Video here: https://youtu.be/OlU795mq0hg

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
3. Click `Quarana:war exploded`
4. Apply changes and click `OK`
5. Click `Run` application to build, connect server and launch Tomcat.

![Tomcat Build Configuration](./images/tomcat_build.png)

You are now all set up! Visit FabFlix on at `http://localhost:8080/FabFlix`.

## Group Member Contributions: Division of Tasks
### Nisarg Shah
*Adding Summary Here*

### Joanna Ko
- Worked on the SQL table creations for `moviedb`. 
- Worked on the java files.
- Changed/fixed front end UI.
