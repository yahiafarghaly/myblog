# Cannot execute menuconfig task from ssh session ? here is the solution

If you build images with yocto on a remote server using ssh, then you may face some problems of failure of executing tasks with bitbake which requires spawning another shell to work with it. One of these tasks is *** menuconfig *** task which promotes your image kernel configurations in another terminal which is usually the gnome terminal.


The easiest way i figured for solving this problem is using [tmux](https://github.com/tmux/tmux) which enables managing multiple of terminals depending on each other in a single window. By that, tmux enables us to have a menuconfig view to be called as a child for the parent terminal of your ssh connection.

following the [README](https://github.com/tmux/tmux/blob/master/README) of [tmux](https://github.com/tmux/tmux). Remember to install the required dependencies of [tmux](https://github.com/tmux/tmux) which are [libevent 2.x](http://libevent.org) and [ncurses](http://invisible-island.net/ncurses/)

On Ubuntu, you can install them using apt-get
```sh
$ sudo apt-get update
$ sudo apt-get install ncurses-*
$ sudo apt-get install libevent-dev
```

For using tmux with bitbake, type
```sh
$ tmux
$ bitbake -c menuconfig virtual/kernel
 # save your kernel configurations
 # to exit tmux,type
$ exit
```

That is all, hope this works for you. 
