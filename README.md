# Digital Archive of Queensland Architecture (DAQA)

The Architectural Practice in Postwar Queensland (1945-1975): Building and Interpreting and Oral History Archive is an Australian Research Council, Linkage Project. The aim of the project is to develop an oral history resource capturing the implicit knowledge of Queensland’s postwar architects.

The project will document the histories of Queensland’s postwar architects. It will produce a new digital research resource on Queensland architecture and an exhibition which will explore the role of climate, place, innovation and sustainability in Queensland’s postwar architecture.

The stories of Queensland’s post war architects remain unrecorded. Gathering these histories into a new digital archive will produce the first comprehensive account of the individuals and firms who built Queensland’s architectural identity in the post WWII period. This will be a leading resource for scholarly research and architectural practice. By applying innovative semantic web technologies researchers will map the intellectual networks informing architectural practice and education in post-war Queensland. The history of present day concerns for place, innovation, and sustainability will be communicated with innovative multi-media technology to build new communities of interest in Queensland design, past, present and future.

The site is at <https://qldarch.net>

## Backend component

This repository contains the backend component of the DAQA web stack.

### Build backend release and deploy on server

Building the backend release is done from your development setup. It is java based and the prerequisite for building are just an installed version of java >=1.8 and the apache ant build tool. The backend repository is on github so clone first if you have not done so already. Make sure your working copy is clean and only contains the changes you want to have in this release. Bump the version in version.txt (stored in the project root) as required.
- In the project root execute `ant clean build`. This might take a while if you have not done this before as all the dependencies have to be downloaded.
- If the build finishes successfully the resulting war file is in `build/qldarch-<version>.war`
- Copy the war file to the destination system into the /opt/qldarch/dist
- Next ssh into the server and become root.
- As root change the symlink /opt/qldarch/backend/qldarch.war to the new war file that you have just uploaded. It is a good idea to take note of the current version the symlink is pointing to just in case you want to revert to this version later. e.g. `ln -s -f /opt/qldarch/backend/dist/qldarch-<version>.war /opt/qldarch/backend/qldarch.war`
- Restart the qldarch tomcat server by executing as root: `service tomcat8 restart`
- Check the logfiles if the server starts up ok and make sure the qldarch is loading ok in the browser.

### Configuration file
The backend configuration file example is provided in qldarch.xml.example and must be configured with the correct credentials and copied to `/etc/tomcat8/Catalina/localhost/qldarch.xml` on the qldarch instance.


