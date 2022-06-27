#!/bin/bash
# This file is meant to be included by the parent cppbuild.sh script
if [[ -z "$PLATFORM" ]]; then
    pushd ..
    bash cppbuild.sh "$@" hyperscan
    popd
    exit
fi

HYPERSCAN_VERSION=5.4.0
BOOST=1_74_0
PCRE_VERSION=8.41
RAGEL_VERSION=6.10
echo "Downloading libraries..."
download "https://github.com/intel/hyperscan/archive/v$HYPERSCAN_VERSION.tar.gz" hyperscan-$HYPERSCAN_VERSION.tar.gz
download http://downloads.sourceforge.net/project/boost/boost/${BOOST//_/.}/boost_$BOOST.tar.gz boost_$BOOST.tar.gz
download "https://www.colm.net/files/ragel/ragel-$RAGEL_VERSION.tar.gz" ragel-$RAGEL_VERSION.tar.gz
download "https://sourceforge.net/projects/pcre/files/pcre/8.41/pcre-$PCRE_VERSION.tar.gz" pcre-$PCRE_VERSION.tar.gz

mkdir -p $PLATFORM
cd $PLATFORM
INSTALL_PATH=`pwd`
mkdir -p include lib
mkdir -p include/hs
echo "Decompressing hyperscan..."
tar -xzvf ../hyperscan-$HYPERSCAN_VERSION.tar.gz

echo "Decompressing boost... this could take a while..."
tar --totals -xf ../boost_$BOOST.tar.gz

cd boost_$BOOST
./bootstrap.sh --with-libraries=headers
./b2 headers
cd ..

ln -sf $INSTALL_PATH/boost_$BOOST/boost $INSTALL_PATH/hyperscan-$HYPERSCAN_VERSION/include/boost

echo "Decompressing Ragel..."
tar -xzvf ../ragel-$RAGEL_VERSION.tar.gz
cd ragel-$RAGEL_VERSION
./configure --prefix="$INSTALL_PATH/.."
make -j $MAKEJ
make install
cd ..

echo "Decompressing PCRE..."
tar -xzvf ../pcre-$PCRE_VERSION.tar.gz
mv pcre-$PCRE_VERSION hyperscan-$HYPERSCAN_VERSION

cd hyperscan-$HYPERSCAN_VERSION

case $PLATFORM in
    linux-x86_64)
        CFLAGS='-O -fPIC' CC="gcc" CXX="g++ -std=c++11 -m64 -fPIC" "$CMAKE" -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=$INSTALL_PATH -DCMAKE_INSTALL_LIBDIR="lib" -DBUILD_STATIC_AND_SHARED=ON -DPCRE_SOURCE="pcre-$PCRE_VERSION" .
        make -j $MAKEJ
        make install/strip
        ;;
    macosx-x86_64)
        "$CMAKE" -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=$INSTALL_PATH -DCMAKE_INSTALL_LIBDIR="lib" -DARCH_OPT_FLAGS='-Wno-error' -DBUILD_STATIC_AND_SHARED=ON -DPCRE_SOURCE="pcre-$PCRE_VERSION" .
        make -j $MAKEJ
        make install/strip
        ;;
    windows-x86_64)
        export CC="cl.exe"
        export CXX="cl.exe"
        CXXFLAGS="/Wv:17" "$CMAKE" -G "Ninja" -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX="$INSTALL_PATH" -DCMAKE_INSTALL_LIBDIR="lib" -DARCH_OPT_FLAGS='' -DBUILD_STATIC_AND_SHARED=ON -DPCRE_SOURCE="pcre-$PCRE_VERSION" .
        ninja -j $MAKEJ
        cp -r src/* $INSTALL_PATH/include/hs/
        cp lib/*.lib $INSTALL_PATH/lib
        ;;
    *)
        echo "Error: Platform \"$PLATFORM\" is not supported"
        ;;
esac

cd ../..
