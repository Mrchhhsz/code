
default:	build

clean:
	rm -rf Makefile objs

.PHONY:	default clean

build:
	$(MAKE) -f objs/Makefile

install:
	$(MAKE) -f objs/Makefile install

modules:
	$(MAKE) -f objs/Makefile modules

upgrade:
	/cygdrive/d/app/nginx-release-1.20.0/sbin/nginx -t

	kill -USR2 `cat /cygdrive/d/app/nginx-release-1.20.0/logs/nginx.pid`
	sleep 1
	test -f /cygdrive/d/app/nginx-release-1.20.0/logs/nginx.pid.oldbin

	kill -QUIT `cat /cygdrive/d/app/nginx-release-1.20.0/logs/nginx.pid.oldbin`

.PHONY:	build install modules upgrade
