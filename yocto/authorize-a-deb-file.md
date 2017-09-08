
Authorization of packages means to verify that the pulled packages are come from a trusted source. And for this operation to be done. a private key is signed to packages in the repo and a public key is distributed to who will pull from these repository.

you can follow the steps mentioned [here](https://help.ubuntu.com/community/CreateAuthenticatedRepository) to create a private and public key for repository until the step before ***Sign the packages with your key***  

After you created the private key in your machine, it is time to sign the packages with it. You can use this script which directly create the repo in the debian way and sign each deb file with the private key. 

```bash
#!/bin/bash

rm -rf db dists db pool
echo "Enter sudo and passphare"
for f in *.deb; do
 echo "Signing $f";
 sudo dpkg-sig --sign builder "$f" 
 # change yahia name in the command 
 reprepro --ask-passphrase -Vb . includedeb yahia "$f"
done
echo "Done"
```
After entering the passphrase for ecah package, then they have signed. now export the **keyFile** (which contain the public key) that will be shipped to our image so apt-get can trust the coming package from the provided URL in sources.list

After you generated the **keyFile**, we need to move it to ***packages-recipe/file*** . After that we modify the **debian-package-support.bb** as following

```python
SUMMARY = "Responsible for providing all facilities to prepare deb package manager"
LICENSE = "CLOSED"
PR = "r0"

SRC_URI = "file://sources.list \
			file://keyFile \
			"
      
do_install() {
	install -d ${D}/etc/apt/
	install -m 0644 ${WORKDIR}/sources.list ${D}/etc/apt/ 
	install -m 0644 ${WORKDIR}/keyFile ${D}/etc/
}

pkg_postinst_debian-package-support() {
	if [ x"$D" = "x" ]; then
	  apt-key add /etc/keyFile
	  logger "key file is added"
	else
	  exit 1
	fi
}
```
The first difference is that we install keyFile inside /etc directory. The second one is that we use pkg_postinst_packageName() . It is a [feature in yocto](http://www.yoctoproject.org/docs/2.3.1/mega-manual/mega-manual.html#new-recipe-post-installation-scripts) where we can perform certain operations after the creation of the build rootfs. if it fail to run at the creation of rootfs, it is executed on the boot on the target image for the first time. 

In our case, to use ** apt-key add ** which add the public key to apt-get on our image,it needs to be delayed until the image is up and running, so i use this format of condition to make a failure at the build to be got executed at the boot time.

One last thing, apt-key is depending on gnupg library which doesn't exist in the core-minimal-image. So, we need to install its recipe alongside ** debian-package-support ** recipe. so we type in ***my-image.bb***
```python
IMAGE_INSTALL_append = "debian-package-support gnupg coreutils "
```
I also added **coreutils** since it is needed by gnupg to operate correctly in the image. so **let's bitbake** !!


After login to your image after build, type 
```sh
$ apt-key list
```
and it should list a brief content of your public key. Now,let's run apt-get update
