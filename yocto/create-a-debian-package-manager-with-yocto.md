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
$ mkdir -p conf images recipes-core/packages-recipe
$ touch conf/layer.conf images/my-image.bb
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
