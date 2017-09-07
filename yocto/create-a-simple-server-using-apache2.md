# create a simple server using apache2

Since the goal of the existing of package manager is to install and update the binaries on the target image without building the image again with updated version of binaries. we need to create a server which hosts our debain files.
For the purpose of simplicty, i will convert my machine to a server using apache2.

first, install apache2 on your machine
```sh
$ sudo apt-get install apache2 
```

After installing apache2, apache2 creates a folder in **/var/www/html** (This is where apache2 server running from) which means if i am accessing this server from another place, it will direct me to this folder. So to test that apache2 is running,type in your browser 
```
http://<your public IP>/index.html 
```
Probably, you will see it loading without showing anything. This is because your gateway(i.e home router) is not configured to forward http requests to your machine (i.e laptop). you need to search about how to configure your router port forward. It is usually exist in a section called **port forwading** where you can add a rule for forwarding as the below shown image.
