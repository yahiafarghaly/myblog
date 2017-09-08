SUMMARY = "An image which add the support of using deb package management"

inherit core-image
include recipes­core/images/core­-image-­minimal.bb

IMAGE_FEATURES += "package-management"
IMAGE_INSTALL_append = "debian-package-support gnupg coreutils "

IMAGE_LINGUA = " "

LICENSE = "MIT"

IMAGE_ROOTFS_SIZE ?= "16192"
