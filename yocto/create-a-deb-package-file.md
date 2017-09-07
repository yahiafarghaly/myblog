# Create a debian package file
In this post, we will create a debian package file from a compiled binary source and put in our server in addition of modifying the source list in yocto image so it can see the packages.

## Creation of a debian package 
There are many ways of making a debian package out their but i will keep it simple and enough to get your binary package in a fast way. 
first, create a folder with the ``` <package name>_<major version>.<minor version>-<package revision> ``` (this is a standard debian notation and it is all lowercase). let's say we want to make an application which calculate the addition of command lines numbers and another one for multiplying so, we create two directories for them

```sh
$ mkdir add_1.0-0 multiply_1.0-0
```
In both of them, we create a DEBIAN directory which will contain metadata about the deb package and another one which simulate where the finally binary should resist (act as where it will be under the root).
```sh
$ mkdir -p add_1.0-0/DEBIAN add_1.0-0/usr/bin multiply_1.0-0/DEBIAN multiply_1.0-0/usr/bin
$ touch add_1.0-0/DEBIAN/control multiply_1.0-0/DEBIAN/control
```
by following the above command, our finally packages will be under /usr/bin. The *control* file is the metadata which the package manager(apt-get) read to be able to install, upgrade and resolve dependancies of deb package.
the control file is something like this

** for add **
```
Package: add
Version: 1.0-0
Section: base
Priority: optional
Architecture: amd64
Depends:
Maintainer: yahia farghaly (yahiafarghaly@gmail.com)
Description: A simple program that add numbers and return the result
```
** for multipy **
```
Package: multiply
Version: 1.0-0
Section: base
Priority: optional
Architecture: i386
Depends:
Maintainer: yahia farghaly (yahiafarghaly@gmail.com)
Description: A simple program that multiply numbers and return the result
```

Note, that i made multiply for x86 and add for x64, the reason for that will come later. Another note, if you gonne make more descriptions, make sure to ident space for each new line. for example
```
Description: A simple program that multiply two numbers and return the result
  etc ......................................
  etc .................
```
Now, let's put make generate our binary files

***add.c***
```c
#include <stdio.h>

int main(int argc,char ** argv)
{
  int i, sum = 0;
     for (i = 1; i < argc; i++)
        sum = sum + atoi(argv[i]);

 printf("%d\n",sum);

 return 0;
}
```

***multiply.c***
```c
#include <stdio.h>

int main(int argc,char ** argv)
{
  int i, sum = 1;
     for (i = 1; i < argc; i++)
        sum = sum * atoi(argv[i]);

 printf("%d\n",sum);

 return 0;
}
```
Then compiling them, i will compile them statically not dynamic since my-image is inherit from core-minimal which doesn't contain the C shared library.

```sh
$ gcc -static -m32 multiply.c -o multiply
$ gcc -static  add.c -o add
```
now, move each binary in its folder under usr/bin.

the finally tree for add_1.0-0 multiply_1.0-0
```
add_1.0-0
├── DEBIAN
│   └── control
└── usr
    └── bin
        └── add
multiply_1.0-0
├── DEBIAN
│   └── control
└── usr
    └── bin
        └── multiply
```

now, execute this command on both packages to create a debian file foe each of them.
```sh
$ dpkg-deb --build add_1.0-0/
dpkg-deb: building package `add' in `add_1.0-0.deb'.
$ dpkg-deb --build multiply_1.0-0/
dpkg-deb: building package `multiply' in `multiply_1.0-0.deb'.
```
Now, we made our debian packages. Let's moves them to *my-repo* directory, so the tree looks like this
```
/var/www/html/my-repo/released-packages/
├── add_1.0-0.deb
└── multiply_1.0-0.deb
```
Now, we have debian files in one place and what is left, is making this directory(released-packages) looks like the official debian repositories, we will make a file which a tool called **reprepro** will structure the released-packages with "The Debian Way" for repositories. 
```sh
$ touch conf/distributions
```
in the distributions file,
```
Origin: yahia-repo
Label: apt repository
Codename: yahia
Architectures: amd64 i386
Components: main
Description: test authorization
Pull: yahia
```
the distribution here is 'yahia' with component 'main', you can read more about from [here](http://www.ibiblio.org/gferg/ldp/giles/repository/repository-2.html) but the important thing here is the Architectures section. It should contain all architectures your repo support.

After creating this file, execute the following command to generate structured files on the debain way.
```sh
$ ls 
$ add_1.0-0.deb multiply_1.0-0.deb conf
$ reprepro --ask-passphrase -Vb . includedeb yahia add_1.0-0.deb
$ reprepro --ask-passphrase -Vb . includedeb yahia multiply_1.0-0.deb 
```
you should do the command for each deb file, the tree of this directory will be

```
.
├── add_1.0-0.deb
├── conf
│   └── distributions
├── db
│   ├── checksums.db
│   ├── contents.cache.db
│   ├── packages.db
│   ├── references.db
│   ├── release.caches.db
│   └── version
├── dists
│   └── yahia
│       ├── main
│       │   ├── binary-amd64
│       │   │   ├── Packages
│       │   │   ├── Packages.gz
│       │   │   └── Release
│       │   └── binary-i386
│       │       ├── Packages
│       │       ├── Packages.gz
│       │       └── Release
│       └── Release
├── multiply_1.0-0.deb
└── pool
    └── main
        ├── a
        │   └── add
        │       └── add_1.0-0_amd64.deb
        └── m
            └── multiply
                └── multiply_1.0-0_i386.deb
```
as you note, it seperates the packages depending on the architecture. Now our ready is ready to be identified by apt-get package manager. 

## Modifying the sources.list file with yocto.
Before we run our image again, we need to modify this file (/etc/apt/sources.list) on the image which tells apt-get where to get the packages. by default it is empty and our goal is to write to it as
```
deb http://156.xxx.xxx.xxx/my-repo/released-packages yahia main
```
So, in recipes-core directory, create a recipe folder called **packages-recipe** and inside it create the following
```sh
$ mkdir packages-recipe/files -p
$ cd packages-recipe
$ touch debian-package-support.bb files/sources.list
```
In sources.list, we put the last line mentioned 
```
deb http://156.xxx.xxx.xxx/my-repo/released-packages yahia main
```

In **debian-package-support.bb** ,we write
```python
SUMMARY = "Responsible for providing all facilities to prepare deb package manager"
LICENSE = "CLOSED"
PR = "r0"

SRC_URI = "file://sources.list \
			"


do_install() {
	install -d ${D}/etc/apt/
	install -m 0644 ${WORKDIR}/sources.list ${D}/etc/apt/ 
}
```

then in **my-image.bb** we add this line, so bitbake install our new recipe.
```python
IMAGE_INSTALL_append = "debian-package-support"
```
the recipe is simply transfer our modified sources.list to rootfs of image.

Then we ***bitbake*** :) and execute runqemu qemux86-64

After login into the image, we execute
```sh
apt-get update
```
and you should expect an output similar to that 
```
Ign:1 http://156.222.48.201/my-repo/released-packages yahia InRelease
Get:2 http://156.222.48.201/my-repo/released-packages yahia Release [1603 B]
Ign:3 http://156.222.48.201/my-repo/released-packages yahia Release.gpg
Get:4 http://156.222.48.201/my-repo/released-packages yahia/main amd64 Packages [337 B]
Fetched 1940 B in 0s (4303 B/s)  
Reading package lists... Done
W: The repository 'http://156.222.48.201/my-repo/released-packages yahia Release' is not signed.
N: Data from such a repository can't be authenticated and is therefore potentially dangerous to use.
N: See apt-secure(8) manpage for repository creation and user configuration details.

```

Congratulation !, you made a successful pull from your online repository to your target machine.

### Two notes to say
 - you notice it pulls only the amd64, this is because the kernel i built for qemux86-64 was 64-bit,so it should able to run both 64bits and 32bits programs but it pulled only the 64bits version (add program). if you build for qemux86, it will pull only the 32 bit version(multipy program)
 - The warning about the authoriztion of packages since we haven't signed our packages to be trusted yet by apt-get. But we will discuss that in the next post.
 
 Now, you can do
 ```sh
 $ apt-get install add
 ```
 and it is installed in /usr/bin as defined before in the debian file. And by that it's added to the same $PATH of current shell so you can execute it directly.
 ```sh
 $ add 4 2 3
 $ 9
 ```
 if we change the version of add to say (1.1-0) and regenerate a deb package and put it in the server with the suitable structure to be generated again for the package. We can definitly execute apt-get upgrade without the need of rebuilding the image with the new version. 
 
 This can be enough for you but if you want your repository packages to be trusted from apt-get so it got be installed without warning. follow the [next post]()
