# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.15

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /cygdrive/d/user/.CLion/system/cygwin_cmake/bin/cmake.exe

# The command to remove a file.
RM = /cygdrive/d/user/.CLion/system/cygwin_cmake/bin/cmake.exe -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /cygdrive/d/proc/app/redis-6.2.0

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/redis-example.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/redis-example.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/redis-example.dir/flags.make

CMakeFiles/redis-example.dir/src/sds.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/sds.c.o: ../src/sds.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/redis-example.dir/src/sds.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/sds.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/sds.c

CMakeFiles/redis-example.dir/src/sds.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/sds.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/sds.c > CMakeFiles/redis-example.dir/src/sds.c.i

CMakeFiles/redis-example.dir/src/sds.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/sds.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/sds.c -o CMakeFiles/redis-example.dir/src/sds.c.s

CMakeFiles/redis-example.dir/src/dict.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/dict.c.o: ../src/dict.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object CMakeFiles/redis-example.dir/src/dict.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/dict.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/dict.c

CMakeFiles/redis-example.dir/src/dict.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/dict.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/dict.c > CMakeFiles/redis-example.dir/src/dict.c.i

CMakeFiles/redis-example.dir/src/dict.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/dict.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/dict.c -o CMakeFiles/redis-example.dir/src/dict.c.s

CMakeFiles/redis-example.dir/src/zmalloc.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/zmalloc.c.o: ../src/zmalloc.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object CMakeFiles/redis-example.dir/src/zmalloc.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/zmalloc.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/zmalloc.c

CMakeFiles/redis-example.dir/src/zmalloc.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/zmalloc.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/zmalloc.c > CMakeFiles/redis-example.dir/src/zmalloc.c.i

CMakeFiles/redis-example.dir/src/zmalloc.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/zmalloc.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/zmalloc.c -o CMakeFiles/redis-example.dir/src/zmalloc.c.s

CMakeFiles/redis-example.dir/src/adlist.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/adlist.c.o: ../src/adlist.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building C object CMakeFiles/redis-example.dir/src/adlist.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/adlist.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/adlist.c

CMakeFiles/redis-example.dir/src/adlist.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/adlist.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/adlist.c > CMakeFiles/redis-example.dir/src/adlist.c.i

CMakeFiles/redis-example.dir/src/adlist.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/adlist.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/adlist.c -o CMakeFiles/redis-example.dir/src/adlist.c.s

CMakeFiles/redis-example.dir/src/hyperloglog.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/hyperloglog.c.o: ../src/hyperloglog.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building C object CMakeFiles/redis-example.dir/src/hyperloglog.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/hyperloglog.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/hyperloglog.c

CMakeFiles/redis-example.dir/src/hyperloglog.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/hyperloglog.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/hyperloglog.c > CMakeFiles/redis-example.dir/src/hyperloglog.c.i

CMakeFiles/redis-example.dir/src/hyperloglog.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/hyperloglog.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/hyperloglog.c -o CMakeFiles/redis-example.dir/src/hyperloglog.c.s

CMakeFiles/redis-example.dir/src/siphash.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/siphash.c.o: ../src/siphash.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building C object CMakeFiles/redis-example.dir/src/siphash.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/siphash.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/siphash.c

CMakeFiles/redis-example.dir/src/siphash.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/siphash.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/siphash.c > CMakeFiles/redis-example.dir/src/siphash.c.i

CMakeFiles/redis-example.dir/src/siphash.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/siphash.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/siphash.c -o CMakeFiles/redis-example.dir/src/siphash.c.s

CMakeFiles/redis-example.dir/src/test/sdstest.c.o: CMakeFiles/redis-example.dir/flags.make
CMakeFiles/redis-example.dir/src/test/sdstest.c.o: ../src/test/sdstest.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Building C object CMakeFiles/redis-example.dir/src/test/sdstest.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/redis-example.dir/src/test/sdstest.c.o   -c /cygdrive/d/proc/app/redis-6.2.0/src/test/sdstest.c

CMakeFiles/redis-example.dir/src/test/sdstest.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/redis-example.dir/src/test/sdstest.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /cygdrive/d/proc/app/redis-6.2.0/src/test/sdstest.c > CMakeFiles/redis-example.dir/src/test/sdstest.c.i

CMakeFiles/redis-example.dir/src/test/sdstest.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/redis-example.dir/src/test/sdstest.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /cygdrive/d/proc/app/redis-6.2.0/src/test/sdstest.c -o CMakeFiles/redis-example.dir/src/test/sdstest.c.s

# Object files for target redis-example
redis__example_OBJECTS = \
"CMakeFiles/redis-example.dir/src/sds.c.o" \
"CMakeFiles/redis-example.dir/src/dict.c.o" \
"CMakeFiles/redis-example.dir/src/zmalloc.c.o" \
"CMakeFiles/redis-example.dir/src/adlist.c.o" \
"CMakeFiles/redis-example.dir/src/hyperloglog.c.o" \
"CMakeFiles/redis-example.dir/src/siphash.c.o" \
"CMakeFiles/redis-example.dir/src/test/sdstest.c.o"

# External object files for target redis-example
redis__example_EXTERNAL_OBJECTS =

src/redis-example.exe: CMakeFiles/redis-example.dir/src/sds.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/dict.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/zmalloc.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/adlist.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/hyperloglog.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/siphash.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/src/test/sdstest.c.o
src/redis-example.exe: CMakeFiles/redis-example.dir/build.make
src/redis-example.exe: deps/lua/liblua.a
src/redis-example.exe: deps/linenoise/liblinenoise.a
src/redis-example.exe: deps/hiredis/libhiredis.dll.a
src/redis-example.exe: CMakeFiles/redis-example.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_8) "Linking C executable src/redis-example.exe"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/redis-example.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/redis-example.dir/build: src/redis-example.exe

.PHONY : CMakeFiles/redis-example.dir/build

CMakeFiles/redis-example.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/redis-example.dir/cmake_clean.cmake
.PHONY : CMakeFiles/redis-example.dir/clean

CMakeFiles/redis-example.dir/depend:
	cd /cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /cygdrive/d/proc/app/redis-6.2.0 /cygdrive/d/proc/app/redis-6.2.0 /cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug /cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug /cygdrive/d/proc/app/redis-6.2.0/cmake-build-debug/CMakeFiles/redis-example.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/redis-example.dir/depend

