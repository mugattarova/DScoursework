## Auction network application

### About
This app simulates an auction functionality at localhost, handling multiple bidders (clients), racing conditions, and distributed backend architecture, where backend nodes can join in and out.

### Running the app
1. run `build.sh`
2. start the `rmiregistry` by running 

```
cd bin/server/
rmiregistry 
```
3. in a separate terminal, run the 3 components of the system: `runbackendserver.sh`, `runfrontendserver.sh`, `runclient.sh`.
As many backend servers or clients can be created by running the commands above, although the frontend server should be kept singular. They automatically open in a new bash terminal, so no need to create a terminal manually.

### Structure
As long as one backend server is running, the rest can be killed off, while preserving the state. Client log in information can be reused to log in from different terminals as well. Upon startup, some example users will be created, which the backend will report on, these can be logged into as well.

Learn more about the structure and design decisions in `dsreport.pdf`.
