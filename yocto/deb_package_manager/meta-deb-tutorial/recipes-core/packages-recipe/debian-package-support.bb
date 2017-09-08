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