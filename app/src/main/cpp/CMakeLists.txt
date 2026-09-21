cmake_minimum_required(VERSION 3.22.1)

project("aegora-native-sec")

add_library(
    aegora-native-sec
    SHARED
    native-lib.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    aegora-native-sec
    ${log-lib}
)
