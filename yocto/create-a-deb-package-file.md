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

Note, that i made multiply for x86 and add for x64, the reason for that will come later. Another note, if you gonne make more description,make to ident space for each new line. for example
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

## Modifying the sources.list file in yocto image.
