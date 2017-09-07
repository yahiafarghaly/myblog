# Create a Debian package manager with yocto
I will present here a series of posts about
  - How add deb package support to a yocto image.
  - How to create a simple server using apach2 to host debian files.
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
$ yocto-layer create mylayer
```
