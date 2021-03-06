# Create a Debian package manager with yocto
I will present here a series of posts about
  - How add deb package support to a yocto image.
  - How to create a simple server using apache2 to host debian files.
  - How to create a deb package file.
  - Finally, how we can authorize our packages so it can be trusted from apt-get. 
  
  > I assume you know how to work with yocto (i.e creating layers, recipes and the basic concepts are enough)
  
Let's start,

## Create a new image
First, we will create a new image based on core-minimal-image, so we can modify it as we want without editing in the original core minimal image or globel configurations for any created image from conf/local.conf

  > The tutorial is based on yocto pyro version(17.0.1) but it should work fine for any other versions.  
  
```sh
$ cd <yocto directory>
$ source oe-init-build-env
$ yocto-layer create deb-tutorial
```
Don't go with the default steps of creating the layer and do instead
```sh
$ mkdir -p conf recipes-core/images recipes-core/packages-recipe
$ touch conf/layer.conf recipes-core/images/my-image.bb
```
#### in conf/layer.conf

```python
# We have a conf and classes directory, add to BBPATH
BBPATH .= ":${LAYERDIR}"

# We have recipes-* directories, add to BBFILES
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
	${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "mylayer"
BBFILE_PATTERN_mylayer = "^${LAYERDIR}/"
BBFILE_PRIORITY_mylayer = "99"
```

#### in images/my-image.bb 
```python
SUMMARY = "An image which add the support of using deb package management"

inherit core-image
include recipes­core/images/core­-image-­minimal.bb

IMAGE_FEATURES += "package-management"

IMAGE_LINGUA = " "

LICENSE = "MIT"

IMAGE_ROOTFS_SIZE ?= "16192"
```
Here we inherit core-minimal-image then add to this image a feature of supporting **package-managment**

#### in local/local.conf 
edit the PACKAGE_CLASSES Variable to support debian packages format in yocto
```python
PACKAGE_CLASSES ?= "package_deb"
```

The last thing is to add our new layer to bblayer.conf in your build directory

```python
BBLAYERS ?= " \
  /home/yahia/Desktop/elinux_dev/bake_factory/poky-pyro-17.0.1/meta \
  /home/yahia/Desktop/elinux_dev/bake_factory/poky-pyro-17.0.1/meta-poky \
  /home/yahia/Desktop/elinux_dev/bake_factory/poky-pyro-17.0.1/meta-yocto-bsp \
  /home/yahia/Desktop/elinux_dev/bake_factory/poky-pyro-17.0.1/meta-deb-tutorial \
  "
```
then we bitbake 
```sh
$ bitbake my-image
``` 
then run the image on whatever the target you working on, i use qemux86-64 .. and finally, you have an image which has an apt-get command that capable of install deb packages.

In the next post, we will [create a simple server using apache2](create-a-simple-server-using-apache2.md) to host debs files.
