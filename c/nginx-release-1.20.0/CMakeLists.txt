cmake_minimum_required(VERSION 3.15)
project(nginx_release_1_20_0)

set(CMAKE_CXX_STANDARD 14)

INCLUDE_DIRECTORIES(./objs)
INCLUDE_DIRECTORIES(./src/core)
INCLUDE_DIRECTORIES(./src/event)
#INCLUDE_DIRECTORIES(./src/event/modules)  # 平台相关不能加入所有的源码
INCLUDE_DIRECTORIES(./src/os/unix)
INCLUDE_DIRECTORIES(./src/http)
INCLUDE_DIRECTORIES(./src/http/modules)
INCLUDE_DIRECTORIES(./src/mail)

aux_source_directory(. SRC_LIST)
aux_source_directory(./src/core SRC_LIST)
aux_source_directory(./src/event SRC_LIST)
#aux_source_directory(./src/event/modules SRC_LIST) # 平台相关不能加入所有的源码
aux_source_directory(./src/os/unix SRC_LIST)
aux_source_directory(./src/http SRC_LIST)
aux_source_directory(./src/http/modules SRC_LIST)


# modify src list
set(SRC_LIST ${SRC_LIST} ./src/event/modules/ngx_epoll_module.c)
set(SRC_LIST ${SRC_LIST} ./objs/ngx_modules.c)

list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_darwin_init.c ./src/os/unix/ngx_darwin_sendfile_chain.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_freebsd_init.c ./src/os/unix/ngx_freebsd_sendfile_chain.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_solaris_init.c ./src/os/unix/ngx_solaris_sendfilev_chain.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_linux_sendfile_chain.c)
list(REMOVE_ITEM SRC_LIST ./src/core/ngx_thread_pool.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_file_aio_read.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_linux_aio_read.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_thread_cond.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_thread_mutex.c)
list(REMOVE_ITEM SRC_LIST ./src/os/unix/ngx_thread_id.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_dav_module.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_geoip_module.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_degradation_module.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_grpc_module.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_image_filter_module.c)
list(REMOVE_ITEM SRC_LIST ./src/http/modules/ngx_http_stub_status_module.c)
list(REMOVE_ITEM SRC_LIST ./src/event/ngx_event_acceptex.c)
list(REMOVE_ITEM SRC_LIST ./src/event/ngx_event_connectex.c)
list(REMOVE_ITEM SRC_LIST ./src/event/modules/ngx_select_module.c)
list(REMOVE_ITEM SRC_LIST ./src/event/modules/ngx_poll_module.c)

add_executable(${PROJECT_NAME} ${SRC_LIST})
TARGET_LINK_LIBRARIES (${PROJECT_NAME} dl pthread crypt ldap lber pcre ssl crypto dl pthread z xml2 xslt exslt)